package dev.hawu.plugins.xenocraft
package events

import org.bukkit.entity.Player
import org.bukkit.event.{Event, HandlerList}
import org.bukkit.event.player.PlayerEvent

/** An event called when the player unsheathes their blade.
  */
class PlayerUnsheatheEvent(player: Player) extends PlayerEvent(player):
  override def getHandlers: HandlerList = PlayerUnsheatheEvent.handlers

/** Companion object for [[PlayerUnsheatheEvent]].
  */
object PlayerUnsheatheEvent:
  private val handlers = HandlerList()

  def getHandlerList: HandlerList = handlers
