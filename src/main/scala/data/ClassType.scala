package dev.hawu.plugins.xenocraft
package data

/**
 * Represents a type of class a player can pick.
 */
enum ClassType(
  val classHp: Double,
  val classAttack: Double,
  val classHealingPower: Double,
  val classDexterity: Double,
  val classAgility: Double,
  val classPhysDef: Double,
  val classEtherDef: Double,
  val weaponType: WeaponType,
  val upgradedWeaponType: Option[WeaponType],
):

  case SWORDFIGHTER extends ClassType(
    classHp = 0.75,
    classAttack = 1.15,
    classHealingPower = 0.35,
    classDexterity = 1.2,
    classAgility = 0.85,
    classPhysDef = 0.15,
    classEtherDef = 0.15,
    weaponType = WeaponType.VEILED_SWORD,
    upgradedWeaponType = Some(WeaponType.TRUTHSINGER),
  )
