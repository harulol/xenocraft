package dev.hawu.plugins.xenocraft
package events.blades

import UserMap.user
import events.UserEvent
import events.blades.PlayerPreUnsheatheEvent.handlers

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.{Cancellable, HandlerList}

/** An event that is called momentarily before the main unsheathing logic happens.
  */
class PlayerPreUnsheatheEvent(who: Player) extends PlayerEvent(who) with UserEvent(who.user.get) with Cancellable:

  private var cancelled = false

  override def getHandlers = handlers

  override def isCancelled = cancelled

  override def setCancelled(cancel: Boolean) = cancelled = cancel

/** Companion object for [[PlayerPreSheatheEvent]].
  */
object PlayerPreUnsheatheEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
