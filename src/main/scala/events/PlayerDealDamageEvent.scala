package dev.hawu.plugins.xenocraft
package events

import org.bukkit.event.Event
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import org.bukkit.entity.Mob
import dev.hawu.plugins.xenocraft.UserMap.user
import java.util.concurrent.ThreadLocalRandom
import dev.hawu.plugins.xenocraft.utils.Formulas
import dev.hawu.plugins.xenocraft.data.EnemyEntity
import dev.hawu.plugins.xenocraft.data.ArtReaction
import dev.hawu.plugins.xenocraft.data.Directional
import dev.hawu.plugins.xenocraft.data.ArtType

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
) extends PlayerEvent(player) with Cancellable:

  private var cancelled = false
  private val random = ThreadLocalRandom.current()
  private val user = player.user.get
  private val criticalHit = Formulas.canCrit(user, artCritMod, isPreemptive)
  private val blockedHit = Formulas.canBlock(entity, direction)
  private val landedHit = Formulas.canHit(user, entity, artHitChance)

  val stabilityModifier = random.nextDouble(0.0, user.weapon.get.weaponAttack * user.weapon.get.weaponStability)
  val critMultiplier = if isCritical then 1.25 + user.critDamage else 1.0
  val comboMultiplier = if entity.reaction.contains(ArtReaction.LAUNCH) then 1.5 else 1.0

  val typeDefenseMultiplier = {
    var value =
      if physical then 1 - entity.physicalDef + entity.flatPhysDefReduction
      else 1 - entity.etherDef + entity.flatEtherDefReduction

    if isPiercing then value = value max 1.0
    value
  }

  val blockedMultiplier = if isBlocked then 1 - (if fusion then 0.75 else 0.5) - entity.flatBlockStrength else 1.0
  val backPreemptiveMultiplier = if isPreemptive && direction == Directional.BACK then 1.5 else 1.0
  val aoeMultiplier = if isAoE then 0.75 else 1.0
  val multiHitCorrection = if artType.isDefined then 1.0 / artType.get.hits else 1.0

  val randomMultiplier =
    if isPreemptive then ThreadLocalRandom.current().nextDouble(1.0, 1.1)
    else ThreadLocalRandom.current().nextDouble(0.9, 1.1)

  var artPowerMultiplier = if artType.isDefined then artType.get.powerMultiplier else 1.0
  var damageBonus1 = 0.0
  var damageBonus2 = 0.0
  var damageBonus3 = 0.0
  var damageReduction = 0.0
  var shackleRingMultiplier = 1.0
  var isEvaded = entity.isEvading
  var isHit = landedHit

  /** Checks if an art was used for this damage event.
    *
    * @return
    *   the art power multiplier
    */
  def isArtUsed: Boolean = artPowerMultiplier.intValue <= 1

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

  /** Calculates the total damage without accounting for the limit.
    *
    * @return
    *   the unlimited total damage
    */
  def totalDamage: Double =
    // A blocked hit never crits.
    val realCritMultiplier = if isBlocked then 1.0 else critMultiplier
    (user.attack + stabilityModifier) * artPowerMultiplier *
      (1 + damageBonus1) *
      (1 + damageBonus2) *
      (1 + damageBonus3) *
      (1 - damageReduction) * typeDefenseMultiplier * blockedMultiplier * realCritMultiplier * comboMultiplier *
      backPreemptiveMultiplier * aoeMultiplier * multiHitCorrection * randomMultiplier * shackleRingMultiplier

  /** Finds the damage that is already coerced within the limit.
    *
    * @return
    *   the coerced damage
    */
  def finalDamage: Double = totalDamage min 9999999 max 0

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
  override def getHandlers(): HandlerList = PlayerDealDamageEvent.getHandlerList

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
