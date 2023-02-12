package dev.hawu.plugins.xenocraft
package events

import data.Attributable
import events.EntityHealthChangeEvent.handlers

import org.bukkit.event.{Cancellable, Event, HandlerList}

/** Called when an attributable has its health changed.
  */
class EntityHealthChangeEvent(val attributable: Attributable, val oldHp: Double, var newHp: Double) extends Event with Cancellable:

  private var cancelled = false

  def isCancelled = cancelled

  def setCancelled(cancel: Boolean) = cancelled = cancel

  override def getHandlers = handlers

object EntityHealthChangeEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
