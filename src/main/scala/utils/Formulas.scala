package dev.hawu.plugins.xenocraft
package utils

import dev.hawu.plugins.xenocraft.data.User

/**
 * Singleton object dedicated to calculating stats.
 */
object Formulas:

  /**
   * Calculates the display attack value of a user.
   *
   * @param user the user
   * @return the display attack value
   */
  def calculateAttack(user: User): Double =
    val baseAttack = user.char.map(_.baseAttack).getOrElse(0D)
    val classAttack = user.cls.map(_.classAttack).getOrElse(0D)
    val weaponAttack = user.weapon.map(_.weaponAttack).getOrElse(0D)
    (baseAttack * classAttack + weaponAttack) * (1 + user.pctAttack) + user.flatAttack

  /**
   * Calculates the display HP value of a user.
   *
   * @param user the user
   * @return the display HP value
   */
  def calculateHp(user: User): Double =
    val baseHp = user.char.map(_.baseHp).getOrElse(0D)
    val classHp = user.cls.map(_.classHp).getOrElse(0D)
    baseHp * classHp * (1 + user.pctHp) + user.flatHp

  /**
   * Calculates the display healing value of a user.
   *
   * @param user the user
   * @return the display healing value
   */
  def calculateHealingPower(user: User): Double =
    val baseHealing = user.char.map(_.baseHealingPower).getOrElse(0D)
    val classHealing = user.cls.map(_.classHealingPower).getOrElse(0D)
    baseHealing * classHealing * (1 + user.pctHealing) + user.flatHealing

  /**
   * Calculates the display dexterity value of a user.
   *
   * @param user the user
   * @return the display dexterity value
   */
  def calculateDexterity(user: User): Double =
    val baseDexterity = user.char.map(_.baseDexterity).getOrElse(0D)
    val classDexterity = user.cls.map(_.classDexterity).getOrElse(0D)
    baseDexterity * classDexterity * (1 + user.pctDexterity) + user.flatDexterity

  /**
   * Calculates the display agility value of a user.
   *
   * @param user the user
   * @return the display agility value
   */
  def calculateAgility(user: User): Double =
    val baseAgility = user.char.map(_.baseAgility).getOrElse(0D)
    val classAgility = user.cls.map(_.classAgility).getOrElse(0D)
    baseAgility * classAgility * (1 + user.pctAgility) + user.flatAgility

  /**
   * Calculates the display critical rate value of a user.
   *
   * @param user the user
   * @return the display critical rate value
   */
  def calculateDisplayCritRate(user: User): Double =
    val weaponCrit = user.weapon.map(_.weaponCrit).getOrElse(0D)
    weaponCrit * (1 + user.noncombatPctCrit) + user.noncombatFlatCrit

  /**
   * Calculates the display block rate value of a user.
   *
   * @param user the user
   * @return the display block rate value
   */
  def calculateDisplayBlockRate(user: User): Double =
    val weaponBlock = user.weapon.map(_.weaponBlock).getOrElse(0D)
    weaponBlock * (1 + user.noncombatPctBlock) + user.noncombatFlatBlock

  /**
   * Calculates the display physical defense value of a user.
   *
   * @param user the user
   * @return the display physical defense value
   */
  def calculateDisplayPhysDefense(user: User): Double =
    val physDef = user.cls.map(_.classPhysDef).getOrElse(0D)
    physDef * (1 + user.pctPhysDef) + user.flatPhysDef

  /**
   * Calculates the display ether defense value of a user.
   *
   * @param user the user
   * @return the display ether defense value
   */
  def calculateDisplayEtherDefense(user: User): Double =
    val etherDef = user.cls.map(_.classEtherDef).getOrElse(0D)
    etherDef * (1 + user.pctEtherDef) + user.flatEtherDef
