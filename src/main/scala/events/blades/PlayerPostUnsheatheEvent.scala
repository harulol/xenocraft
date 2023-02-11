package dev.hawu.plugins.xenocraft
package events.blades

import UserMap.user
import events.UserEvent
import events.blades.PlayerPostUnsheatheEvent.handlers

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/** An event that is called after unsheathing has successfully happened.
  *
  * If [[PlayerPreSheatheEvent]] was cancelled, this will never be called.
  */
class PlayerPostUnsheatheEvent(who: Player) extends PlayerEvent(who) with UserEvent(who.user.get):

  override def getHandlers = handlers

/** Object companion for [[PlayerPostUnsheatheEvent]].
  */
object PlayerPostUnsheatheEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
