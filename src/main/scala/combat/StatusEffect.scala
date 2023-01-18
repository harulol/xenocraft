package dev.hawu.plugins.xenocraft
package combat

import org.bukkit.plugin.java.JavaPlugin

import java.io.InputStreamReader

/**
 * Represents a status effect, whether it is a buff
 * or a debuff.
 */
enum StatusEffect(
  val isBuff: Boolean,
  val randomlyApplicable: Boolean,
  private val name: Option[String] = None,
  val physical: Option[Boolean] = None,
):

  /**
   * The computed display name of this status effect.
   *
   * @return the name
   */
  def displayName: String = name.getOrElse(this.toString.split("_").map(_.toLowerCase.capitalize).mkString(" "))

  /**
   * Checks if the status effect is of type physical.
   * Only debuffs have this attribute.
   *
   * @return whether this effect is physical
   */
  def isPhysical = physical.getOrElse(false)

  /**
   * Checks if the status effect is of type ether.
   * Only debuffs have this attribute.
   *
   * @return whether this effect is ether
   */
  def isEther = physical.exists(!_)

  /**
   * Checks if this status effect is not typed.
   * Buffs should not be typed.
   *
   * @return whether this effect is not typed
   */
  def isNotTyped = physical.isEmpty

  case ACCURACY_UP extends StatusEffect(true, true)
  case ARMOR_VEIL extends StatusEffect(true, true)
  case ART_FOLLOW_UP extends StatusEffect(true, false, Some("Art Follow-Up"))
  case ATTACK_UP extends StatusEffect(true, true)
  case AWAKENING extends StatusEffect(true, false)
  case COUNTER_HEAL extends StatusEffect(true, true)
  case CRITICAL_HIT_PLUS extends StatusEffect(true, true)
  case CRITICAL_RATE_UP extends StatusEffect(true, true)
  case DEBUFF_BARRIER extends StatusEffect(true, true)
  case DECOY extends StatusEffect(true, true)
  case DEFENSE_UP extends StatusEffect(true, true)
  case DELAYED_HEALING extends StatusEffect(true, true)
  case ETHER_ANCHOR extends StatusEffect(true, true)
  case EVASION_UP extends StatusEffect(true, true)
  case FAST_RECHARGE extends StatusEffect(true, true)
  case INVINCIBLE extends StatusEffect(true, false)
  case PAUSE_BUFF_TIMER extends StatusEffect(true, false)
  case POWER_CHARGE extends StatusEffect(true, true)
  case RECOVERY_UP extends StatusEffect(true, true)
  case REGENERATE extends StatusEffect(true, false)

  case ACCURACY_DOWN extends StatusEffect(false, true, physical = Some(true))
  case ATTACK_DOWN extends StatusEffect(false, true, physical = Some(true))
  case BIND extends StatusEffect(false, true, physical = Some(false))
  case BLAZE extends StatusEffect(false, false, physical = Some(false))
  case BLEED extends StatusEffect(false, false, physical = Some(true))
  case DAMAGE_LINK extends StatusEffect(false, false, physical = Some(false))
  case ECLIPSE_SOUL extends StatusEffect(false, false, physical = Some(false))
  case ETHER_DEFENSE_DOWN extends StatusEffect(false, true, physical = Some(false))
  case EVASION_DOWN extends StatusEffect(false, true, physical = Some(true))
  case FLAME_CLOCK_LOW extends StatusEffect(false, false, name = Some("Flame Clock Low, Attack Down"), physical = Some(false))
  case FREEZE extends StatusEffect(false, false, physical = Some(false))
  case HEAL_BIND extends StatusEffect(false, false, physical = Some(false))
  case INTERLINK_SEAL extends StatusEffect(false, false, physical = Some(false))
  case MOEBIUS_SHACKLES extends StatusEffect(false, false, physical = Some(true))
  case PHYSICAL_DEF_DOWN extends StatusEffect(false, true, physical = Some(true))
  case REDUCE_ALL extends StatusEffect(false, false, physical = Some(false))
  case RESISTANCE_DOWN extends StatusEffect(false, true, physical = Some(false))
  case SHACKLE_ARTS extends StatusEffect(false, false, physical = Some(true))
  case SHACKLE_BLOCKING extends StatusEffect(false, true, physical = Some(true))
  case SHACKLE_HEALING extends StatusEffect(false, false, physical = Some(false))
  case SHIELD_BIND extends StatusEffect(false, false, physical = Some(false))
  case SLOW_RECHARGE extends StatusEffect(false, false, physical = Some(false))
  case SLEEP extends StatusEffect(false, true, physical = Some(false))
  case SWORD_BIND extends StatusEffect(false, false, physical = Some(false))
  case TARGET_LOCK extends StatusEffect(false, false, physical = Some(true))
  case TOXIN extends StatusEffect(false, false, physical = Some(false))
