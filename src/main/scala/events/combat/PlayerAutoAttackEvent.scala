package dev.hawu.plugins.xenocraft
package events.combat

import UserMap.user
import data.EnemyEntity
import events.UserEvent
import events.combat.PlayerAutoAttackEvent.handlers

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.{Cancellable, HandlerList}

/** This event is called when the player attempts to auto attack an enemy.
  */
class PlayerAutoAttackEvent(who: Player, val enemy: EnemyEntity) extends PlayerEvent(who) with UserEvent(who.user.get) with Cancellable:

  private var cancelled = false

  override def isCancelled = cancelled

  override def setCancelled(cancel: Boolean) = cancelled = cancel

  override def getHandlers = handlers

/** Object singleton for [[PlayerAutoAttackEvent]]
  */
object PlayerAutoAttackEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
