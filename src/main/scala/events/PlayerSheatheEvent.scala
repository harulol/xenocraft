package dev.hawu.plugins.xenocraft
package events

import UserMap.user

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/** Called when the player sheathes their blade.
  *
  * @param player
  *   the player
  */
@deprecated
class PlayerSheatheEvent(player: Player) extends PlayerEvent(player) with UserEvent(player.user.get):
  override def getHandlers: HandlerList = PlayerSheatheEvent.handlers

/** The companion object for [[PlayerSheatheEvent]].
  */
object PlayerSheatheEvent:
  private val handlers = HandlerList()
  def getHandlerList: HandlerList = handlers
