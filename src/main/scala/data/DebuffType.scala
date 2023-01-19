package dev.hawu.plugins.xenocraft
package data

/**
 * Represents a debuff a player can be affected with.
 *
 * Some effects are completely impossible for a player
 * to inflict with. Such as [[DebuffType.MOEBIUS_SHACKLES]].
 *
 * I don't know how I'll implement that.
 */
enum DebuffType(
  val physical: Boolean,
  val val1: Double = 0,
  val val2: Double = 0,
  val val3: Double = 0,
  val baseDur: Double = 0,
  val maxDur: Double = 0,
  val baseRate: Double = 0,
  val rng: Boolean,
):

  case ATTACK_DOWN extends DebuffType(
    physical = true,
    val1 = -0.25,
    baseDur = 15,
    maxDur = 30,
    baseRate = 0.75,
    rng = true,
  )
  case PHYS_DEF_DOWN extends DebuffType(
    physical = true,
    val1 = 0.25,
    baseDur = 15,
    maxDur = 30,
    baseRate = 0.75,
    rng = true,
  )
  case TARGET_LOCK extends DebuffType(
    physical = true,
    baseDur = 10,
    maxDur = 15,
    baseRate = 1,
    rng = false,
  )
  case SHACKLE_BLOCKING extends DebuffType(
    physical = true,
    baseDur = 15,
    maxDur = 30,
    baseRate = 0.75,
    rng = true,
  )
  case ACCURACY_DOWN extends DebuffType(
    physical = true,
    val1 = -0.25,
    baseDur = 15,
    maxDur = 30,
    baseRate = 0.75,
    rng = true,
  )
  case EVASION_DOWN extends DebuffType(
    physical = true,
    val1 = 0.25,
    baseDur = 15,
    maxDur = 30,
    baseRate = 0.75,
    rng = true,
  )
  case BLEED extends DebuffType(
    physical = true,
    val1 = 0.04,
    baseDur = 46,
    maxDur = 92,
    baseRate = 1,
    rng = false,
  )
  case SHACKLE_ARTS extends DebuffType(
    physical = true,
    baseDur = 15,
    maxDur = 30,
    baseRate = 0.75,
    rng = false,
  )

  case ECLIPSE_SOUL extends DebuffType(
    physical = false,
    baseDur = 15,
    maxDur = 30,
    baseRate = 1,
    rng = false,
  )
  case ETHER_DEFENSE_DOWN extends DebuffType(
    physical = false,
    val1 = 0.25,
    baseDur = 15,
    maxDur = 30,
    baseRate = 0.75,
    rng = true,
  )
  case FLAME_CLOCK_LOW_ATTACK_DOWN extends DebuffType(
    physical = false,
    baseDur = Double.PositiveInfinity,
    maxDur = Double.PositiveInfinity,
    baseRate = 1,
    rng = false,
  )
  case RESISTANCE_DOWN extends DebuffType(
    physical = false,
    val1 = -0.25,
    baseDur = 15,
    maxDur = 30,
    baseRate = 0.75,
    rng = true,
  )
  case SHACKLE_HEALING extends DebuffType(
    physical = false,
    baseDur = 15,
    maxDur = 30,
    baseRate = 0.75,
    rng = false,
  )
  case SLEEP extends DebuffType(
    physical = false,
    baseDur = 20,
    maxDur = 30,
    baseRate = 0.6,
    rng = true,
  )
  case INTERLINK_SEAL extends DebuffType(
    physical = false,
    baseDur = Double.PositiveInfinity,
    maxDur = Double.PositiveInfinity,
    baseRate = 1,
    rng = false,
  )
  case MOEBIUS_SHACKLES extends DebuffType(
    physical = false,
    baseDur = 7.5,
    maxDur = 11.25,
    baseRate = 1,
    rng = false,
  )
  case BIND extends DebuffType(
    physical = false,
    baseDur = 7.5,
    maxDur = 11.25,
    baseRate = 0.6,
    rng = true,
  )
  case HEAL_BIND extends DebuffType(
    physical = false,
    baseDur = 7.5,
    maxDur = 11.25,
    baseRate = 1,
    rng = false,
  )
  case SHIELD_BIND extends DebuffType(
    physical = false,
    baseDur = 7.5,
    maxDur = 11.25,
    baseRate = 1,
    rng = false,
  )
  case SWORD_BIND extends DebuffType(
    physical = false,
    baseDur = 7.5,
    maxDur = 11.25,
    baseRate = 1,
    rng = false,
  )
  case DAMAGE_LINK extends DebuffType(
    physical = false,
    val1 = 5,
    baseDur = 15,
    maxDur = 30,
    baseRate = 1.0,
    rng = false,
  )
  case REDUCE_ALL extends DebuffType(
    physical = false,
    val1 = -0.3,
    val2 = -0.5,
    val3 = -0.3,
    baseDur = 60,
    maxDur = 60,
    baseRate = Double.PositiveInfinity,
    rng = false,
  )
  case SLOW_RECHARGE extends DebuffType(
    physical = false,
    val1 = -0.4,
    baseDur = 20,
    maxDur = 40,
    baseRate = 0.75,
    rng = false,
  )
  case BLAZE extends DebuffType(
    physical = false,
    val1 = 0.08,
    baseDur = 10,
    maxDur = 20,
    baseRate = 1,
    rng = false,
  )
  case FREEZE extends DebuffType(
    physical = false,
    val1 = 0.06,
    baseDur = 21.5,
    maxDur = 43,
    baseRate = 1,
    rng = false,
  )
  case TOXIN extends DebuffType(
    physical = false,
    val1 = 0.12,
    baseDur = 24,
    maxDur = 48,
    baseRate = 1,
    rng = false,
  )
