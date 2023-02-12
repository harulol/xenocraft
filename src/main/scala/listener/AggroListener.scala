package dev.hawu.plugins.xenocraft
package listener

import UserMap.user
import events.PlayerIncapacitateEvent
import managers.{AggroManager, EnemyManager}

import org.bukkit.entity.{Mob, Player}
import org.bukkit.event.entity.{EntityDeathEvent, EntityExplodeEvent, EntityTargetLivingEntityEvent}
import org.bukkit.event.player.{PlayerInteractEvent, PlayerQuitEvent}
import org.bukkit.event.{EventHandler, EventPriority, Listener}

import scala.util.Try

/** Represents the listener for [[managers.AggroManager]].
 */
object AggroListener extends Listener:

  @EventHandler
  private def onTarget(event: EntityTargetLivingEntityEvent): Unit = event.getEntity match
    case mob: Mob if event.getTarget.isInstanceOf[Player] =>
      AggroManager.map(mob, event.getTarget.asInstanceOf[Player])
      event.getTarget.asInstanceOf[Player].closeInventory()
    case _ =>

  @EventHandler
  private def onQuit(event: PlayerQuitEvent): Unit = AggroManager.clearAll(event.getPlayer)

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private def onEntityDeath(event: EntityDeathEvent): Unit = Try(event.getEntity.asInstanceOf[Mob]).foreach(AggroManager.clear)

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private def onEntityExplode(event: EntityExplodeEvent): Unit = Try(event.getEntity.asInstanceOf[Mob]).foreach(AggroManager.clear)

  @EventHandler
  private def onItemUse(event: PlayerInteractEvent): Unit =
    if event.getPlayer.user.exists(_.bladeUnsheathed) || AggroManager.isBeingTargeted(event.getPlayer) then event.setCancelled(true)

  @EventHandler(priority = EventPriority.MONITOR)
  private def onIncapacitate(event: PlayerIncapacitateEvent): Unit =
    AggroManager.getFor(event.getPlayer).foreach(mob =>
      mob.setTarget(null)
      EnemyManager.getBossbar(mob).foreach(_.removePlayer(event.getPlayer)),
    )
    AggroManager.clearAll(event.getPlayer)
