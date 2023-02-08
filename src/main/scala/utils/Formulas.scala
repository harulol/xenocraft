package dev.hawu.plugins.xenocraft
package utils

import combat.CombatManager
import data.{Attributable, Directional, EnemyEntity, User}

import org.bukkit.entity.{LivingEntity, Mob}

import java.security.SecureRandom
import java.util.concurrent.ThreadLocalRandom

/** Singleton object dedicated to calculating stats.
  */
object Formulas:

  /** Calculates the display attack value of a user.
    *
    * @param user
    *   the user
    * @return
    *   the display attack value
    */
  def calculateAttack(user: User): Double =
    val baseAttack = user.char.map(_.baseAttack).getOrElse(0d)
    val classAttack = user.cls.map(_.classAttack).getOrElse(0d)
    val weaponAttack = user.weapon.map(_.weaponAttack).getOrElse(0d)
    (baseAttack * classAttack + weaponAttack) * (1 + user.pctAttack) + user.flatAttack

  /** Calculates the display HP value of a user.
    *
    * @param user
    *   the user
    * @return
    *   the display HP value
    */
  def calculateHp(user: User): Double =
    val baseHp = user.char.map(_.baseHp).getOrElse(0d)
    val classHp = user.cls.map(_.classHp).getOrElse(0d)
    baseHp * classHp * (1 + user.pctHp) + user.flatHp

  /** Calculates the display healing value of a user.
    *
    * @param user
    *   the user
    * @return
    *   the display healing value
    */
  def calculateHealingPower(user: User): Double =
    val baseHealing = user.char.map(_.baseHealingPower).getOrElse(0d)
    val classHealing = user.cls.map(_.classHealingPower).getOrElse(0d)
    baseHealing * classHealing * (1 + user.pctHealing) + user.flatHealing

  /** Calculates the display dexterity value of a user.
    *
    * @param user
    *   the user
    * @return
    *   the display dexterity value
    */
  def calculateDexterity(user: User): Double =
    val baseDexterity = user.char.map(_.baseDexterity).getOrElse(0d)
    val classDexterity = user.cls.map(_.classDexterity).getOrElse(0d)
    baseDexterity * classDexterity * (1 + user.pctDexterity) + user.flatDexterity

  /** Calculates the display agility value of a user.
    *
    * @param user
    *   the user
    * @return
    *   the display agility value
    */
  def calculateAgility(user: User): Double =
    val baseAgility = user.char.map(_.baseAgility).getOrElse(0d)
    val classAgility = user.cls.map(_.classAgility).getOrElse(0d)
    baseAgility * classAgility * (1 + user.pctAgility) + user.flatAgility

  /** Calculates the display critical rate value of a user.
    *
    * @param user
    *   the user
    * @return
    *   the display critical rate value
    */
  def calculateDisplayCritRate(user: User): Double =
    val weaponCrit = user.weapon.map(_.weaponCrit).getOrElse(0d)
    weaponCrit * (1 + user.noncombatPctCrit) + user.noncombatFlatCrit

  /** Calculates the display block rate value of a user.
    *
    * @param user
    *   the user
    * @return
    *   the display block rate value
    */
  def calculateDisplayBlockRate(user: User): Double =
    val weaponBlock = user.weapon.map(_.weaponBlock).getOrElse(0d)
    weaponBlock * (1 + user.noncombatPctBlock) + user.noncombatFlatBlock

  /** Calculates the display physical defense value of a user.
    *
    * @param user
    *   the user
    * @return
    *   the display physical defense value
    */
  def calculateDisplayPhysDefense(user: User): Double =
    val physDef = user.cls.map(_.classPhysDef).getOrElse(0d)
    physDef * (1 + user.pctPhysDef) + user.flatPhysDef

  /** Calculates the display ether defense value of a user.
    *
    * @param user
    *   the user
    * @return
    *   the display ether defense value
    */
  def calculateDisplayEtherDefense(user: User): Double =
    val etherDef = user.cls.map(_.classEtherDef).getOrElse(0d)
    etherDef * (1 + user.pctEtherDef) + user.flatEtherDef

  /** Checks if the user should have a critical hit.
    *
    * @param user
    *   the user
    * @param artCritMod
    *   whether an art was used, and the art has a modifier on crit rate
    * @param preemptive
    *   whether it was a preemptive attack
    * @return
    *   whether the user should have a critical hit
    */
  def canCrit(user: User, artCritMod: Double = 0, preemptive: Boolean = false): Boolean =
    val critRate = user.critRate * (1 + user.combatPctCrit) * (1 + artCritMod) + user.combatFlatCrit
    val roll = ThreadLocalRandom.current().nextDouble()
    val actualRoll = if preemptive then roll * 0.57 else roll
    actualRoll <= critRate

  /** Runs a check whether the entity has done a critical hit.
    *
    * @param enemy
    *   the enemy
    * @return
    *   the result of the check
    */
  def canCrit(enemy: EnemyEntity): Boolean =
    val critRate = enemy.critRate * (1 + enemy.combatPctCrit) + enemy.combatFlatCrit
    val roll = ThreadLocalRandom.current().nextDouble()
    roll <= critRate

  /** Runs a check whether the user can block a hit.
    *
    * @param user
    *   the user
    * @return
    *   the result of the check
    */
  def canBlock(user: User): Boolean =
    val blockRate = user.blockRate * (1 + user.combatPctBlock) + user.combatFlatBlock
    val roll = ThreadLocalRandom.current().nextDouble()
    roll <= blockRate

  /** Performs a check on whether the hit should land.
    */
  def canHit(source: Attributable, target: Attributable, artHitChance: Double = 0.0): Boolean =
    val hitChance1 = ((0.75 * (source.dexterity) / (target.agility) * (1 + artHitChance)) min 0.97) max 0.0
    val hitChance2 = hitChance1 * (1 + source.accuracyUp - target.evasionUp)
    val hitChance3 = (hitChance2 min 0.97) max (0.015 min hitChance1)
    val hitChance = hitChance3 * (1 - target.evasionChance)
    ThreadLocalRandom.current().nextDouble() <= hitChance

  /** Checks if an enemy can block.
    *
    * @param entity
    *   the entity
    * @param directional
    *   the directional
    * @return
    *   whether the enemy can block
    */
  def canBlock(entity: EnemyEntity, directional: Directional): Boolean =
    val positionalBlockRate = directional match
      case Directional.LEFT  => entity.guardLeft
      case Directional.RIGHT => entity.guardRight
      case Directional.FRONT => entity.guardFront
      case Directional.BACK  => entity.guardBack
    val other = positionalBlockRate + entity.combatFlatBlock
    val blockRate = (0.015 min positionalBlockRate) max other

    ThreadLocalRandom.current().nextDouble() <= blockRate

end Formulas
