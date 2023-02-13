package dev.hawu.plugins.xenocraft
package events.stances

import UserMap.user
import data.ArtFusionBonus
import events.UserEvent
import events.stances.PlayerStanceChangeEvent.handlers
import utils.Configuration

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/** Calls when the [[player]] changes stance.
  */
class PlayerStanceChangeEvent(who: Player, fusion: Boolean = false) extends PlayerEvent(who) with UserEvent(who.user.get):

  var baseDuration = 30.0
  var shackleRingMultiplier = 1.0
  var fusionDurationMultiplier: Double = if fusion then Configuration.fusionBonus(ArtFusionBonus.DURATION) else 1.0
  var stanceDurationBonus = 0.0

  /** Calculates the final duration of the stance change in seconds.
    */
  def finalDuration: Double = baseDuration * fusionDurationMultiplier * shackleRingMultiplier + baseDuration * stanceDurationBonus

  override def getHandlers: HandlerList = handlers

object PlayerStanceChangeEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
