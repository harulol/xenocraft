package dev.hawu.plugins.xenocraft
package events.arts

import UserMap.user
import data.ArtType
import events.UserEvent
import events.arts.PlayerUseArtEvent.handlers

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.{Cancellable, HandlerList}

/** An event that is called when the player uses a single art, whether it is a class art or a master art. This is merely an event for
  * watchers, you shouldn't change it in anyway.
  */
class PlayerUseArtEvent(who: Player, val art: ArtType) extends PlayerEvent(who) with UserEvent(who.user.get) with Cancellable:

  private var cancelled = false

  override def isCancelled: Boolean = cancelled

  override def setCancelled(cancel: Boolean): Unit = cancelled = cancel

  override def getHandlers = handlers

/** Companion object for [[PlayerUseArtEvent]].
  */
object PlayerUseArtEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
