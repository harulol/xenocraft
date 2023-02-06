package dev.hawu.plugins.xenocraft
package combat

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.api.events.Events
import dev.hawu.plugins.xenocraft.UserMap.{save, user}
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.event.entity.*
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.{Bukkit, EntityEffect}
import java.util.UUID
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import dev.hawu.plugins.xenocraft.I18n.tl
import org.bukkit.event.EventPriority
import dev.hawu.plugins.xenocraft.events.PlayerDealDamageEvent
import scala.collection.mutable.ArrayBuffer
import dev.hawu.plugins.xenocraft.data.Directional
import dev.hawu.plugins.api.MathUtils
import org.bukkit.GameMode
import dev.hawu.plugins.xenocraft.events.EnemyDamagePlayerEvent
import dev.hawu.plugins.xenocraft.events.PlayerIncapitateEvent
import org.bukkit.Effect
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.event.player.PlayerInteractEvent

/** The singleton object dedicated to listening for events related to fields.
  */
object BattlefieldListener extends Listener:

  private val aggro = mutable.Map.empty[UUID, UUID] // entity -> player
  private var plugin: Option[JavaPlugin] = None

  /** Initializes the battlefield listener.
    *
    * @param pl
    *   the plugin to initialize with.
    */
  def initialize(pl: JavaPlugin): Unit =
    Events.registerEvents(pl, this)
    plugin = Some(pl)

    // The task to heal a player anytime they aren't in battle every second.
    Tasks.run(() =>
      Bukkit.getOnlinePlayers.asScala.filterNot(isInBattle).flatMap(_.user).filterNot(_.bladeUnsheathed)
        .filter(user => user.hp.intValue < user.maxHp.intValue)
        .foreach(user => CombatManager.heal(user.player.get, math.floor(user.maxHp * 0.1))),
    ).delay(0).period(20).plugin(pl).run()

    // The task to draw an aggro line whenever an entity targets a player.
    Tasks.run { () =>
      aggro.foreach((entity, p) => {
        val mob = Bukkit.getEntity(entity)
        if mob == null || mob.isDead() || !mob.isInstanceOf[Mob] || Bukkit.getPlayer(p) == null then
          aggro.remove(entity)
        else
          val mobInstance = mob.asInstanceOf[Mob]
          if mobInstance.getTarget() == null then
            aggro.remove(entity)
            BossbarManager.makeBar(CombatManager.makeEnemy(mobInstance)).removePlayer(Bukkit.getPlayer(p))
            Option(p).map(Bukkit.getPlayer).flatMap(_.user).foreach(_.sheathe())
      })

      // Auto-sheathe
      val values = aggro.values.toSet
      Bukkit.getOnlinePlayers.asScala.filter(_ != null).filterNot(isInBattle).flatMap(_.user).foreach(_.sheathe())

      aggro.map[LivingEntity, Player](entry =>
        Bukkit.getEntity(entry._1).asInstanceOf[LivingEntity] -> Bukkit.getPlayer(entry._2),
      ).filter(tup => tup._2 != null && tup._2.isOnline && tup._1 != null && !tup._1.isDead)
        .foreach(entry => CombatManager.drawAggroLine(entry._1, entry._2))
    }.delay(0).period(5).async(true).plugin(pl).run()

  end initialize

  /** Checks if a player is in battle.
    *
    * @param player
    *   the player to check.
    * @return
    *   true if the player is in battle, false otherwise.
    */
  def isInBattle(player: Player): Boolean = aggro.values.toList.contains(player.getUniqueId)

  @EventHandler
  private def onDamage(event: EntityDamageEvent): Unit = event.getEntity match
    case player: Player =>
      event.getCause match
        case DamageCause.CUSTOM | DamageCause.ENTITY_ATTACK => return ()
        case _                                              => event.setCancelled(true)
      val user = player.user.get
      val percentage = event.getFinalDamage / player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue
      CombatManager.damage(player, user.maxHp * percentage, true)
      player.playEffect(EntityEffect.HURT)
      player.setNoDamageTicks(20)
    case mob: Mob => if CombatManager.isEnemy(mob) then event.setCancelled(event.getCause() != DamageCause.CUSTOM)
    case _        => ()

  @EventHandler
  private def onDamage(event: EntityDamageByEntityEvent): Unit = event.getDamager match
    case player: Player =>
      val user = player.user.get
      if !user.bladeUnsheathed then
        event.getEntity match
          case mob: Mob => if mob != null && !mob.isInstanceOf[Animals] then
              user.unsheathe()
              mob.setTarget(player)
              BossbarManager.updateBossbar(mob).foreach(_.addPlayer(player))
              event.setCancelled(true)
              aggro.put(mob.getUniqueId(), player.getUniqueId())
          case _ => ()
      else
        event.getEntity match
          case mob: Mob => if mob != null && !mob.isInstanceOf[Animals] then
              mob.setTarget(player)
              aggro.put(mob.getUniqueId(), player.getUniqueId())

              val enemy = CombatManager.makeEnemy(mob)
              val e = PlayerDealDamageEvent(player, calculateDirection(player, mob), enemy, true)
              e.shouldOverride = false
              Bukkit.getPluginManager().callEvent(e)

              // Leave handling to vanilla.
              if e.isCancelled then return

              // A successful hit has to be a hit that evaded or missed.
              event.setCancelled(true)
              if !e.isEvaded && e.isHit then enemy.setHp(enemy.hp - e.finalDamage)
          case _ => ()
      end if
    case mob: Mob =>
      if !event.getEntity().isInstanceOf[Player] then return
      val player = event.getEntity().asInstanceOf[Player]
      val enemy = CombatManager.makeEnemy(mob)

      val e = EnemyDamagePlayerEvent(mob, enemy, player)
      e.shouldOverride = false
      Bukkit.getPluginManager().callEvent(e)

      if e.isCancelled() then return
      else
        event.setCancelled(true)
        if !e.isEvaded && e.isHit then CombatManager.damage(player, e.finalDamage)
    case _ => ()

  /** Calculates the direction of the target relative to the player.
    *
    * @param player
    *   the player
    * @param target
    *   the target
    * @return
    *   the directiond
    */
  def calculateDirection(player: Player, target: Mob): Directional =
    val direction = player.getLocation().getDirection().setY(0)
    val enemyDirection = target.getLocation().getDirection().setY(0)

    val angle = enemyDirection.angle(direction)
    if angle <= 0.785398 then Directional.BACK // radians for 45 degrees, ranges from 45 -> 0, 0 -> -45
    else if angle >= 2.35619 then Directional.FRONT // radians for 135 degrees, ranges from 135 -> 180 and 180 -> -135
    else
      val left = MathUtils.getLeftUnit(enemyDirection).angle(direction)
      val right = MathUtils.getRightUnit(enemyDirection).angle(direction)
      if left < right then Directional.LEFT else Directional.RIGHT

  @EventHandler
  private def onCombust(event: EntityCombustEvent): Unit = event.setCancelled(true)

  @EventHandler
  private def onTransform(event: EntityTransformEvent): Unit =
    if event.getEntity().isInstanceOf[Mob] then CombatManager.clearEnemy(event.getEntity().asInstanceOf[Mob])
    BossbarManager.clear(event.getEntity())

  @EventHandler
  private def onTarget(event: EntityTargetLivingEntityEvent): Unit =
    if event.getTarget.isInstanceOf[Player] && event.getEntity.isInstanceOf[LivingEntity] then
      aggro.put(event.getEntity.getUniqueId, event.getTarget.getUniqueId)
      event.getTarget().asInstanceOf[Player].closeInventory()

  @EventHandler
  private def onItemUse(event: PlayerInteractEvent): Unit =
    if event.getPlayer().user.exists(_.bladeUnsheathed) || isInBattle(event.getPlayer()) then event.setCancelled(true)

  @EventHandler
  private def onIncapitate(event: PlayerIncapitateEvent): Unit =
    aggro.filter(_._2 == event.getPlayer().getUniqueId()).map((uuid, _) => Bukkit.getEntity(uuid).asInstanceOf[Mob])
      .foreach(mob => {
        mob.setTarget(null)
        val bar = BossbarManager.makeBar(CombatManager.makeEnemy(mob))
        bar.removePlayer(event.getPlayer())
      })
    aggro.filterInPlace((_, v) => v != event.getPlayer.getUniqueId)
    Tasks.run { _ =>
      val loc = event.getPlayer().getLocation().add(0.0, 1.0, 0.0)
      loc.getWorld().spawnParticle(Particle.TOTEM, loc, 100, 0.7, 0.7, 0.7, 0.1)
      event.getPlayer().playSound(loc, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
    }.delay(2).run()

  @EventHandler
  private def onDeath(event: EntityDeathEvent): Unit = event.getEntity match
    case player: Player => aggro.filterInPlace((_, v) => v != player.getUniqueId)
    case entity: Mob =>
      aggro.remove(entity.getUniqueId)
      BossbarManager.clear(entity)
    case _ => ()

  @EventHandler
  private def onExplode(event: EntityExplodeEvent): Unit =
    aggro.remove(event.getEntity.getUniqueId)
    BossbarManager.clear(event.getEntity)

  @EventHandler
  private def onQuit(event: PlayerQuitEvent): Unit = aggro.filterInPlace((_, v) => event.getPlayer.getUniqueId != v)

  @EventHandler
  private def onRegen(event: EntityRegainHealthEvent): Unit =
    // Only healing allowed is during non-battle or using healing arts.
    if event.getRegainReason != RegainReason.CUSTOM then event.setCancelled(true) // Cancel all natural healing ways

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private def onPlayerDamageEntity(event: PlayerDealDamageEvent): Unit =
    val holo =
      if event.isEvaded then Hologram(event.entity.entity.getEyeLocation, ArrayBuffer(s"&fEvaded!"), 40)
      else if !event.isHit then Hologram(event.entity.entity.getEyeLocation, ArrayBuffer(s"&fMissed!"), 40)
      else if event.isPreemptive then
        Hologram(event.entity.entity.getEyeLocation, ArrayBuffer(s"&e&l${event.finalDamage.round}"), 40)
      else if event.isBlocked then
        Hologram(event.entity.entity.getEyeLocation, ArrayBuffer(s"&7&l${event.finalDamage.round}"), 40)
      else if event.isCritical then
        Hologram(event.entity.entity.getEyeLocation, ArrayBuffer(s"&b&l${event.finalDamage.round}"), 40)
      else Hologram(event.entity.entity.getEyeLocation, ArrayBuffer(s"&f&l${event.finalDamage.round}"), 40)
    holo.nudgeLocation()
    holo.spawn()

  @EventHandler
  def onEntityDamagePlayer(event: EnemyDamagePlayerEvent): Unit =
    val holo =
      if event.isEvaded then Hologram(event.player.getEyeLocation, ArrayBuffer(s"&fEvaded!"), 40)
      else if !event.isHit then Hologram(event.player.getEyeLocation, ArrayBuffer(s"&fMissed!"), 40)
      else if event.isBlocked then
        Hologram(event.player.getEyeLocation, ArrayBuffer(s"&7${event.finalDamage.round}"), 40)
      else if event.isCritical then
        Hologram(event.player.getEyeLocation, ArrayBuffer(s"&b${event.finalDamage.round}"), 40)
      else Hologram(event.player.getEyeLocation, ArrayBuffer(s"&c${event.finalDamage.round}"), 40)
    holo.nudgeLocation()
    holo.spawn()

end BattlefieldListener
