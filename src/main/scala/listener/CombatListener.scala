package dev.hawu.plugins.xenocraft
package listener

import UserMap.{setUp, user}
import events.arts.{PlayerUseArtEvent, PlayerUseFusionArtEvent}
import events.blades.PlayerPostSheatheEvent
import events.combat.{EnemyAutoAttackEvent, PlayerAutoAttackEvent}
import events.{EntityHealthChangeEvent, PlayerIncapacitateEvent}
import utils.Hologram

import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.{Strings, Tasks}
import org.bukkit.*
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.{LivingEntity, Player}
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason
import org.bukkit.event.{EventHandler, EventPriority, Listener}

import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import scala.collection.mutable

/** Singleton object for listening to events combat-related.
  */
object CombatListener extends Listener:

  private val playerCooldowns = mutable.Map.empty[UUID, Long]
  private val entityCooldowns = mutable.Map.empty[UUID, Long]

  @EventHandler
  private def onSheathe(event: PlayerPostSheatheEvent): Unit = event.getPlayer.user.foreach(_.reset())

  @EventHandler(priority = EventPriority.LOWEST)
  private def onPlayerAuto(event: PlayerAutoAttackEvent): Unit =
    val cooldown = playerCooldowns.getOrElse(event.getPlayer.getUniqueId, 0L)
    if cooldown < System.currentTimeMillis() then
      val boost = 1 / event.getPlayer.user.get.rechargeSpeed
      playerCooldowns.put(event.getPlayer.getUniqueId, System.currentTimeMillis() + (1000 * boost).round)
    else event.setCancelled(true)

  @EventHandler(priority = EventPriority.LOWEST)
  private def onEntityAuto(event: EnemyAutoAttackEvent): Unit =
    val cooldown = entityCooldowns.getOrElse(event.getEntity.getUniqueId, 0L)
    if cooldown < System.currentTimeMillis() then
      val boost = 1 / event.enemy.rechargeSpeed
      entityCooldowns.put(event.getEntity.getUniqueId, System.currentTimeMillis() + (1000 * boost).round)
    else event.setCancelled(true)

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private def onArtUse(event: PlayerUseArtEvent): Unit =
    val locale = UserAdapter.getAdapter.getUser(event.getPlayer).getLocale
    event.getPlayer.playSound(event.getPlayer.getLocation, Sound.ITEM_FLINTANDSTEEL_USE, 1, 1)
    event.getPlayer.sendTitle(" ", Strings.color(s"&b${event.art.name(locale)}"), 0, 40, 0)

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private def onFusionArtUse(event: PlayerUseFusionArtEvent): Unit =
    val locale = UserAdapter.getAdapter.getUser(event.getPlayer).getLocale
    event.getPlayer.playSound(event.getPlayer.getLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1)
    animateFrom(event.getPlayer)
    event.getPlayer.sendTitle(" ", Strings.color(s"&b&l${event.classArt.name(locale)} &7| &b&l${event.fusionArt.name(locale)}"), 0, 40, 0)

  private def animateFrom(player: Player): Unit = for _ <- 1 to 10 do
    val unscaled = player.getEyeLocation.add(randomGaussian, randomGaussian, randomGaussian)
    val direction = player.getEyeLocation.subtract(unscaled).toVector.normalize()
    val destination = player.getEyeLocation.add(direction.multiply(3))
    drawFusionLine(player.getEyeLocation, destination)

  private def drawFusionLine(from: Location, destination: Location): Unit =
    val step = destination.toVector.subtract(from.toVector).normalize().multiply(0.2)
    while from.distanceSquared(destination) >= 0.1 do
      val red = ThreadLocalRandom.current().nextInt(245, 256)
      val green = ThreadLocalRandom.current().nextInt(170, 180)
      val blue = ThreadLocalRandom.current().nextInt(60, 70)

      val dustOptions = DustOptions(Color.fromRGB(red, green, blue), 1)
      from.getWorld.spawnParticle(Particle.REDSTONE, from, 1, 0, 0, 0, dustOptions)
      from.add(step)

  private def randomGaussian: Double =
    if ThreadLocalRandom.current().nextBoolean() then ThreadLocalRandom.current().nextGaussian()
    else -ThreadLocalRandom.current().nextGaussian()

  @EventHandler
  private def onIncapacitate(event: PlayerIncapacitateEvent): Unit =
    event.getPlayer.teleport(event.getPlayer.getWorld.getSpawnLocation)
    Tasks.run { _ =>
      val loc = event.getPlayer.getLocation().add(0.0, 1.0, 0.0)
      loc.getWorld.spawnParticle(Particle.TOTEM, loc, 100, 0.7, 0.7, 0.7, 0.1)
      event.getPlayer.playSound(loc, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
    }.plugin(Xenocraft.getInstance).run()

  @EventHandler
  private def onRegen(event: EntityRegainHealthEvent): Unit =
    // Only healing allowed is during non-battle or using healing arts.
    if event.getRegainReason != RegainReason.CUSTOM then event.setCancelled(true) // Cancel all natural healing ways
