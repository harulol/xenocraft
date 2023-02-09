package dev.hawu.plugins.xenocraft
package events

import UserMap.user

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/** Represents an event that is called when the player has technically "died" within the game, does not correlate with a player's actual
  * death.
  *
  * @param who
  *   The player that has been incapacitated.
  */
class PlayerIncapacitateEvent(who: Player) extends PlayerEvent(who) with UserEvent(who.user.get):

  override def getHandlers: HandlerList = PlayerIncapacitateEvent.getHandlerList

/** Represents a list of handlers for the [[PlayerIncapacitateEvent]].
  */
object PlayerIncapacitateEvent:

  private val handlers = HandlerList()

  def getHandlerList: HandlerList = handlers
