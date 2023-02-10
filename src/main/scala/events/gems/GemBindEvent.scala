package dev.hawu.plugins.xenocraft
package events.gems

import data.{GemType, User}
import events.UserEvent
import events.gems.GemBindEvent.handlers

import org.bukkit.event.{Cancellable, HandlerList}

/** Called when a user is about to bind the gem to a certain slot.
  */
class GemBindEvent(who: User, var slot: Int, var gem: GemType, var level: Int) extends UserEvent(who) with Cancellable:

  private var cancelled = false

  override def getHandlers = handlers

  override def isCancelled = cancelled

  override def setCancelled(cancel: Boolean) = cancelled = cancel

/** Companion object for [[GemBindEvent]].
  */
object GemBindEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
