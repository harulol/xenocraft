package dev.hawu.plugins.xenocraft
package data

import java.util.UUID
import scala.collection.mutable
import org.bukkit.scheduler.BukkitTask
import dev.hawu.plugins.api.Tasks

/** Represents a class that has attributes for fighting such as HP, battlefields, and others.
  *
  * [[User]] is a special case of [[Attributable]] where [[User]] only accepts real players.
  */
trait Attributable(val uuid: UUID):

  private val kevesiArts = mutable.Map.empty[ArtType, Double]
  private val agnianArts = mutable.Map.empty[ArtType, Double]
  private var talentArt: Double = 0.0
  private var task: Option[BukkitTask] = None

  // GENERIC STATS VALUES
  var pctAttack = 0.0
  var flatAttack = 0.0
  var pctHp = 0.0
  var flatHp = 0.0
  var pctHealing = 0.0
  var flatHealing = 0.0
  var pctDexterity = 0.0
  var flatDexterity = 0.0
  var pctAgility = 0.0
  var flatAgility = 0.0

  // CRIT CHANCE CALCULATIONS
  var noncombatPctCrit = 0.0
  var noncombatFlatCrit = 0.0
  var combatPctCrit = 0.0
  var combatFlatCrit = 0.0
  var critDamage = 0.0

  // BLOCK RATE CALCULATIONS
  var noncombatPctBlock = 0.0
  var noncombatFlatBlock = 0.0
  var combatPctBlock = 0.0
  var combatFlatBlock = 0.0

  // AGGRO CALCULATIONS
  var aggro = 0.0
  var autoAggroGeneration = 1.0
  var artAggroGeneration = 1.0

  // RECHARGE CALCULATIONS
  var rechargeSpeed = 1.0
  var doubleHits = 1.0

  // BATTLE CALCULATIONS
  var buffPower = 0.0
  var buffDurationBonus = 0.0
  var debuffPower = 0.0
  var debuffDurationBonus = 0.0
  var allyHpRestore = 1.0
  var allyReviveSpeed = 1.0
  var reaction: Option[ArtReaction] = None
  var reactionFrames = 0

  // BUFFS AND DEBUFFS
  var accuracyUp = 0.0
  var rechargeUp = 0.0
  var evasionUp = 0.0
  var evasionChance = 0.0

  // DEFENSE TYPE CALCULATIONS
  var pctPhysDef = 0.0
  var flatPhysDef = 0.0
  var flatPhysDefReduction = 0.0
  var pctEtherDef = 0.0
  var flatEtherDef = 0.0
  var flatEtherDefReduction = 0.0
  protected var _hp: Double = 0.0

  /** Starts the cooldown task.
    */
  def startKevesiCooldown(): Unit =
    task.foreach(_.cancel())
    task = Some(Tasks.run(_ => { kevesiArts.keys.foreach(rechargeArt(_)) }).async(true).delay(0).period(1).run())

  /** Stops the cooldown task.
    */
  def stopKevesiCooldown(): Unit =
    task.foreach(_.cancel())
    task = None

  /** Gets the current recharge guage for an art.
    *
    * @param art
    *   the art to get the recharge guage for.
    */
  def getCooldown(art: ArtType): Double =
    if art.isTalent then talentArt
    else if art.isKevesi then kevesiArts.getOrElseUpdate(art, 0.0)
    else if art.isAgnian then agnianArts.getOrElseUpdate(art, 0.0)
    else 0.0

  /** Checks if a certain art is on cooldown.
    *
    * @param art
    *   the art
    * @return
    *   the cooldown status
    */
  def isOnCooldown(art: ArtType): Boolean =
    if art.isTalent then talentArt < 1.0
    else if art.isKevesi then kevesiArts.getOrElseUpdate(art, 0.0) < art.cooldown * 20 // 20 ticks per second.
    else if art.isAgnian then agnianArts.getOrElseUpdate(art, 0.0) < art.cooldown // agnian arts are auto attacks.
    else false

  /** Applies a cooldown to the provided art type.
    *
    * @param art
    *   the art type
    */
  def use(art: ArtType): Unit =
    if art.isTalent then talentArt = 0.0
    else if art.isKevesi then kevesiArts.put(art, 0.0)
    else if art.isAgnian then agnianArts.put(art, 0.0)

  /** Resets all cooldowns of this attributable.
    */
  def resetCooldowns(): Unit =
    kevesiArts.clear()
    agnianArts.clear()
    talentArt = 0.0

  /** Recharges the art by the base value.
    *
    * @param art
    *   the art
    * @param base
    *   the base value
    */
  def rechargeArt(art: ArtType, base: Double = 1.0): Unit =
    if art.isKevesi then kevesiArts.put(art, kevesiArts.getOrElse(art, 0.0) + base * (1 + rechargeUp))
    else if art.isAgnian then agnianArts.put(art, agnianArts.getOrElse(art, 0.0) + base * (1 + rechargeUp))
    else if art.isTalent then talentArt += base * (1 + rechargeUp)

  /** Retrieves the current value of the attributable.
    *
    * As Bukkit doesn't support huge values of health, please synchronize this value to be as close to the real health
    * value as possible, percentage wise.
    *
    * Meaning, if [[hp]] / [[maxHp]] = 0.5, then the amount of real HP of the attributable must be half of the max
    * health value in Minecraft.
    *
    * The entity fell if this value reaches 0.
    *
    * @return
    *   the HP value
    */
  def hp: Double = _hp

  /** Sets the HP for this attributable.
    *
    * Implementations must coerce the value to be within valid HP range [0, [[maxHp]]].
    *
    * @param value
    *   the value to set
    */
  def setHp(value: Double): Unit

  /** The maximum value of the HP value.
    *
    * The HP must never surpass this value.
    *
    * @return
    *   the maximum possible HP value
    */
  def maxHp: Double = 0

  /** The attack value of this attributable. This decides the base value of all damage calculations.
    *
    * @return
    *   the attack value
    */
  def attack: Double = 0

  /** The healing power value of this attributable. This decides how much they are healed, also varied by arts.
    *
    * @return
    *   the healing value
    */
  def healing: Double = 0

  /** The dexterity value of this attributable. This decides how likely the attributable's hits are going to land.
    *
    * @return
    *   the dexterity value
    */
  def dexterity: Double = 0

  /** The agility value of this attributable. This decides how likely this attributable can dodge attacks.
    *
    * @return
    *   the agility value
    */
  def agility: Double = 0

  /** The critical rate value of this attributable, this does not mean the crit rate is the same during combat as combat
    * requires [[combatPctCrit]] or similar in calculations. This decides how likely this attributable can deal a
    * critical hit.
    *
    * Critical hits mean that they deal from 125% more critical damage. This can be increased via accessories or gems.
    *
    * @return
    *   the critical rate value
    */
  def critRate: Double = 0

  /** The block rate value of this attributable. This does not mean the block rate is the same during combat as combat
    * requires [[combatPctBlock]] or similar in calculations. The decides how likely this attributable can block a
    * certain hit.
    *
    * Blocked hits deal vastly reduced damage. This can be increased via accessories or gems.
    *
    * @return
    *   the block rate value
    */
  def blockRate: Double = 0

  /** The physical defense value of this attributable. This decides how much damage is negated from physical attacks.
    *
    * @return
    *   the physical defense value
    */
  def physicalDef: Double = 0

  /** The ether defense value of this attributable. This decides how much damage is negated from ether attacks.
    *
    * @return
    *   the ether defense value
    */
  def etherDef: Double = 0

end Attributable
