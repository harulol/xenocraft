package dev.hawu.plugins.xenocraft
package events

import UserMap.user
import data.EnemyEntity
import events.PlayerAutoAttackEvent.handlers

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/** This event is called when the player attempt an auto attack at an enemy. Whether it has landed or not.
  *
  * @param who
  *   the player
  */
class PlayerAutoAttackEvent(who: Player, val enemy: EnemyEntity) extends PlayerEvent(who) with UserEvent(who.user.get):

  override def getHandlers = handlers

/** Object singleton for [[PlayerAutoAttackEvent]]
  */
object PlayerAutoAttackEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
