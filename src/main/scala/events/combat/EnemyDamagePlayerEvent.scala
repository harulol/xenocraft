package dev.hawu.plugins.xenocraft
package events.combat

import UserMap.user
import data.{ArtReaction, EnemyEntity}
import events.combat.EnemyDamagePlayerEvent
import utils.Formulas

import org.bukkit.entity.{Mob, Player}
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.{Cancellable, HandlerList}

import java.util.concurrent.ThreadLocalRandom

/** Represents an event where an enemy damages a player.
  *
  * This should be much much more simpler.
  *
  * @param entity
  *   the entity dealing the damage
  * @param enemy
  *   the enemy instance of the entity
  * @param player
  *   the target player
  */
class EnemyDamagePlayerEvent(entity: Mob, val enemy: EnemyEntity, val player: Player) extends EntityEvent(entity) with Cancellable:

  private val user = player.user.get
  private val criticalHit = Formulas.canCrit(enemy)
  private val blockedHit = Formulas.canBlock(user)
  private val landedHit = Formulas.canHit(enemy, user) || user.reaction.contains(ArtReaction.DAZE) // Dazed entities can't dodge.
  private val random = ThreadLocalRandom.current()

  private val _critMultiplier = if isCritical then 1.25 + enemy.critDamage else 1.0
  private val _comboMultiplier = if user.reaction.contains(ArtReaction.LAUNCH) then 1.5 else 1.0
  private val _blockedMultiplier = if isBlocked then 1 - user.flatBlockStrength else 1.0
  private val _randomMultiplier = random.nextDouble(0.9, 1.1)

  var damageReduction = 0.0
  var isEvaded = user.isEvading || random.nextDouble() < user.evasionChance
  var isHit = landedHit

  private var cancelled = false

  /** Retrieves the multiplier accounting for critical hits.
    *
    * @return
    * 1.25 + attack's crit damage if critical hit
    */
  def critMultiplier: Double = _critMultiplier

  /** Retrieves the multiplier accounting for combo hits.
    *
    * @return
    * 1.5 if target is being launched or affected with rising
    */
  def comboMultiplier = _comboMultiplier

  /** Retrieves the multiplier accounting for how much damage the user can negate with flat block strength.
    *
    * @return
    *   the multiplier
    */
  def blockedMultiplier = _blockedMultiplier

  /** Retrieves the randomize ratio for damage for this event.
    *
    * @return
    *   the randomized ratio
    */
  def randomMultiplier = _randomMultiplier

  /** Checks if this damage event is a critical hit.
    *
    * @return
    *   true if the damage event is a critical hit, false otherwise
    */
  def isCritical: Boolean = criticalHit

  /** Checks if this damage event is a blocked hit.
    *
    * @return
    *   true if the damage event is a blocked hit, false otherwise
    */
  def isBlocked: Boolean = blockedHit

  /** Calculates the final damage of this event.
    *
    * @return
    *   the final damage
    */
  def finalDamage: Double = totalDamage min 9999999 max 0

  /** Calculates the total damage of this event.
    *
    * @return
    *   the total damage
    */
  def totalDamage: Double = enemy.attack * (1 - damageReduction) * _critMultiplier * _comboMultiplier * _blockedMultiplier *
    _randomMultiplier

  override def getHandlers: HandlerList = EnemyDamagePlayerEvent.handlers

  override def isCancelled: Boolean = cancelled

  override def setCancelled(cancel: Boolean): Unit = cancelled = cancel

end EnemyDamagePlayerEvent

/** Companion object for [[EnemyDamagePlayerEvent]].
  */
object EnemyDamagePlayerEvent:
  private val handlers = HandlerList()

  def getHandlerList: HandlerList = handlers
