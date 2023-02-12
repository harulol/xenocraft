package dev.hawu.plugins.xenocraft
package events.combat

import UserMap.user
import events.UserEvent
import events.combat.PlayerAutoAttackSuccessEvent.handlers

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/**
 * Calls after a damage event has gone through successfully and the attack has landed, but via an auto-attack.
 */
class PlayerAutoAttackSuccessEvent(who: Player) extends PlayerEvent(who) with UserEvent(who.user.get):

  override def getHandlers: HandlerList = handlers

object PlayerAutoAttackSuccessEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
