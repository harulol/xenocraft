package dev.hawu.plugins.xenocraft
package listener

import UserMap.user
import events.combat.{EnemyAutoAttackEvent, PlayerAutoAttackEvent}
import events.{EntityHealthChangeEvent, PlayerIncapacitateEvent}
import utils.Hologram

import dev.hawu.plugins.api.Tasks
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason
import org.bukkit.event.{EventHandler, EventPriority, Listener}
import org.bukkit.{Bukkit, Particle, Sound}

import java.util.UUID
import scala.collection.mutable

/** Singleton object for listening to events combat-related.
  */
object CombatListener extends Listener:

  private val playerCooldowns = mutable.Map.empty[UUID, Long]
  private val entityCooldowns = mutable.Map.empty[UUID, Long]

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

  @EventHandler
  private def onIncapacitate(event: PlayerIncapacitateEvent): Unit = Tasks.run { _ =>
    val loc = event.getPlayer.getLocation().add(0.0, 1.0, 0.0)
    loc.getWorld.spawnParticle(Particle.TOTEM, loc, 100, 0.7, 0.7, 0.7, 0.1)
    event.getPlayer.playSound(loc, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
  }.plugin(Xenocraft.getInstance).run()

  @EventHandler
  private def onRegen(event: EntityRegainHealthEvent): Unit =
    // Only healing allowed is during non-battle or using healing arts.
    if event.getRegainReason != RegainReason.CUSTOM then event.setCancelled(true) // Cancel all natural healing ways
