package dev.hawu.plugins.xenocraft
package combat

import UserMap.user
import events.{EnemyDamagePlayerEvent, PlayerDealDamageEvent}

import org.bukkit.event.{EventHandler, EventPriority, Listener}

import java.util.UUID
import scala.collection.mutable

/** Represents a singleton object that restricts it so player won't rack up damage too fast.
 */
object CooldownsListener extends Listener:

  private val playerCooldowns = mutable.Map.empty[UUID, Long]
  private val entityCooldowns = mutable.Map.empty[UUID, Long]

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  private def onPlayerDamage(event: PlayerDealDamageEvent): Unit =
    if event.shouldOverride then return

    val cooldown = playerCooldowns.getOrElse(event.getPlayer.getUniqueId, 0L)
    if cooldown < System.currentTimeMillis() then
      val boost = 1 / event.getPlayer.user.get.rechargeSpeed
      playerCooldowns.put(event.getPlayer.getUniqueId, System.currentTimeMillis() + (1000 * boost).round)
    else event.setCancelled(true)

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  private def onEntityDamage(event: EnemyDamagePlayerEvent): Unit =
    if event.shouldOverride then return

    val cooldown = entityCooldowns.getOrElse(event.getEntity.getUniqueId, 0L)
    if cooldown < System.currentTimeMillis() then
      val boost = 1 / event.enemy.rechargeSpeed
      entityCooldowns.put(event.getEntity.getUniqueId, System.currentTimeMillis() + (1000 * boost).round)
    else event.setCancelled(true)

end CooldownsListener
