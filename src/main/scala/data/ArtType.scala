package dev.hawu.plugins.xenocraft
package data

/**
 * Enumerations for all art types.
 * One art type should only correspond to one art object.
 */
enum ArtType(
  val category: ArtCategory,
  val target: ArtTarget,
  val powerMultiplier: Double = 0,
  val cooldown: Double,
  val cooldownType: ArtRechargeType,
  val fusionBonus: ArtFusionBonus = ArtFusionBonus.NONE,
  val hits: Int = 0,
  val isMaster: Boolean = false,
):

  /**
   * Checks if this art type is a talent art.
   *
   * @return whether it is
   */
  def isTalent: Boolean = cooldownType == ArtRechargeType.ROLE_ACTION

  // ================================== //
  // SWORDFIGHTER ARTS
  // ================================== //
  case GROUND_BEAT extends ArtType(
    category = ArtCategory.PHYSICAL,
    target = ArtTarget.FRONT_AOE,
    powerMultiplier = 4.4,
    cooldown = 22.7,
    cooldownType = ArtRechargeType.TIME,
    fusionBonus = ArtFusionBonus.RANGE,
    hits = 1,
    isMaster = true,
  )
  case SWORD_STRIKE extends ArtType(
    category = ArtCategory.PHYSICAL,
    target = ArtTarget.SINGLE,
    powerMultiplier = 2.25,
    cooldown = 22.7,
    cooldownType = ArtRechargeType.TIME,
    hits = 1,
    fusionBonus = ArtFusionBonus.DAMAGE,
  )
  case EDGE_THRUST extends ArtType(
    category = ArtCategory.PHYSICAL,
    target = ArtTarget.SINGLE,
    powerMultiplier = 1.8,
    cooldown = 11,
    cooldownType = ArtRechargeType.TIME,
    hits = 1,
    fusionBonus = ArtFusionBonus.DAMAGE,
  )
  case AIR_SLASH extends ArtType(
    category = ArtCategory.PHYSICAL,
    target = ArtTarget.SINGLE,
    powerMultiplier = 4.25,
    cooldown = 19.3,
    cooldownType = ArtRechargeType.TIME,
    hits = 1,
    fusionBonus = ArtFusionBonus.DAMAGE,
  )
  case SHADOW_EYE extends ArtType(
    category = ArtCategory.BUFF,
    target = ArtTarget.SELF,
    cooldown = 29,
    cooldownType = ArtRechargeType.TIME,
    fusionBonus = ArtFusionBonus.DURATION,
    isMaster = true,
  )
  case OVERCLOCK_BUSTER extends ArtType(
    category = ArtCategory.PHYSICAL,
    target = ArtTarget.SINGLE,
    powerMultiplier = 10.5,
    cooldown = 3,
    cooldownType = ArtRechargeType.ROLE_ACTION,
    hits = 1,
    isMaster = true,
  )

  // ================================== //
  // EXCLUSIVE NOAH'S ARTS
  // ================================== //
  case INFINITY_BLADE extends ArtType(
    category = ArtCategory.PHYSICAL,
    target = ArtTarget.FRONT_AOE,
    powerMultiplier = 10,
    cooldown = 4,
    cooldownType = ArtRechargeType.ROLE_ACTION,
    hits = 2,
    isMaster = true,
  )
