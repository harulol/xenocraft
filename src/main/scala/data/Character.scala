package dev.hawu.plugins.xenocraft
package data

import org.bukkit.Material

/**
 * Represents a character that a user can select
 * as the base stats holder.
 */
enum Character(
  val icon: Material,
  val baseHp: Double,
  val baseAttack: Double,
  val baseHealingPower: Double,
  val baseDexterity: Double,
  val baseAgility: Double,
):

  import org.bukkit.Material

  var description = "No description set."

  case NOAH extends Character(
    icon = Material.IRON_SWORD,
    baseHp = 6994,
    baseAttack = 388,
    baseHealingPower = 368,
    baseDexterity = 310,
    baseAgility = 215,
  )
  case MIO extends Character(
    icon = Material.ENDER_PEARL,
    baseHp = 6754,
    baseAttack = 361,
    baseHealingPower = 341,
    baseDexterity = 306,
    baseAgility = 234,
  )
  case EUNIE extends Character(
    icon = Material.FEATHER,
    baseHp = 6364,
    baseAttack = 346,
    baseHealingPower = 409,
    baseDexterity = 298,
    baseAgility = 223,
  )
  case TAION extends Character(
    icon = Material.PAPER,
    baseHp = 6371,
    baseAttack = 368,
    baseHealingPower = 381,
    baseDexterity = 325,
    baseAgility = 197,
  )
  case LANZ extends Character(
    icon = Material.DIAMOND_CHESTPLATE,
    baseHp = 7755,
    baseAttack = 392,
    baseHealingPower = 329,
    baseDexterity = 297,
    baseAgility = 284,
  )
  case SENA extends Character(
    icon = Material.DIAMOND_AXE,
    baseHp = 7125,
    baseAttack = 398,
    baseHealingPower = 346,
    baseDexterity = 282,
    baseAgility = 216,
  )
