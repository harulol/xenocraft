package dev.hawu.plugins.xenocraft
package events.gems

import UserMap.user
import data.User
import events.UserEvent
import events.gems.GemEffectApplyEvent.handlers

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.{Cancellable, HandlerList}

/** Called when a gem effect is applied to the player.
  */
class GemEffectApplyEvent(user: User) extends UserEvent(user) with Cancellable:

  private var cancelled = false

  override def getHandlers = handlers

  override def isCancelled = cancelled

  override def setCancelled(cancel: Boolean) = cancelled = cancel

/** Companion object for [[GemEffectApplyEvent]].
  */
object GemEffectApplyEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
