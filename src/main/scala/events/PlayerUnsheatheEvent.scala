package dev.hawu.plugins.xenocraft
package events

import UserMap.user

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.{Event, HandlerList}

/** An event called when the player unsheathes their blade.
  */
class PlayerUnsheatheEvent(player: Player) extends PlayerEvent(player) with UserEvent(player.user.get):
  override def getHandlers: HandlerList = PlayerUnsheatheEvent.handlers

/** Companion object for [[PlayerUnsheatheEvent]].
  */
object PlayerUnsheatheEvent:
  private val handlers = HandlerList()

  def getHandlerList: HandlerList = handlers
