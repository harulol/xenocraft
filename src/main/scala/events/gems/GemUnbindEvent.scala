package dev.hawu.plugins.xenocraft
package events.gems

import data.{GemType, User}
import events.UserEvent
import events.gems.GemUnbindEvent.handlers

import org.bukkit.event.{Cancellable, HandlerList}

/** Called when a user is about to unbind the gem from a certain slot.
 */
class GemUnbindEvent(who: User, var slot: Int) extends UserEvent(who) with Cancellable:

  private var cancelled = false

  override def getHandlers = handlers

  override def isCancelled = cancelled

  override def setCancelled(cancel: Boolean) = cancelled = cancel

/** Companion object for [[GemUnbindEvent]].
 */
object GemUnbindEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
