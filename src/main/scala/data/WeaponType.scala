package dev.hawu.plugins.xenocraft
package data

import org.bukkit.Material

/**
 * Represents a type of Blade a class holder can have.
 *
 * For probably good reasons, only the main cast's Blades
 * have upgraded versions, which is why there 2 Blades with
 * different stats that are bound to the same class type.
 *
 * See [[ClassType]].
 */
enum WeaponType(
  val material: Material,
  val weaponAttack: Double,
  val weaponCrit: Double,
  val weaponBlock: Double,
  val weaponStability: Double,
):

  case VEILED_SWORD extends WeaponType(
    material = Material.IRON_SWORD,
    weaponAttack = 575,
    weaponCrit = 0.2,
    weaponBlock = 0.1,
    weaponStability = 0.08,
  )
  case TRUTHSINGER extends WeaponType(
    material = Material.DIAMOND_SWORD,
    weaponAttack = 690,
    weaponCrit = 0.24,
    weaponBlock = 0.12,
    weaponStability = 0.08,
  )
