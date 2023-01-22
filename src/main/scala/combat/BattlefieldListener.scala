package dev.hawu.plugins.xenocraft
package combat

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.api.events.Events
import dev.hawu.plugins.xenocraft.UserMap.{save, user}
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason
import org.bukkit.event.entity.{EntityDamageEvent, EntityDeathEvent, EntityRegainHealthEvent, EntityTargetLivingEntityEvent}
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.plugin.java.JavaPlugin

import java.util.UUID
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

/**
 * The singleton object dedicated to listening for events
 * related to fields.
 */
object BattlefieldListener extends Listener:

  private val aggro = mutable.Map.empty[UUID, UUID] // entity -> player
  private var plugin: Option[JavaPlugin] = None

  /**
   * Initializes the battlefield listener.
   *
   * @param pl the plugin to initialize with.
   */
  def initialize(pl: JavaPlugin): Unit =
    Events.registerEvents(pl, this)
    plugin = Some(pl)

    // The task to heal a player anytime they aren't in battle every second.
    Tasks.run(() =>
      Bukkit.getOnlinePlayers.asScala
        .flatMap(_.user)
        .filterNot(user => aggro.values.toList.contains(user.uuid))
        .filterNot(_.bladeUnsheathed)
        .filter(user => user.hp.intValue < user.maxHp.intValue)
        .foreach(user => CombatManager.heal(user.player.get, math.floor(user.maxHp * 0.1)))
    ).delay(0).period(20).plugin(pl).run()

    // The task to draw an aggro line whenever an entity targets a player.
    Tasks.run { () =>
      aggro.filterInPlace((entity, p) => {
        val mob = Bukkit.getEntity(entity)
        mob != null && mob.asInstanceOf[Mob].getTarget != null && mob.asInstanceOf[Mob].getTarget.getUniqueId == p && Bukkit.getPlayer(p) != null
      })

      aggro.map[LivingEntity, Player](entry => Bukkit.getEntity(entry._1).asInstanceOf[LivingEntity] -> Bukkit.getPlayer(entry._2))
        .filter(tup => tup._2 != null && tup._2.isOnline && tup._1 != null && !tup._1.isDead)
        .foreach(entry => CombatManager.drawAggroLine(entry._1, entry._2))
    }.delay(0).period(2).async(true).plugin(pl).run()

  @EventHandler
  private def onDamage(event: EntityDamageEvent): Unit =
    event.getEntity match
      case player: Player =>
        if event.getCause != DamageCause.CUSTOM then
          event.setCancelled(true)
        val user = player.user.get
        val percentage = event.getFinalDamage / player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue
        CombatManager.damage(player, user.maxHp * percentage)
      case _ => ()

  @EventHandler
  private def onTarget(event: EntityTargetLivingEntityEvent): Unit =
    if event.getTarget.isInstanceOf[Player] && event.getEntity.isInstanceOf[LivingEntity] then
      aggro.put(event.getEntity.getUniqueId, event.getTarget.getUniqueId)

  @EventHandler
  private def onDeath(event: EntityDeathEvent): Unit =
    event.getEntity match
      case player: Player => aggro.filterInPlace((_, v) => v != player.getUniqueId)
      case entity: LivingEntity => aggro.remove(entity.getUniqueId)
      case null => ()

  @EventHandler
  private def onQuit(event: PlayerQuitEvent): Unit =
    aggro.filterInPlace((_, v) => event.getPlayer.getUniqueId != v)

  @EventHandler
  private def onRegen(event: EntityRegainHealthEvent): Unit =
  // Only healing allowed is during non-battle or using healing arts.
    if event.getRegainReason != RegainReason.CUSTOM then
      event.setCancelled(true) // Cancel all natural healing ways
