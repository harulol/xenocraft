package dev.hawu.plugins.xenocraft
package events.combat

import data.{ArtReaction, Attributable, EnemyEntity, User}
import events.combat.EntityReactionEvent.handlers

import org.bukkit.entity.LivingEntity
import org.bukkit.event.{Cancellable, Event, HandlerList}

import java.util.concurrent.ThreadLocalRandom

/** Calls when an entity has caused a reaction on another entity.
  *
  * If this is called because of a damage event, this will not be called altogether if the damage event has missed in any way.
  */
class EntityReactionEvent(
  val damager: LivingEntity,
  val entity: LivingEntity,
  val attacker: Attributable,
  val target: Attributable,
  val reaction: ArtReaction,
) extends Event with Cancellable:

  var baseReactionResist = 0.0
  var flatReactionResist = 0.0
  var flatReactionShred = 0.0
  var resistDown: Double = target.resistDown
  var pctReactionShred = 0.0
  var shackleRingMultiplier = 0.0

  private var cancelled = false

  baseReactionResist = target match
    case enemy: EnemyEntity => reaction match
        case ArtReaction.BREAK     => enemy.breakResistance
        case ArtReaction.TOPPLE    => enemy.toppleResistance
        case ArtReaction.DAZE      => enemy.dazeResistance
        case ArtReaction.BURST     => enemy.burstResistance
        case ArtReaction.LAUNCH    => enemy.launchResistance
        case ArtReaction.SMASH     => enemy.smashResistance
        case ArtReaction.BLOWDOWN  => enemy.blowdownResistance
        case ArtReaction.KNOCKBACK => enemy.knockbackResistance
    case _ => 0.0

  /** Checks if the reaction was from a user.
    */
  def isFromPlayer: Boolean = attacker.isInstanceOf[User]

  /** Evaluates a random number and check if with the current values, can the target be inflicted with the reaction or not.
    */
  def canResist: Boolean = ThreadLocalRandom.current().nextDouble() < reactionRate

  def reactionRate: Double = ((1 - reactionResist3) * shackleRingMultiplier) max 0

  def reactionResist3: Double = (reactionResist2 * (1 - pctReactionShred) - flatReactionShred) max 0

  def reactionResist2: Double = (reactionResist1 + flatReactionResist - resistDown) max 0

  def reactionResist1: Double = baseReactionResist max 0

  override def isCancelled: Boolean = cancelled

  override def setCancelled(cancel: Boolean): Unit = cancelled = cancel

  override def getHandlers: HandlerList = handlers

/** Object for [[EntityReactionEvent]].
  */
object EntityReactionEvent:

  private val handlers = HandlerList()

  def getHandlerList: HandlerList = handlers
