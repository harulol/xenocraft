package dev.hawu.plugins.xenocraft
package events

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.HandlerList

/** Called when the player sheathes their blade.
  *
  * @param player
  *   the player
  */
class PlayerSheatheEvent(player: Player) extends PlayerEvent(player):
  override def getHandlers: HandlerList = PlayerSheatheEvent.handlers

/** The companion object for [[PlayerSheatheEvent]].
  */
object PlayerSheatheEvent:
  private val handlers = HandlerList()
  def getHandlerList: HandlerList = handlers
