package dev.hawu.plugins.xenocraft
package listener

import events.PlayerIncapacitateEvent
import events.blades.PlayerPostSheatheEvent
import managers.StancesManager

import org.bukkit.event.{EventHandler, Listener}

/** Singleton listener for handling events for [[StancesManager]].
  */
object StancesListener extends Listener:

  @EventHandler
  private def onSheathe(event: PlayerPostSheatheEvent): Unit = StancesManager.clearStance(event.getPlayer)

  @EventHandler
  private def onIncapacitate(event: PlayerIncapacitateEvent): Unit = StancesManager.clearStance(event.getPlayer)
