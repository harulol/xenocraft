package dev.hawu.plugins.xenocraft
package data

/** Similar to potion effects, these are types of buffs that a user can have.
  */
enum BuffType(
  val val1: Double = 0,
  val val2: Double = 0,
  val val3: Double = 0,
  val baseDur: Double,
  val maxDur: Double,
  val rng: Boolean,
):

  case CRITICAL_RATE_UP extends BuffType(val1 = 0.25, baseDur = 20, maxDur = 40, rng = true)
  case ACCURACY_UP extends BuffType(val1 = 0.25, baseDur = 20, maxDur = 40, rng = true)

  case POWER_CHARGE
    extends BuffType(val1 = 0.5, baseDur = Double.PositiveInfinity, maxDur = Double.PositiveInfinity, rng = true)

  case ARMOR_VEIL
    extends BuffType(val1 = 0.2, baseDur = Double.PositiveInfinity, maxDur = Double.PositiveInfinity, rng = true)

  case ETHER_ANCHOR extends BuffType(val1 = 1, baseDur = 20, maxDur = 40, rng = true)
  case ATTACK_UP extends BuffType(val1 = 0.25, baseDur = 20, maxDur = 40, rng = true)
  case CRITICAL_HIT_PLUS extends BuffType(val1 = 0.5, baseDur = 20, maxDur = 40, rng = true)
  case RECOVERY_UP extends BuffType(val1 = 0.5, baseDur = 20, maxDur = 40, rng = true)
  case DELAYED_HEALING extends BuffType(val1 = 0.3, val2 = 0.15, baseDur = 20, maxDur = 40, rng = true)
  case COUNTER_HEAL extends BuffType(val1 = 0.05, val2 = 3, baseDur = 15, maxDur = 30, rng = true)
  case PAUSE_DEBUFF_TIMER extends BuffType(baseDur = 20, maxDur = 40, rng = false)
  case INVINCIBLE extends BuffType(baseDur = 10, maxDur = 20, rng = false)
  case AWAKENING extends BuffType(val1 = 0.75, val2 = 0.25, val3 = 0.5, baseDur = 20, maxDur = 40, rng = false)
  case DEFENSE_UP extends BuffType(val1 = 0.15, baseDur = 20, maxDur = 40, rng = true)
  case DECOY extends BuffType(val1 = 1, baseDur = 20, maxDur = 40, rng = true)
  case DEBUFF_BARRIER extends BuffType(val1 = 1, baseDur = 20, maxDur = 40, rng = true)
  case FAST_RECHARGE extends BuffType(val1 = 0.2, baseDur = 20, maxDur = 40, rng = true)
  case EVASION_UP extends BuffType(val1 = 0.1, baseDur = 20, maxDur = 40, rng = true)
  case REGENERATE extends BuffType(val1 = 0.015, baseDur = 30, maxDur = 60, rng = false)

end BuffType
