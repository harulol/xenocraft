package dev.hawu.plugins.xenocraft
package events

import data.User
import events.UserEvent.handlers

import org.bukkit.entity.Player
import org.bukkit.event.{Event, HandlerList}

/** Represents an event that involves a user.
  */
trait UserEvent(val user: User) extends Event:

  override def getHandlers = handlers

/** Object for [[UserEvent]].
  */
object UserEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
