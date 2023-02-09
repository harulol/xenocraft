package dev.hawu.plugins.xenocraft
package listener

import events.PlayerIncapacitateEvent
import managers.AggroManager

import org.bukkit.entity.{Mob, Player}
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.event.{EventHandler, EventPriority, Listener}

/** Represents the listener for [[managers.AggroManager]].
  */
object AggroListener extends Listener:

  @EventHandler
  private def onTarget(event: EntityTargetLivingEntityEvent): Unit = event.getEntity match
    case mob: Mob if event.getTarget.isInstanceOf[Player] =>
      AggroManager.clear(mob)
      AggroManager.map(mob, event.getTarget.asInstanceOf[Player])
    case _ =>

  @EventHandler
  private def onIncapacitate(event: PlayerIncapacitateEvent): Unit = AggroManager.clearAll(event.getPlayer)
