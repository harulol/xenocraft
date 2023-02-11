package dev.hawu.plugins.xenocraft
package events.blades

import UserMap.user
import events.UserEvent
import events.blades.PlayerPostSheatheEvent.handlers

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/** An event that is called after sheathing has successfully happened.
  *
  * If [[PlayerPostSheatheEvent]] was cancelled, this will never be called.
  */
class PlayerPostSheatheEvent(who: Player) extends PlayerEvent(who) with UserEvent(who.user.get):

  override def getHandlers = handlers

/** Object companion for [[PlayerPostSheatheEvent]].
  */
object PlayerPostSheatheEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
