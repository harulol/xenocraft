package dev.hawu.plugins.xenocraft
package events.gems

import data.User
import events.UserEvent
import events.gems.GemEffectUnapplyEvent.handlers

import org.bukkit.event.{Cancellable, HandlerList}

class GemEffectUnapplyEvent(user: User) extends UserEvent(user) with Cancellable:

  private var cancelled = false

  override def getHandlers = handlers

  override def isCancelled = cancelled

  override def setCancelled(cancel: Boolean) = cancelled = cancel

/** Companion object for [[GemEffectUnapplyEvent]].
 */
object GemEffectUnapplyEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
