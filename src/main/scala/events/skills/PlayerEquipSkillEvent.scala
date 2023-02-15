package dev.hawu.plugins.xenocraft
package events.skills

import data.{SkillType, User}
import events.UserEvent
import events.skills.PlayerEquipSkillEvent.handlers

import org.bukkit.event.{Cancellable, Event, HandlerList}

/** Represents an event that is called when a skill of [[skill]] is being equipped to the player.
  */
//noinspection DuplicatedCode
class PlayerEquipSkillEvent(user: User, val skill: SkillType) extends Event with UserEvent(user) with Cancellable:

  private var cancelled = false

  override def isCancelled: Boolean = cancelled

  override def setCancelled(cancel: Boolean): Unit = cancelled = cancel

  override def getHandlers: HandlerList = handlers

/** Object companion for [[PlayerEquipSkillEvent]].
  */
object PlayerEquipSkillEvent:

  private val handlers = HandlerList()

  def getHandlerList: HandlerList = handlers
