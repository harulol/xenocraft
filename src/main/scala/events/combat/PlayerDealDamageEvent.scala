package dev.hawu.plugins.xenocraft
package events.combat

import UserMap.user
import data.*
import events.UserEvent
import events.combat.PlayerDealDamageEvent
import managers.{BattlefieldManager, EnemyManager}
import utils.{Configuration, Formulas}

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
  val masterArt: Boolean = false,
) extends PlayerEvent(player) with Cancellable with UserEvent(player.user.get):

  private val random = ThreadLocalRandom.current()
  private val criticalHit = Formulas.canCrit(user, artCritMod, isPreemptive)
  private val blockedHit = Formulas.canBlock(entity, direction)
  private val landedHit = entity.reaction.contains(ArtReaction.DAZE) || Formulas.canHit(user, entity, artHitChance) // Dazed enemies can't dodge.

  // Use the original weapon if used as a single master art.
  private val weapon = if masterArt && artType.isDefined then artType.flatMap(_.cls).get.weaponType else user.weapon.get
  private val _stabilityModifier = random.nextDouble(0.0, weapon.weaponAttack * weapon.weaponStability)
  private val _fusionDamageMultiplier = if fusion then Configuration.fusionBonus(ArtFusionBonus.DAMAGE) else 1.0

  // Only physical attacks can crit.
  private val _critMultiplier = if isCritical && physical then 1.25 + user.critDamage else 1.0
  private val _comboMultiplier = if entity.reaction.contains(ArtReaction.LAUNCH) then 1.5 else 1.0
  private val _typeDefenseMultiplier = Formulas.calculateTypeDefenseMultiplier(entity, physical, isPiercing)

  // Ether attacks can not be blocked.
  private val _blockedMultiplier =
    if isBlocked && !physical then 1 - 0.5 * Configuration.fusionBonus(ArtFusionBonus.BLOCK) - entity.flatBlockStrength else 1.0

  private val _aoeMultiplier = if isAoE then 0.75 else 1.0
  private val _multiHitCorrection = if artType.isDefined then 1.0 / artType.get.hits else 1.0
  private val _randomMultiplier = if isPreemptive then random.nextDouble(1.0, 1.1) else random.nextDouble(0.9, 1.1)

  var artPowerMultiplier: Double = if artType.isDefined then artType.get.powerMultiplier else 1.0
  var backPreemptiveMultiplier = 1.0
  var hits: Int = artType.map(_.hits).getOrElse(1)
  var damageBonus1 = 0.0
  var damageBonus2 = 0.0
  var damageBonus3 = 0.0
  var damageReduction = 0.0
  var shackleRingMultiplier = 1.0
  var isEvaded: Boolean = entity.isEvading || random.nextDouble() < entity.evasionChance
  var isHit: Boolean = landedHit

  private var cancelled = false

  // Create getters using `def` for the private val fields with an underscore before name.
  // Please Copilot

  /** Gets the critical hit multiplier of the damage event. [[1.25 + user.critDamage]] if this was a critical hit.
    *
    * @return
    *   the critical hit multiplier
    */
  def critMultiplier: Double = _critMultiplier

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
      (1 - damageReduction) * _fusionDamageMultiplier * _typeDefenseMultiplier * _blockedMultiplier * realCritMultiplier *
      _comboMultiplier * backPreemptiveMultiplier * _aoeMultiplier * _multiHitCorrection * _randomMultiplier * shackleRingMultiplier

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
   * the list of handlers
   */
  override def getHandlers: HandlerList = PlayerDealDamageEvent.getHandlerList

  /** Gets the combo multiplier of the damage event.
   *
   * @return
   * the combo multiplier
   */
  def comboMultiplier: Double = _comboMultiplier

  /** Gets the AoE multiplier of the damage event.
   *
   * @return
   * the AoE multiplier
   */
  def aoeMultiplier: Double = _aoeMultiplier

  /** Gets the multi-hit correction of the damage event.
   *
   * @return
   * the multi-hit correction
   */
  def multiHitCorrection: Double = _multiHitCorrection

  /** Gets the random multiplier of the damage event.
   *
   * @return
   * the random multiplier
   */
  def randomMultiplier: Double = _randomMultiplier

  /** Gets the stability modifier of the damage event. This depends on the weapon's stability, and accounts for how consistent the attack
   * increase is.
   *
   * @return
   * the stability modifier
   */
  def stabilityModifier: Double = _stabilityModifier

  /** Gets the type defense multiplier of the damage event.
   *
   * @return
   * the type defense multiplier
   */
  def typeDefenseMultiplier: Double = _typeDefenseMultiplier

  /** Gets the blocked multiplier of the damage event.
   *
   * @return
   * the blocked multiplier
   */
  def blockedMultiplier: Double = _blockedMultiplier

  /** The fusion damage multiplier if it is a fusion art.
   */
  def fusionDamageMultiplier: Double = _fusionDamageMultiplier

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

  /** Constructs a new builder for [[PlayerDealDamageEvent]].
    */
  def apply(player: Player): Builder = Builder(player)

  /** Represents a builder for building [[PlayerDealDamageEvent]].
    */
  class Builder(private val player: Player):

    private var _direction: Option[Directional] = None
    private var _entity: Option[EnemyEntity] = None
    private var _physical: Boolean = true
    private var _artType: Option[ArtType] = None
    private var _fusion: Boolean = false
    private var _artCritMod: Double = 0
    private var _artHitChance: Double = 0
    private var _isPreemptive: Boolean = false
    private var _isPiercing: Boolean = false
    private var _isAoE: Boolean = false
    private var _isMaster: Boolean = false

    def targeting(entity: Mob): Builder =
      _direction = Some(BattlefieldManager.calculateDirection(player, entity))
      _entity = EnemyManager.getEnemy(entity)
      this

    def targeting(entity: EnemyEntity): Builder =
      _direction = Some(BattlefieldManager.calculateDirection(player, entity.entity))
      _entity = Some(entity)
      this

    def direction(direction: Directional): Builder =
      _direction = Some(direction)
      this

    def entity(entity: EnemyEntity): Builder =
      _entity = Some(entity)
      this

    def physical(physical: Boolean): Builder =
      _physical = physical
      this

    def artType(artType: ArtType): Builder =
      _artType = Some(artType)
      this

    def fusion(fusion: Boolean): Builder =
      _fusion = fusion
      this

    def artCritMod(artCritMod: Double): Builder =
      _artCritMod = artCritMod
      this

    def artHitChance(artHitChance: Double): Builder =
      _artHitChance = artHitChance
      this

    def setPreemptive(isPreemptive: Boolean): Builder =
      _isPreemptive = isPreemptive
      this

    def setPiercing(isPiercing: Boolean): Builder =
      _isPiercing = isPiercing
      this

    def setAoE(isAoE: Boolean): Builder =
      _isAoE = isAoE
      this
      
    def setMaster(isMaster: Boolean): Builder =
      _isMaster = isMaster
      this

    /** Builds the player deal damage event.
      */
    def build: PlayerDealDamageEvent = new PlayerDealDamageEvent(
      player,
      _direction.getOrElse(throw IllegalArgumentException("Direction must not be empty.")),
      _entity.getOrElse(throw IllegalArgumentException("Entity must not be empty")),
      _physical,
      _artType,
      _fusion,
      _artCritMod,
      _artHitChance,
      _isPreemptive,
      _isPiercing,
      _isAoE,
      _isMaster,
    )
