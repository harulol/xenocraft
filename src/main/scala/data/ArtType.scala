package dev.hawu.plugins.xenocraft
package data

import dev.hawu.plugins.api.i18n.Locale
import dev.hawu.plugins.xenocraft.arts.ArtManager
import dev.hawu.plugins.xenocraft.gui.ArtsGUI
import org.bukkit.Material

/** Enumerations for all art types. One art type should only correspond to one art object.
  */
enum ArtType(
  val cls: Option[ClassType] = None,
  val category: ArtCategory,
  val target: ArtTarget,
  val powerMultiplier: Double = 0,
  val cooldown: Double,
  val cooldownType: ArtRechargeType,
  val reaction: Option[ArtReaction] = None,
  val fusionBonus: ArtFusionBonus = ArtFusionBonus.NONE,
  val hits: Int = 0,
  val isMaster: Boolean = false,
):

  /** Attempts to retrieve the icon for this specific art type.
    *
    * @return
    *   the icon
    */
  def icon: Material = ArtManager.getIcon(this)

  /** Attempts to retrieve the name for this specific art type.
    *
    * @param locale
    *   The locale to use.
    * @return
    *   the name
    */
  def name(locale: Locale): String = ArtsGUI.getModule.translate(locale, s"${toString.toLowerCase.replace('_', '-')}")

  /** Attempts to retrieve the description for this specific art type.
    *
    * @param locale
    *   The locale to use.
    * @return
    *   the description
    */
  def description(locale: Locale): String = ArtsGUI.getModule
    .translate(locale, s"${toString.toLowerCase.replace('_', '-')}-desc")

  /** Checks if this art is a soulhacker art. Soulhacker is annoyingly different as it is a class that has its own set
    * of arts and skills, completely unrelated.
    *
    * And also talent arts are not available for soulhacker except Final Countdown, but Noah and Mio can use their
    * respective exclusive arts still.
    *
    * @return
    */
  def isSoulhacker: Boolean = cls.exists(_.isSoulhacker)

  /** Checks if this art type is a talent art.
    *
    * @return
    *   whether it is
    */
  def isTalent: Boolean = cooldownType == ArtRechargeType.ROLE_ACTION

  /** Checks if this art type is a Kevesi's art.
    *
    * @return
    *   whether it is a Kevesi's
    */
  def isKevesi: Boolean = cooldownType == ArtRechargeType.TIME

  /** Checks if this art type is an Agnian's art.
    *
    * @return
    *   whether it is an Agnian's
    */
  def isAgnian: Boolean = cooldownType == ArtRechargeType.AUTO_ATTACK

  case GROUND_BEAT
    extends ArtType(
      cls = Some(ClassType.SWORDFIGHTER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 4.4,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.RANGE,
      hits = 1,
      isMaster = true,
    )

  case SWORD_STRIKE
    extends ArtType(
      cls = Some(ClassType.SWORDFIGHTER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.25,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      reaction = Some(ArtReaction.BREAK),
      hits = 1,
      fusionBonus = ArtFusionBonus.DAMAGE,
    )

  case EDGE_THRUST
    extends ArtType(
      cls = Some(ClassType.SWORDFIGHTER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.8,
      cooldown = 11,
      cooldownType = ArtRechargeType.TIME,
      hits = 1,
      fusionBonus = ArtFusionBonus.DAMAGE,
    )

  case AIR_SLASH
    extends ArtType(
      cls = Some(ClassType.SWORDFIGHTER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4.25,
      cooldown = 19.3,
      cooldownType = ArtRechargeType.TIME,
      hits = 1,
      fusionBonus = ArtFusionBonus.DAMAGE,
    )

  case SHADOW_EYE
    extends ArtType(
      cls = Some(ClassType.SWORDFIGHTER),
      category = ArtCategory.BUFF,
      target = ArtTarget.SELF,
      cooldown = 29,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
      isMaster = true,
    )

  case OVERCLOCK_BUSTER
    extends ArtType(
      cls = Some(ClassType.SWORDFIGHTER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 10.5,
      cooldown = 3,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      hits = 1,
      isMaster = true,
    )

  case BUTTERFLY_BLADE
    extends ArtType(
      cls = Some(ClassType.ZEPHYR),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.65,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.AGGRO,
      hits = 2,
    )

  case AIR_FANG
    extends ArtType(
      cls = Some(ClassType.ZEPHYR),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.5,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.AGGRO,
      hits = 2,
    )

  case WIDE_SLASH
    extends ArtType(
      cls = Some(ClassType.ZEPHYR),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.6,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2, // 3 FOR MIO, 2 FOR OTHERS
      isMaster = true,
    )

  case GLOW_RING
    extends ArtType(
      cls = Some(ClassType.ZEPHYR),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 1,
      isMaster = true,
    )

  case SPEED_DEMON
    extends ArtType(
      cls = Some(ClassType.ZEPHYR),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case GEMINI_STRIKE
    extends ArtType(
      cls = Some(ClassType.ZEPHYR),
      category = ArtCategory.PHYSICAL, // PHYSICAL FOR OTHERS, ETHER FOR MIO
      target = ArtTarget.SINGLE,
      powerMultiplier = 7,
      cooldown = 3,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      hits = 2,
      isMaster = true,
    )

  case MYOPIC_SCREEN
    extends ArtType(
      cls = Some(ClassType.MEDIC_GUNNER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.4,
      cooldown = 11.3,
      cooldownType = ArtRechargeType.TIME,
      reaction = Some(ArtReaction.DAZE),
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      isMaster = true,
    )

  case GROUP_HEAL
    extends ArtType(
      cls = Some(ClassType.MEDIC_GUNNER),
      category = ArtCategory.HEALING,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 2.25,
      cooldown = 14.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case POWER_RING
    extends ArtType(
      cls = Some(ClassType.MEDIC_GUNNER),
      category = ArtCategory.FIELD,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 13.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case ETHER_CANNON
    extends ArtType(
      cls = Some(ClassType.MEDIC_GUNNER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4.85,
      cooldown = 24.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      isMaster = true,
    )

  case VORTEX
    extends ArtType(
      cls = Some(ClassType.MEDIC_GUNNER),
      category = ArtCategory.ETHER,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 4.1,
      cooldown = 19.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.RANGE,
      hits = 1,
    )

  case HEALING_RING
    extends ArtType(
      cls = Some(ClassType.MEDIC_GUNNER),
      category = ArtCategory.FIELD,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 4,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      isMaster = true,
    )

  case OVERFALL
    extends ArtType(
      cls = Some(ClassType.TACTICIAN),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.75,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 1,
      isMaster = true,
    )

  case STORMY_SKIES
    extends ArtType(
      cls = Some(ClassType.TACTICIAN),
      category = ArtCategory.HEALING,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 2.45,
      cooldown = 4,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.HEALING,
    )

  case HAZY_FIGURE
    extends ArtType(
      cls = Some(ClassType.TACTICIAN),
      category = ArtCategory.FIELD,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.RANGE,
      hits = 1,
    )

  case AVENGING_EAGLE
    extends ArtType(
      cls = Some(ClassType.TACTICIAN),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 3.1,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.RANGE,
      hits = 1,
    )

  case DARK_BANNER
    extends ArtType(
      cls = Some(ClassType.TACTICIAN),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 1.65,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 1,
      isMaster = true,
    )

  case TIDAL_WAVE
    extends ArtType(
      cls = Some(ClassType.TACTICIAN),
      category = ArtCategory.ETHER,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 7,
      cooldown = 4,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      hits = 1,
      isMaster = true,
    )

  case BULL_RUSH
    extends ArtType(
      cls = Some(ClassType.HEAVY_GUARD),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.4,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      reaction = Some(ArtReaction.TOPPLE),
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case CRASH_OUT
    extends ArtType(
      cls = Some(ClassType.HEAVY_GUARD),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 3.4,
      cooldown = 14.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.AGGRO,
      hits = 1,
      isMaster = true,
    )

  case UPPERCUT_SLASH
    extends ArtType(
      cls = Some(ClassType.HEAVY_GUARD),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4.4,
      cooldown = 17.5,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.AGGRO,
      hits = 1,
    )

  case VARIABLE_TURRET
    extends ArtType(
      cls = Some(ClassType.HEAVY_GUARD),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.4,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 25,
    )

  case SOLID_STANCE
    extends ArtType(
      cls = Some(ClassType.HEAVY_GUARD),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 36,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
      isMaster = true,
    )

  case MAD_TAUNT
    extends ArtType(
      cls = Some(ClassType.HEAVY_GUARD),
      category = ArtCategory.TAUNT,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 4,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      isMaster = true,
    )

  case BIG_IMPACT
    extends ArtType(
      cls = Some(ClassType.OGRE),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.75,
      cooldown = 7,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      reaction = Some(ArtReaction.BLOWDOWN),
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case HAMMERHEAD
    extends ArtType(
      cls = Some(ClassType.OGRE),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.65,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case GIANT_SWING
    extends ArtType(
      cls = Some(ClassType.OGRE),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4,
      cooldown = 6,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      reaction = Some(ArtReaction.KNOCKBACK),
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
      isMaster = true,
    )

  case CANNONBALL
    extends ArtType(
      cls = Some(ClassType.OGRE),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case MAXIMUM_VOLTAGE
    extends ArtType(
      cls = Some(ClassType.OGRE),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 9,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      isMaster = true,
    )

  case PRESSURE_DROP
    extends ArtType(
      cls = Some(ClassType.OGRE),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 7,
      cooldown = 3,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      reaction = Some(ArtReaction.LAUNCH),
      hits = 2,
      isMaster = true,
    )

  case CROSS_IMPACT
    extends ArtType(
      cls = Some(ClassType.FLASH_FENCER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.8,
      cooldown = 14.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
      isMaster = true,
    )

  case NULL_SLASH
    extends ArtType(
      cls = Some(ClassType.FLASH_FENCER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 2.75,
      cooldown = 11,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case HIDDEN_THORN
    extends ArtType(
      cls = Some(ClassType.FLASH_FENCER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4.4,
      cooldown = 27.2,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
      isMaster = true,
    )

  case REVOLUTION_EDGE
    extends ArtType(
      cls = Some(ClassType.FLASH_FENCER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.55,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case ACCELERATION
    extends ArtType(
      cls = Some(ClassType.FLASH_FENCER),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 36,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case SABER_STRIKE
    extends ArtType(
      cls = Some(ClassType.FLASH_FENCER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 5.25,
      cooldown = 3,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      reaction = Some(ArtReaction.SMASH),
      hits = 1,
      isMaster = true,
    )

  case HEAL_BULLET
    extends ArtType(
      cls = Some(ClassType.WAR_MEDIC),
      category = ArtCategory.HEALING,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.25,
      cooldown = 13.5,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.HEALING,
    )

  case MULTI_BLAST
    extends ArtType(
      cls = Some(ClassType.WAR_MEDIC),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.4,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 3,
      isMaster = true,
    )

  case ADVANCED_COOLDOWN
    extends ArtType(
      cls = Some(ClassType.WAR_MEDIC),
      category = ArtCategory.FIELD,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 13.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
      isMaster = true,
    )

  case CURE_BULLET
    extends ArtType(
      cls = Some(ClassType.WAR_MEDIC),
      category = ArtCategory.CLEANSE,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 29,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case VITAL_BULLET
    extends ArtType(
      cls = Some(ClassType.WAR_MEDIC),
      category = ArtCategory.BUFF,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case TECHNICAL_HEAL
    extends ArtType(
      cls = Some(ClassType.WAR_MEDIC),
      category = ArtCategory.HEALING,
      target = ArtTarget.PARTY,
      powerMultiplier = 2.5,
      cooldown = 5,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      isMaster = true,
    )

  case NOBLE_TAUNT
    extends ArtType(
      cls = Some(ClassType.GUARDIAN_COMMANDER),
      category = ArtCategory.TAUNT,
      target = ArtTarget.SINGLE,
      cooldown = 14.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.AGGRO,
      isMaster = true,
    )

  case SHIELD_BASH
    extends ArtType(
      cls = Some(ClassType.GUARDIAN_COMMANDER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      reaction = Some(ArtReaction.TOPPLE),
      fusionBonus = ArtFusionBonus.AGGRO,
      hits = 2,
      isMaster = true,
    )

  case FALCON_SWOOP
    extends ArtType(
      cls = Some(ClassType.GUARDIAN_COMMANDER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 2.7,
      cooldown = 11,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case GRAND_PROTECTION
    extends ArtType(
      cls = Some(ClassType.GUARDIAN_COMMANDER),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 36,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case IMPERIAL_SWORD
    extends ArtType(
      cls = Some(ClassType.GUARDIAN_COMMANDER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4.4,
      cooldown = 19.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case DEFLECTOR_FIELD
    extends ArtType(
      cls = Some(ClassType.GUARDIAN_COMMANDER),
      category = ArtCategory.DEFENSE,
      target = ArtTarget.SELF,
      cooldown = 4,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      isMaster = true,
    )

  case RANK_SPLITTER
    extends ArtType(
      cls = Some(ClassType.THAUMATURGE),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case VIOLENT_FLURRY
    extends ArtType(
      cls = Some(ClassType.THAUMATURGE),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
      isMaster = true,
    )

  case DIVINE_PROTECTION
    extends ArtType(
      cls = Some(ClassType.THAUMATURGE),
      category = ArtCategory.FIELD,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case LEAPING_CURRENT
    extends ArtType(
      cls = Some(ClassType.THAUMATURGE),
      category = ArtCategory.CLEANSE,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case SPEARPOINT_THRUST
    extends ArtType(
      cls = Some(ClassType.THAUMATURGE),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 7,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 1,
      isMaster = true,
    )

  case DEMISE_THRUST
    extends ArtType(
      cls = Some(ClassType.THAUMATURGE),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 7,
      cooldown = 3,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      reaction = Some(ArtReaction.BURST),
      hits = 1,
      isMaster = true,
    )

  case EXTENDED_SLASH
    extends ArtType(
      cls = Some(ClassType.YUMSMITH),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4,
      cooldown = 19.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 1,
    )

  case FLASHBACK
    extends ArtType(
      cls = Some(ClassType.YUMSMITH),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.8,
      cooldown = 11,
      cooldownType = ArtRechargeType.TIME,
      reaction = Some(ArtReaction.BREAK),
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
      isMaster = true,
    )

  case ENERGY_GRENADE
    extends ArtType(
      cls = Some(ClassType.YUMSMITH),
      category = ArtCategory.ETHER,
      target = ArtTarget.ENEMY_AOE,
      powerMultiplier = 1.8,
      cooldown = 13.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      isMaster = true,
    )

  case ENERGY_FIELD
    extends ArtType(
      cls = Some(ClassType.YUMSMITH),
      category = ArtCategory.FIELD,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 13.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case DETERMINATION
    extends ArtType(
      cls = Some(ClassType.YUMSMITH),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 36,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case SWORD_OF_VALOR
    extends ArtType(
      cls = Some(ClassType.YUMSMITH),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 10.5,
      cooldown = 4,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      hits = 1,
      isMaster = true,
    )

  case QUICKDRAW
    extends ArtType(
      cls = Some(ClassType.FULL_METAL_JAGUAR),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.7,
      cooldown = 11,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
      isMaster = true,
    )

  case DEAD_SET
    extends ArtType(
      cls = Some(ClassType.FULL_METAL_JAGUAR),
      category = ArtCategory.BUFF,
      target = ArtTarget.SELF,
      cooldown = 29,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case FATAL_BARRAGE
    extends ArtType(
      cls = Some(ClassType.FULL_METAL_JAGUAR),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 3.4,
      cooldown = 19.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 11, // 11 hits for males, 8 hits for females
    )

  case SUPERSTORM
    extends ArtType(
      cls = Some(ClassType.FULL_METAL_JAGUAR),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.4,
      cooldown = 14.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
    )

  case DEMOLITION
    extends ArtType(
      cls = Some(ClassType.FULL_METAL_JAGUAR),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 4.4,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 4,
      isMaster = true,
    )

  case VIOLENT_STREAK
    extends ArtType(
      cls = Some(ClassType.FULL_METAL_JAGUAR),
      category = ArtCategory.ETHER,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 7,
      cooldown = 4,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      hits = 12,
      isMaster = true,
    )

  case NO_LOVE_LOST
    extends ArtType(
      cls = Some(ClassType.STRATEGOS),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 4,
      isMaster = true,
    )

  case HORNET
    extends ArtType(
      cls = Some(ClassType.STRATEGOS),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
      isMaster = true,
    )

  case MOONDOG
    extends ArtType(
      cls = Some(ClassType.STRATEGOS),
      category = ArtCategory.FIELD,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 4,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case EARTH_BIND
    extends ArtType(
      cls = Some(ClassType.STRATEGOS),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.65,
      cooldown = 7,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 1,
    )

  case MIRROR_TRAP
    extends ArtType(
      cls = Some(ClassType.STRATEGOS),
      category = ArtCategory.BUFF,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case SOULFIRE
    extends ArtType(
      cls = Some(ClassType.STRATEGOS),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 7,
      cooldown = 4,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      hits = 1,
      isMaster = true,
    )

  case LANDSLIDE
    extends ArtType(
      cls = Some(ClassType.STALKER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.9,
      cooldown = 4,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case STAYING_POWER
    extends ArtType(
      cls = Some(ClassType.STALKER),
      category = ArtCategory.FIELD,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      isMaster = true,
    )

  case DRIVING_ARROW
    extends ArtType(
      cls = Some(ClassType.STALKER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case RAPID_SHOT
    extends ArtType(
      cls = Some(ClassType.STALKER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 5.2,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case NIGHT_HUNT
    extends ArtType(
      cls = Some(ClassType.STALKER),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      isMaster = true,
    )

  case REFRESHING_RAIN
    extends ArtType(
      cls = Some(ClassType.STALKER),
      category = ArtCategory.ETHER,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 7,
      cooldown = 3,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 9,
      isMaster = true,
    )

  case DEMON_SLAYER
    extends ArtType(
      cls = Some(ClassType.LONE_EXILE),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.8,
      cooldown = 10,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.AGGRO,
      hits = 2,
    )

  case EYE_OF_THE_STORM
    extends ArtType(
      cls = Some(ClassType.LONE_EXILE),
      category = ArtCategory.DEFENSE,
      target = ArtTarget.SELF,
      cooldown = 24.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.BLOCK,
    )

  case GALE_SLASH
    extends ArtType(
      cls = Some(ClassType.LONE_EXILE),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 3.4,
      cooldown = 17.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 5,
      isMaster = true,
    )

  case SOARING_TEMPEST
    extends ArtType(
      cls = Some(ClassType.LONE_EXILE),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.4,
      cooldown = 19.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.AGGRO,
      reaction = Some(ArtReaction.LAUNCH),
      hits = 6,
      isMaster = true,
    )

  case JAWS_OF_DEATH
    extends ArtType(
      cls = Some(ClassType.LONE_EXILE),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 36,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case BLOSSOM_DANCE
    extends ArtType(
      cls = Some(ClassType.LONE_EXILE),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 7,
      cooldown = 2,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 7,
      isMaster = true,
    )

  case REVERSAL_EDGE
    extends ArtType(
      cls = Some(ClassType.INCURSOR),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.65,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case GLITTER_STREAM
    extends ArtType(
      cls = Some(ClassType.INCURSOR),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.5,
      cooldown = 7,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      reaction = Some(ArtReaction.BREAK),
      hits = 1,
      isMaster = true,
    )

  case SIDEWINDER
    extends ArtType(
      cls = Some(ClassType.INCURSOR),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 3.1,
      cooldown = 4,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.RANGE,
      hits = 1,
    )

  case AERIAL_SLASH
    extends ArtType(
      cls = Some(ClassType.INCURSOR),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      isMaster = true,
    )

  case MYSTIC_VISION
    extends ArtType(
      cls = Some(ClassType.INCURSOR),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case SUPREME_SWORD
    extends ArtType(
      cls = Some(ClassType.INCURSOR),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 7,
      cooldown = 3,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      hits = 2,
      isMaster = true,
    )

  case SHOCKWAVE
    extends ArtType(
      cls = Some(ClassType.LOST_VANGUARD),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 4,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      reaction = Some(ArtReaction.KNOCKBACK),
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
    )

  case MIGHTY_BEAT
    extends ArtType(
      cls = Some(ClassType.LOST_VANGUARD),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 2.5,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.RANGE,
      reaction = Some(ArtReaction.SMASH),
      hits = 1,
      isMaster = true,
    )

  case SHIELD_WALL
    extends ArtType(
      cls = Some(ClassType.LOST_VANGUARD),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      isMaster = true,
    )

  case ELECTRO_FIELD
    extends ArtType(
      cls = Some(ClassType.LOST_VANGUARD),
      category = ArtCategory.FIELD,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 7,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case TAUNT_PULSE
    extends ArtType(
      cls = Some(ClassType.LOST_VANGUARD),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case THUNDER_STEEL
    extends ArtType(
      cls = Some(ClassType.LOST_VANGUARD),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 7,
      cooldown = 3,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      isMaster = true,
    )

  case RESONANT_FLAG
    extends ArtType(
      cls = Some(ClassType.SIGNIFIER),
      category = ArtCategory.BUFF,
      target = ArtTarget.PARTY,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      isMaster = true,
    )

  case AUREOLE
    extends ArtType(
      cls = Some(ClassType.SIGNIFIER),
      category = ArtCategory.BUFF,
      target = ArtTarget.SELF,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      isMaster = true,
    )

  case HEAL_HARMONY
    extends ArtType(
      cls = Some(ClassType.SIGNIFIER),
      category = ArtCategory.FIELD,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case ILLUMINATE
    extends ArtType(
      cls = Some(ClassType.SIGNIFIER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 3.1,
      cooldown = 4,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case SPIRITED_CHARGE
    extends ArtType(
      cls = Some(ClassType.SIGNIFIER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.65,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case CRY_OF_FAITH
    extends ArtType(
      cls = Some(ClassType.SIGNIFIER),
      category = ArtCategory.BUFF,
      target = ArtTarget.PARTY,
      cooldown = 6,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      isMaster = true,
    )

  case JACKAL_CLAW
    extends ArtType(
      cls = Some(ClassType.MARTIAL_ARTIST),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      isMaster = true,
    )

  case DETONATING_HIT
    extends ArtType(
      cls = Some(ClassType.MARTIAL_ARTIST),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.5,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      reaction = Some(ArtReaction.DAZE),
    )

  case IMPULSE_WAVE
    extends ArtType(
      cls = Some(ClassType.MARTIAL_ARTIST),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 1.65,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      isMaster = true,
    )

  case DESTRUCTIVE_CHARGE
    extends ArtType(
      cls = Some(ClassType.MARTIAL_ARTIST),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      reaction = Some(ArtReaction.BLOWDOWN),
    )

  case FOCUS
    extends ArtType(
      cls = Some(ClassType.MARTIAL_ARTIST),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case RAGING_FORCE
    extends ArtType(
      cls = Some(ClassType.MARTIAL_ARTIST),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 7,
      cooldown = 2,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      hits = 1,
      isMaster = true,
    )

  case AUTUMN_RAIN
    extends ArtType(
      cls = Some(ClassType.TROUBADOUR),
      category = ArtCategory.BUFF,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case WAY_HOME
    extends ArtType(
      cls = Some(ClassType.TROUBADOUR),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      reaction = Some(ArtReaction.BURST),
      isMaster = true,
    )

  case RING_OF_ROSES
    extends ArtType(
      cls = Some(ClassType.TROUBADOUR),
      category = ArtCategory.FIELD,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      isMaster = true,
    )

  case TWIN_MOONS
    extends ArtType(
      cls = Some(ClassType.TROUBADOUR),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
    )

  case SHADOW_PARADE
    extends ArtType(
      cls = Some(ClassType.TROUBADOUR),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case GLITTERING_MELODY
    extends ArtType(
      cls = Some(ClassType.TROUBADOUR),
      category = ArtCategory.BUFF,
      target = ArtTarget.PARTY,
      cooldown = 9,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      isMaster = true,
    )

  case DRAGONS_SNARL
    extends ArtType(
      cls = Some(ClassType.SERAPH),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.1,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case CRIMSON_LANCE
    extends ArtType(
      cls = Some(ClassType.SERAPH),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      reaction = Some(ArtReaction.TOPPLE),
      isMaster = true,
    )

  case INFERNO_DANCE
    extends ArtType(
      cls = Some(ClassType.SERAPH),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 5,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.RANGE,
      hits = 1,
    )

  case FLAMELORD
    extends ArtType(
      cls = Some(ClassType.SERAPH),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.5,
      cooldown = 4,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      isMaster = true,
    )

  case LEVELING_FIRE
    extends ArtType(
      cls = Some(ClassType.SERAPH),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case EMPYREAN_SALVO
    extends ArtType(
      cls = Some(ClassType.SERAPH),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 7.0,
      cooldown = 3,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      hits = 1,
      isMaster = true,
    )

  case TYRANT_CROSS
    extends ArtType(
      cls = Some(ClassType.MACHINE_ASSASSIN),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.7,
      cooldown = 24.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      isMaster = true,
    )

  case FATAL_BITE
    extends ArtType(
      cls = Some(ClassType.MACHINE_ASSASSIN),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.4,
      cooldown = 14.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
      isMaster = true,
    )

  case TERROR_ONSLAUGHT
    extends ArtType(
      cls = Some(ClassType.MACHINE_ASSASSIN),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.4,
      cooldown = 19.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 1,
    )

  case HAVOC_SLAYER
    extends ArtType(
      cls = Some(ClassType.MACHINE_ASSASSIN),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 4.4,
      cooldown = 24.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
    )

  case MEGA_CRESCENT
    extends ArtType(
      cls = Some(ClassType.MACHINE_ASSASSIN),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.8,
      cooldown = 11,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case VIRAL_VECTOR
    extends ArtType(
      cls = Some(ClassType.MACHINE_ASSASSIN),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.ENEMY_AOE,
      powerMultiplier = 7.0,
      cooldown = 4,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      hits = 2,
      isMaster = true,
    )

  case SABER_SLASH
    extends ArtType(
      cls = Some(ClassType.LIFESAGE),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case REDEMPTION
    extends ArtType(
      cls = Some(ClassType.LIFESAGE),
      category = ArtCategory.HEALING,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 2.45,
      cooldown = 4,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.HEALING,
    )

  case WATERLILY
    extends ArtType(
      cls = Some(ClassType.LIFESAGE),
      category = ArtCategory.HEALING,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case HYDRO_BLAST
    extends ArtType(
      cls = Some(ClassType.LIFESAGE),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4.0,
      cooldown = 4,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      reaction = Some(ArtReaction.BLOWDOWN),
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      isMaster = true,
    )

  case AQUA_MIND
    extends ArtType(
      cls = Some(ClassType.LIFESAGE),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      isMaster = true,
    )

  case DIVINE_SWORD
    extends ArtType(
      cls = Some(ClassType.LIFESAGE),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 10.5,
      cooldown = 5,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      hits = 4,
      isMaster = true,
    )

  case SUMMON_ELEMENT
    extends ArtType(
      cls = Some(ClassType.ROYAL_SUMMONER),
      category = ArtCategory.ETHER,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 1.8,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      isMaster = true,
    )

  case ELEMENTAL_DISCHARGE
    extends ArtType(
      cls = Some(ClassType.ROYAL_SUMMONER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.4,
      cooldown = 11.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      isMaster = true,
    )

  case SHADOW_STITCH
    extends ArtType(
      cls = Some(ClassType.ROYAL_SUMMONER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.4,
      cooldown = 24.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 1,
    )

  case STARFALL
    extends ArtType(
      cls = Some(ClassType.ROYAL_SUMMONER),
      category = ArtCategory.ETHER,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 4.4,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      reaction = Some(ArtReaction.TOPPLE),
    )

  case POWER_EFFECT
    extends ArtType(
      cls = Some(ClassType.ROYAL_SUMMONER),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 36,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case ELEMENT_GENESIS
    extends ArtType(
      cls = Some(ClassType.ROYAL_SUMMONER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 10.5,
      cooldown = 4,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 6,
      isMaster = true,
    )

  case DUAL_FLASH
    extends ArtType(
      cls = Some(ClassType.NOPONIC_CHAMPION),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.AGGRO,
      hits = 2,
    )

  case REFRACTIVE_EDGE
    extends ArtType(
      cls = Some(ClassType.NOPONIC_CHAMPION),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.2,
      cooldown = 9,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case TRI_SLASH
    extends ArtType(
      cls = Some(ClassType.NOPONIC_CHAMPION),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.65,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      reaction = Some(ArtReaction.BREAK),
      isMaster = true,
    )

  case LUSTROUS_SPIKE
    extends ArtType(
      cls = Some(ClassType.NOPONIC_CHAMPION),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.5,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
      isMaster = true,
    )

  case DAZZLING_ILLUSION
    extends ArtType(
      cls = Some(ClassType.NOPONIC_CHAMPION),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case SHINING_REFRAIN
    extends ArtType(
      cls = Some(ClassType.NOPONIC_CHAMPION),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 7.0,
      cooldown = 4,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      hits = 8,
      isMaster = true,
    )

  // ================================== //
  // SOULHACKER ARTS
  // ================================== //
  case HARD_DIG
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.8,
      cooldown = 14.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 3,
      reaction = Some(ArtReaction.KNOCKBACK),
    )

  case BEAST_HOWL
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 1.8,
      cooldown = 10,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.AGGRO,
      hits = 1,
    )

  case THUNDERCLAP
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 3.4,
      cooldown = 19.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 4,
    )

  case RAID_STRIKE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.7,
      cooldown = 11.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
    )

  case SNAKE_EYES
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.8,
      cooldown = 24.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case DRAGON_GAS
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 1.8,
      cooldown = 29,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 5,
    )

  case TAIL_SLAP
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.8,
      cooldown = 14.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 4,
    )

  case HORN_DANCE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 3.4,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.RANGE,
      hits = 1,
    )

  case WILD_WAVE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 6.6,
      cooldown = 29,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 5,
    )

  case LIFE_PLANT
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.FIELD,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 13.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 1,
    )

  case DOUBLE_SLAP
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.8,
      cooldown = 14.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 4,
    )

  case DRAINING_VACUUM
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 5.1,
      cooldown = 24.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case DINO_STORM
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 3.4,
      cooldown = 24.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 4,
    )

  case SPINNING_BOLT
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.55,
      cooldown = 19.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
    )

  case AQUABALL
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 5.1,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case CAVALRY
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.8,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 1,
    )

  case TROUT_HOP
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.4,
      cooldown = 14.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 4,
    )

  case DESPERATE_CHARGE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.BUFF,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 29,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 1,
    )

  case DUAL_ATTACK
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4.4,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 4,
    )

  case PSYCHOWAVE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 3.4,
      cooldown = 24.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      reaction = Some(ArtReaction.BURST),
      hits = 1,
    )

  case NECK_SLICE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4.4,
      cooldown = 19.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.AGGRO,
      hits = 1,
    )

  case SPIT_BEAM
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 5.1,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case DINO_FLASH
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.4,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case RADIANT_ARTS
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4.25,
      cooldown = 24.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
    )

  case RHINO_STORM
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.4,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      reaction = Some(ArtReaction.BLOWDOWN),
      hits = 2,
    )

  case DOUBLE_BITE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.25,
      cooldown = 14.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
    )

  case PREDATION
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.8,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case DOUBLE_STRIKE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.55,
      cooldown = 14.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 2,
    )

  case DIG
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 2.25,
      cooldown = 14.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case FIN_OF_FEAR
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 3.4,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case SPIDER_WEB
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.8,
      cooldown = 19.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case TOXIC_GAS
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 3.4,
      cooldown = 19.3,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.RANGE,
      hits = 1,
    )

  case MURDER_KNUCKLE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4.4,
      cooldown = 22.7,
      cooldownType = ArtRechargeType.TIME,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case HEALING_REST
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.HEALING,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 2.45,
      cooldown = 4,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.HEALING,
    )

  case SUMO_PRESS
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 2.35,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      reaction = Some(ArtReaction.SMASH),
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case SHELL_GUARD
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 9,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case BUBBLE_CLOUD
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 3.1,
      cooldown = 4,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case BURROWING_ROCKET
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case ELECTRIC_SKIN
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      reaction = Some(ArtReaction.DAZE),
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case TRICK_DART
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 2.5,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case BUTTERFLY_DUST
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.BUFF,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case POISON_SPRAY
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 2.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 1,
    )

  case HAWK_SHOT
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      reaction = Some(ArtReaction.BREAK),
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case WATER_JET
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 2.9,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      reaction = Some(ArtReaction.KNOCKBACK),
      hits = 1,
    )

  case CURSED_CUISINE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      reaction = Some(ArtReaction.BLOWDOWN),
      hits = 1,
    )

  case HYPNO_LIGHT
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.DEBUFF,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 3,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case HYPNOTISM
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.DEBUFF,
      target = ArtTarget.SINGLE,
      cooldown = 7,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case BATTLE_PHEROMONE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.BUFF,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 7,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case LAST_FENCER
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case POINTY_STICK
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case AQUATIC_MISSILE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      reaction = Some(ArtReaction.TOPPLE),
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case PIERCING_LASER
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 4,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case BOMBERHEAD
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      reaction = Some(ArtReaction.LAUNCH),
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case JET_STREAM
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 4.0,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
      reaction = Some(ArtReaction.BLOWDOWN),
    )

  case DRAGON_DECREE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.CLEANSE,
      target = ArtTarget.CIRCLE_AOE,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.RANGE,
    )

  case SHARK_SHOCK
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.CIRCLE_AOE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case ETHER_SPHERE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 2.5,
      cooldown = 4,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case VAMPIRE_BAT
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 1.65,
      cooldown = 4,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case TRANSIENT_BOND
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 5.0,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 16,
    )

  case GATLING_PECK
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 4,
    )

  case MINISTORM
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 3.1,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case HYDRA_GAS
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 1.65,
      cooldown = 5,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
      hits = 1,
    )

  case KINGS_BASH
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 3.9,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case ELIMINATION_BEAM
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.ETHER,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 6.0,
      cooldown = 8,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DAMAGE,
      hits = 1,
    )

  case EVASIVE_POSTURE
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.STANCE,
      target = ArtTarget.SELF,
      cooldown = 9,
      cooldownType = ArtRechargeType.AUTO_ATTACK,
      fusionBonus = ArtFusionBonus.DURATION,
    )

  case FINAL_COUNTDOWN
    extends ArtType(
      cls = Some(ClassType.SOULHACKER_POWER),
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.SINGLE,
      powerMultiplier = 5,
      cooldown = 3,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      hits = 7,
      isMaster = true,
    )

  // ================================== //
  // EXCLUSIVE NOAH'S ARTS
  // ================================== //
  case INFINITY_BLADE
    extends ArtType(
      category = ArtCategory.PHYSICAL,
      target = ArtTarget.FRONT_AOE,
      powerMultiplier = 10,
      cooldown = 4,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      hits = 2,
    )

  case UNLIMITED_SWORD
    extends ArtType(
      category = ArtCategory.BUFF,
      target = ArtTarget.SELF,
      cooldown = 10,
      cooldownType = ArtRechargeType.ROLE_ACTION,
    )

  // ================================== //
  // EXCLUSIVE MIO'S ARTS
  // ================================== //
  case DOMINION_FLOWER
    extends ArtType(
      category = ArtCategory.ETHER,
      target = ArtTarget.SINGLE,
      powerMultiplier = 7.5,
      cooldown = 4,
      cooldownType = ArtRechargeType.ROLE_ACTION,
      reaction = Some(ArtReaction.BURST), // Burst on every hit.
      hits = 5,
    )

end ArtType
