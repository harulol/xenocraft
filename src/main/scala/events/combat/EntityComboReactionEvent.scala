package dev.hawu.plugins.xenocraft
package events.combat

import data.{ArtReaction, Attributable}
import events.combat.EntityComboReactionEvent.handlers

import org.bukkit.event.{Event, HandlerList}

/** Calls when a combo reaction has successfully been applied to an entity. This event is called for managing the duration for the combo,
  * this is part of why this event is non-cancellable. If you want to cancel a combo reaction, check out [[EntityReactionEvent]].
  *
  * The duration only will be applied for ''BREAK'', ''TOPPLE'', ''LAUNCH'' and ''DAZE''. '''SMASH''', '''BURST''', '''BLOWDOWN''' and
  * '''KNOCKBACK''' are all instantaneous reaction and can not be extended.
  */
class EntityComboReactionEvent(val target: Attributable, val attacker: Attributable, val reaction: ArtReaction) extends Event:

  /** The base duration of the reaction in server ticks. Originally in frames in 30fps environment.
    */
  val baseFrames: Long = reaction match
    case ArtReaction.BREAK                                          => 15 * 20 // 15s base, 20 ticks/s
    case ArtReaction.TOPPLE | ArtReaction.LAUNCH | ArtReaction.DAZE => 5 * 20 // 5s base, 20 ticks/s
    case _                                                          => 0

  var reactionDurationUp = 0.0
  var shackleRingMultiplier = 1.0
  var pctReactionExtension = 0.0

  /** The rounded number of ticks the duration will last for.
    */
  def finalDuration: Long = totalDuration.round

  /** Retrieves the unbound number of ticks that the reaction will last.
    */
  def totalDuration: Double = baseFrames * (1 + reactionDurationUp) * shackleRingMultiplier * (baseFrames + pctReactionExtension)

  override def getHandlers: HandlerList = handlers

/** Companion object for [[EntityComboReactionEvent]].
  */
object EntityComboReactionEvent:

  private val handlers = HandlerList()

  def getHandlerList: HandlerList = handlers
