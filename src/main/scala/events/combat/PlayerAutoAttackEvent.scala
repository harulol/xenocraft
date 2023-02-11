package dev.hawu.plugins.xenocraft
package events.combat

import UserMap.user
import data.EnemyEntity
import events.UserEvent
import events.combat.PlayerAutoAttackEvent.handlers

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/** This event is called when the player successfully lands an auto attack at an enemy.
  */
class PlayerAutoAttackEvent(who: Player, val enemy: EnemyEntity) extends PlayerEvent(who) with UserEvent(who.user.get):

  override def getHandlers = handlers

/** Object singleton for [[PlayerAutoAttackEvent]]
  */
object PlayerAutoAttackEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
