package dev.hawu.plugins.xenocraft
package data

import java.util.UUID

/** Represents a class that has attributes for fighting such as HP, battlefields, and others.
  *
  * [[User]] is a special case of [[Attributable]] where [[User]] only accepts real players.
  */
trait Attributable(val uuid: UUID):

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

  // DEFENSE TYPE CALCULATIONS
  var pctPhysDef = 0.0
  var flatPhysDef = 0.0
  var flatPhysDefReduction = 0.0
  var pctEtherDef = 0.0
  var flatEtherDef = 0.0
  var flatEtherDefReduction = 0.0
  protected var _hp: Double = 0.0

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
