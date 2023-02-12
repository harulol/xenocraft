package dev.hawu.plugins.xenocraft
package events.combat

import UserMap.user
import data.{ArtReaction, ArtType, Directional, EnemyEntity}
import events.UserEvent
import events.combat.PlayerDealDamageEvent
import utils.Formulas

import org.bukkit.entity.{Mob, Player}
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.{Cancellable, Event, HandlerList}

import java.util.concurrent.ThreadLocalRandom

/** The event is called when a player attempts to deal damage to a mob.
  *
  * Cancelling this event will prevent the mob from taking any damage.
  */
class PlayerDealDamageEvent(
  player: Player,
  val direction: Directional,
  val entity: EnemyEntity,
  val physical: Boolean,
  val artType: Option[ArtType] = None,
  val fusion: Boolean = false,
  val artCritMod: Double = 0,
  val artHitChance: Double = 0,
  val isPreemptive: Boolean = false,
  val isPiercing: Boolean = false,
  val isAoE: Boolean = false,
) extends PlayerEvent(player) with Cancellable with UserEvent(player.user.get):

  private val random = ThreadLocalRandom.current()
  private val criticalHit = Formulas.canCrit(user, artCritMod, isPreemptive)
  private val blockedHit = Formulas.canBlock(entity, direction)
  private val landedHit = Formulas.canHit(user, entity, artHitChance)

  private val _stabilityModifier = random.nextDouble(0.0, user.weapon.get.weaponAttack * user.weapon.get.weaponStability)
  private val _critMultiplier = if isCritical then 1.25 + user.critDamage else 1.0
  private val _comboMultiplier = if entity.reaction.contains(ArtReaction.LAUNCH) then 1.5 else 1.0
  private val _typeDefenseMultiplier = Formulas.calculateTypeDefenseMultiplier(entity, physical, isPiercing)
  private val _blockedMultiplier = if isBlocked then 1 - (if fusion then 0.75 else 0.5) - entity.flatBlockStrength else 1.0
  private val _backPreemptiveMultiplier = if isPreemptive && direction == Directional.BACK then 1.5 else 1.0
  private val _aoeMultiplier = if isAoE then 0.75 else 1.0
  private val _multiHitCorrection = if artType.isDefined then 1.0 / artType.get.hits else 1.0
  private val _randomMultiplier = if isPreemptive then random.nextDouble(1.0, 1.1) else random.nextDouble(0.9, 1.1)

  var artPowerMultiplier = if artType.isDefined then artType.get.powerMultiplier else 1.0
  var hits = artType.map(_.hits).getOrElse(1)
  var damageBonus1 = 0.0
  var damageBonus2 = 0.0
  var damageBonus3 = 0.0
  var damageReduction = 0.0
  var shackleRingMultiplier = 1.0
  var isEvaded = entity.isEvading
  var isHit = landedHit

  private var cancelled = false

  // Create getters using `def` for the private val fields with an underscore before name.
  // Please Copilot

  /** Gets the stability modifier of the damage event. This depends on the weapon's stability, and accounts for how consistent the attack
    * increase is.
    *
    * @return
    *   the stability modifier
    */
  def stabilityModifier: Double = _stabilityModifier

  /** Gets the critical hit multiplier of the damage event. [[1.25 + user.critDamage]] if this was a critical hit.
    *
    * @return
    *   the critical hit multiplier
    */
  def critMultiplier: Double = _critMultiplier

  /** Gets the combo multiplier of the damage event.
    *
    * @return
    *   the combo multiplier
    */
  def comboMultiplier: Double = _comboMultiplier

  /** Gets the type defense multiplier of the damage event.
    *
    * @return
    *   the type defense multiplier
    */
  def typeDefenseMultiplier: Double = _typeDefenseMultiplier

  /** Gets the blocked multiplier of the damage event.
    *
    * @return
    *   the blocked multiplier
    */
  def blockedMultiplier: Double = _blockedMultiplier

  /** Gets the back preemptive multiplier of the damage event.
    *
    * @return
    *   the back preemptive multiplier
    */
  def backPreemptiveMultiplier: Double = _backPreemptiveMultiplier

  /** Gets the AoE multiplier of the damage event.
    *
    * @return
    *   the AoE multiplier
    */
  def aoeMultiplier: Double = _aoeMultiplier

  /** Gets the multi-hit correction of the damage event.
    *
    * @return
    *   the multi-hit correction
    */
  def multiHitCorrection: Double = _multiHitCorrection

  /** Gets the random multiplier of the damage event.
    *
    * @return
    *   the random multiplier
    */
  def randomMultiplier: Double = _randomMultiplier

  /** Checks if this damage event is a critical hit.
    *
    * @return
    *   true if the damage event is a critical hit, false otherwise
    */
  def isCritical: Boolean = criticalHit

  /** Finds the damage that is already coerced within the limit.
    *
    * @return
    *   the coerced damage
    */
  def finalDamage: Double = totalDamage min 9999999 max 0

  /** Calculates the total damage without accounting for the limit.
    *
    * @return
    *   the unlimited total damage
    */
  def totalDamage: Double =
    // A blocked hit can never crit.
    val realCritMultiplier = if isBlocked then 1.0 else _critMultiplier
    (user.attack + _stabilityModifier) * artPowerMultiplier *
      (1 + damageBonus1) *
      (1 + damageBonus2) *
      (1 + damageBonus3) *
      (1 - damageReduction) * _typeDefenseMultiplier * _blockedMultiplier * realCritMultiplier * _comboMultiplier *
      _backPreemptiveMultiplier * _aoeMultiplier * _multiHitCorrection * _randomMultiplier * shackleRingMultiplier

  /** Checks if this damage event is a blocked hit.
    *
    * @return
    *   true if the damage event is a blocked hit, false otherwise
    */
  def isBlocked: Boolean = blockedHit

  /** Gets whether the event is cancelled.
    *
    * @return
    *   true if the event is cancelled, false otherwise
    */
  override def isCancelled: Boolean = cancelled

  /** Sets whether the event is cancelled.
    *
    * @param cancelled
    *   true if the event is cancelled, false otherwise
    */
  override def setCancelled(cancelled: Boolean): Unit = this.cancelled = cancelled

  /** Gets the list of handlers for this event.
    *
    * @return
    *   the list of handlers
    */
  override def getHandlers: HandlerList = PlayerDealDamageEvent.getHandlerList

end PlayerDealDamageEvent

/** Object companion for the [[PlayerDealDamageEvent]] class.
  */
object PlayerDealDamageEvent:

  private val handlers = HandlerList()

  /** Gets the list of handlers for this event.
    *
    * @return
    *   the list of handlers
    */
  def getHandlerList: HandlerList = handlers
