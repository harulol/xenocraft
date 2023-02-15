package dev.hawu.plugins.xenocraft
package events.arts

import data.{ArtType, User}
import events.UserEvent
import events.arts.PlayerEquipArtEvent.handlers

import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.{Cancellable, Event, HandlerList}

/** Represents an event that is called when an art of [[art]] is being equipped to the player.
  */
//noinspection DuplicatedCode
class PlayerEquipArtEvent(user: User, val art: ArtType, val master: Boolean) extends Event with UserEvent(user) with Cancellable:

  private var cancelled = false

  override def isCancelled: Boolean = cancelled

  override def setCancelled(cancel: Boolean): Unit = cancelled = cancel

  override def getHandlers: HandlerList = handlers

/** Object companion for [[PlayerEquipArtEvent]].
  */
object PlayerEquipArtEvent:

  private val handlers = HandlerList()

  def getHandlerList: HandlerList = handlers
