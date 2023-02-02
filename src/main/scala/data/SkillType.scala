package dev.hawu.plugins.xenocraft
package data

import org.bukkit.Material
import dev.hawu.plugins.api.i18n.Locale
import dev.hawu.plugins.xenocraft.gui.SkillsGUI

/** Represents a skill type.
  */
enum SkillType(val cls: Option[ClassType] = None, val material: Material, val isMaster: Boolean = false):

  /** Checks if this skill belongs to an agnian class.
    *
    * @return
    *   whether it is agnian
    */
  def isAgnian: Boolean = cls.exists(_.nation == ClassNation.AGNUS)

  /** Checks if this skill belongs to a kevesi class.
    *
    * @return
    *   whether it is kevesi
    */
  def isKevesi: Boolean = cls.exists(_.nation == ClassNation.KEVES)

  /** Checks if this skill belongs to a soulhacker class.
    *
    * @return
    *   whether it is soulhacker
    */
  def isSoulhacker: Boolean = cls.exists(_.isSoulhacker)

  /** Retrieves the properly formatted name of the skill type.
    *
    * @return
    *   the name
    */
  def name(locale: Locale) = SkillsGUI.getModule.translate(locale, toString.toLowerCase().replace('_', '-'))

  /** Retrieves the properly formatted description of the skill type.
    *
    * @return
    *   the description
    */
  def description(locale: Locale) = SkillsGUI.getModule
    .translate(locale, s"${toString.toLowerCase().replace('_', '-')}-desc")

  case SHARP_EYE extends SkillType(Some(ClassType.SWORDFIGHTER), Material.SPECTRAL_ARROW, false)
  case COVERT_ATTACK extends SkillType(Some(ClassType.SWORDFIGHTER), Material.ENDER_EYE, true)
  case CYPHER_EDGE extends SkillType(Some(ClassType.SWORDFIGHTER), Material.NETHER_STAR, true)
  case INSPIRIT extends SkillType(Some(ClassType.SWORDFIGHTER), Material.END_CRYSTAL, false)

  case STORMY_GALE extends SkillType(Some(ClassType.ZEPHYR), Material.RABBIT_FOOT, true)
  case ILL_DEFEND_YOU extends SkillType(Some(ClassType.ZEPHYR), Material.SHIELD, false)
  case SPLIT_SECOND_COUNTER extends SkillType(Some(ClassType.ZEPHYR), Material.IRON_TRAPDOOR, true)
  case ETHERS_SANCTUARY extends SkillType(Some(ClassType.ZEPHYR), Material.HONEY_BOTTLE, false)

  case MEDICAL_MIND extends SkillType(Some(ClassType.MEDIC_GUNNER), Material.IRON_SWORD, false)
  case SPEEDY_RECOVERY extends SkillType(Some(ClassType.MEDIC_GUNNER), Material.GOLDEN_APPLE, false)
  case NINJA_HEALER extends SkillType(Some(ClassType.MEDIC_GUNNER), Material.ENDER_EYE, true)
  case ETERNAL_FORMATION extends SkillType(Some(ClassType.MEDIC_GUNNER), Material.CLOCK, true)

  case ETHERAL_ABILITY extends SkillType(Some(ClassType.TACTICIAN), Material.TRIDENT, true)
  case SHIELDRENDER extends SkillType(Some(ClassType.TACTICIAN), Material.IRON_AXE, false)
  case ETERNAL_CURSE extends SkillType(Some(ClassType.TACTICIAN), Material.CLOCK, false)
  case INEVITABLE_EVITABILITY extends SkillType(Some(ClassType.TACTICIAN), Material.RABBIT_FOOT, true)

  case DEFENSIVE_SOUL extends SkillType(Some(ClassType.HEAVY_GUARD), Material.IRON_CHESTPLATE, true)
  case ILL_PROTECT_YOU extends SkillType(Some(ClassType.HEAVY_GUARD), Material.SHIELD, false)
  case AGGRAVATOR extends SkillType(Some(ClassType.HEAVY_GUARD), Material.ENDER_EYE, false)
  case TENACIOUS_BLOCKER extends SkillType(Some(ClassType.HEAVY_GUARD), Material.SHIELD, true)

  case INSULT_TO_INJURY extends SkillType(Some(ClassType.OGRE), Material.SPECTRAL_ARROW, false)
  case FURY_SMASH extends SkillType(Some(ClassType.OGRE), Material.STONE_BUTTON, false)
  case FIGHTING_PROWESS extends SkillType(Some(ClassType.OGRE), Material.DIAMOND_SWORD, true)
  case DYNAMITE_SPIRIT extends SkillType(Some(ClassType.OGRE), Material.SPECTRAL_ARROW, true)

  case FIRST_MOVE_WINS extends SkillType(Some(ClassType.FLASH_FENCER), Material.IRON_SWORD, false)
  case FIGHTING_INSTINCT extends SkillType(Some(ClassType.FLASH_FENCER), Material.DIAMOND, false)
  case CRITICAL_STRIKE extends SkillType(Some(ClassType.FLASH_FENCER), Material.NETHER_STAR, true)
  case CAPABLE_HANDS extends SkillType(Some(ClassType.FLASH_FENCER), Material.DIAMOND, true)

  case RECOVERY_ENGINE extends SkillType(Some(ClassType.WAR_MEDIC), Material.APPLE, false)
  case RESCUE_LICENSE extends SkillType(Some(ClassType.WAR_MEDIC), Material.LEAD, false)
  case HEALING_LICENSE extends SkillType(Some(ClassType.WAR_MEDIC), Material.GOLDEN_CARROT, true)
  case ANTIBODY_SYSTEM extends SkillType(Some(ClassType.WAR_MEDIC), Material.WATER_BUCKET, true)

  case ILL_HELP extends SkillType(Some(ClassType.GUARDIAN_COMMANDER), Material.ENDER_EYE, false)
  case SOLDIERS_POTENTIAL extends SkillType(Some(ClassType.GUARDIAN_COMMANDER), Material.GOLDEN_CARROT, false)
  case PROTECTORS_PRIDE extends SkillType(Some(ClassType.GUARDIAN_COMMANDER), Material.COMPASS, true)
  case MIND_FOR_GUARDING extends SkillType(Some(ClassType.GUARDIAN_COMMANDER), Material.IRON_CHESTPLATE, true)

  case WHEEL_OF_REBIRTH extends SkillType(Some(ClassType.THAUMATURGE), Material.GOLDEN_APPLE, false)
  case QINGGONG extends SkillType(Some(ClassType.THAUMATURGE), Material.RABBIT_FOOT, false)
  case ULTIMATE_QIGONG extends SkillType(Some(ClassType.THAUMATURGE), Material.IRON_AXE, true)
  case LIFE_SAVING_FISTS extends SkillType(Some(ClassType.THAUMATURGE), Material.WOODEN_SWORD, true)

  case BANGSPLOSION extends SkillType(Some(ClassType.YUMSMITH), Material.MUSIC_DISC_CAT, false)
  case SAPPY_SAPPY_DRAIN extends SkillType(Some(ClassType.YUMSMITH), Material.LAVA_BUCKET, true)
  case SLICEY_DICEY extends SkillType(Some(ClassType.YUMSMITH), Material.MUSIC_DISC_CAT, true)
  case PREPPY_PREPPY_LINK extends SkillType(Some(ClassType.YUMSMITH), Material.SPLASH_POTION, false)

  case SIXTH_SENSE extends SkillType(Some(ClassType.FULL_METAL_JAGUAR), Material.GOLDEN_SWORD, false)
  case POSITIONING_PRO extends SkillType(Some(ClassType.FULL_METAL_JAGUAR), Material.EXPERIENCE_BOTTLE, true)
  case SHOCK_SHOT extends SkillType(Some(ClassType.FULL_METAL_JAGUAR), Material.GOLDEN_SWORD, true)
  case BULLET_SYMPHONY extends SkillType(Some(ClassType.FULL_METAL_JAGUAR), Material.IRON_HOE, false)

  case PROWESS_GAMBIT extends SkillType(Some(ClassType.STRATEGOS), Material.SPECTRAL_ARROW, false)
  case SAPPING_GAMBIT extends SkillType(Some(ClassType.STRATEGOS), Material.CLOCK, false)
  case STRENGTHENING_GAMBIT extends SkillType(Some(ClassType.STRATEGOS), Material.CLOCK, true)
  case PREEMPTION_GAMBIT extends SkillType(Some(ClassType.STRATEGOS), Material.FIREWORK_ROCKET, true)

  case SWIFT_DEATH extends SkillType(Some(ClassType.STALKER), Material.SPECTRAL_ARROW, true)
  case IN_COLD_BLOOD extends SkillType(Some(ClassType.STALKER), Material.WITHER_ROSE, false)
  case EVADE_AND_ATTACK extends SkillType(Some(ClassType.STALKER), Material.RABBIT_FOOT, true)
  case SERENE_HEART extends SkillType(Some(ClassType.STALKER), Material.ENDER_EYE, false)

  case GOADING_ATTACK extends SkillType(Some(ClassType.LONE_EXILE), Material.ENDER_EYE, false)
  case MANIAC extends SkillType(Some(ClassType.LONE_EXILE), Material.SPECTRAL_ARROW, true)
  case RETURNING_THE_FAVOR extends SkillType(Some(ClassType.LONE_EXILE), Material.GLASS, false)
  case NATURAL_SELECTION extends SkillType(Some(ClassType.LONE_EXILE), Material.BLUE_BANNER, true)

  case ADROIT_BLADE extends SkillType(Some(ClassType.INCURSOR), Material.SPECTRAL_ARROW, false)
  case FLOATING_KILL extends SkillType(Some(ClassType.INCURSOR), Material.NETHER_STAR, false)
  case VIGILANCE extends SkillType(Some(ClassType.INCURSOR), Material.IRON_SWORD, true)
  case EYE_FOR_WEAK_POINTS extends SkillType(Some(ClassType.INCURSOR), Material.NETHER_STAR, true)

  case COME_AND_GET_ME extends SkillType(Some(ClassType.LOST_VANGUARD), Material.ENDER_EYE, true)
  case NOW_THATS_TACTICS extends SkillType(Some(ClassType.LOST_VANGUARD), Material.EXPERIENCE_BOTTLE, true)
  case NEVER_AGAIN extends SkillType(Some(ClassType.LOST_VANGUARD), Material.SHIELD, false)
  case STAY_RIGHT_THERE extends SkillType(Some(ClassType.LOST_VANGUARD), Material.GLASS, false)

  case CHEER_UP_EVERYONE extends SkillType(Some(ClassType.SIGNIFIER), Material.ENCHANTED_GOLDEN_APPLE, false)
  case IVE_GOT_YOUR_BACKS extends SkillType(Some(ClassType.SIGNIFIER), Material.CLOCK, false)
  case ALL_ABOUT_SUPPORT extends SkillType(Some(ClassType.SIGNIFIER), Material.EXPERIENCE_BOTTLE, true)
  case WE_CAN_DO_BETTER extends SkillType(Some(ClassType.SIGNIFIER), Material.GOLDEN_APPLE, true)

  case SOUL_HACK extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.EXPERIENCE_BOTTLE, true)

  case STORM_AND_STRESS extends SkillType(Some(ClassType.MARTIAL_ARTIST), Material.END_CRYSTAL, false)
  case FLASHING_PUNCH extends SkillType(Some(ClassType.MARTIAL_ARTIST), Material.IRON_SWORD, false)
  case FELINE_RIGHTING_REFLEX extends SkillType(Some(ClassType.MARTIAL_ARTIST), Material.RABBIT_HIDE, true)
  case FRENZIED_COMBO extends SkillType(Some(ClassType.MARTIAL_ARTIST), Material.COMPASS, true)

  case ENDSONG extends SkillType(Some(ClassType.TROUBADOUR), Material.COMPASS, false)
  case DANCE_OF_BARRAGES extends SkillType(Some(ClassType.TROUBADOUR), Material.CLOCK, true)
  case DANCE_OF_LIFE extends SkillType(Some(ClassType.TROUBADOUR), Material.COMPASS, false)
  case SWIFTSONG extends SkillType(Some(ClassType.TROUBADOUR), Material.RABBIT_FOOT, true)

  case BEAST_MODE extends SkillType(Some(ClassType.SERAPH), Material.EXPERIENCE_BOTTLE, false)
  case GOT_THE_GUTS extends SkillType(Some(ClassType.SERAPH), Material.END_CRYSTAL, false)
  case GREATEST_WARRIOR extends SkillType(Some(ClassType.SERAPH), Material.SPIDER_SPAWN_EGG, true)
  case UNIVERSAL_ANNIHILATION extends SkillType(Some(ClassType.SERAPH), Material.IRON_AXE, true)

  case CHAOS_CHAIN extends SkillType(Some(ClassType.MACHINE_ASSASSIN), Material.IRON_SWORD, false)
  case PHANTOM_STRIKE extends SkillType(Some(ClassType.MACHINE_ASSASSIN), Material.IRON_AXE, false)
  case CURSED_EDGE extends SkillType(Some(ClassType.MACHINE_ASSASSIN), Material.IRON_SWORD, true)
  case ANTI_EROSION_SYSTEM extends SkillType(Some(ClassType.MACHINE_ASSASSIN), Material.LAVA_BUCKET, true)

  case STEADFAST_FEELINGS extends SkillType(Some(ClassType.LIFESAGE), Material.GOLDEN_APPLE, false)
  case INSTANT_REGEN extends SkillType(Some(ClassType.LIFESAGE), Material.SPLASH_POTION, false)
  case ENHANCED_CELLULAR_STIMULUS extends SkillType(Some(ClassType.LIFESAGE), Material.SWEET_BERRY_BUSH, true)
  case ABUNDANT_OCEANS extends SkillType(Some(ClassType.LIFESAGE), Material.WATER_BUCKET, true)

  case ELEMENTAL_BOOST extends SkillType(Some(ClassType.ROYAL_SUMMONER), Material.FLINT_AND_STEEL, false)
  case ESSENCE_OF_ETHER extends SkillType(Some(ClassType.ROYAL_SUMMONER), Material.BREWING_STAND, true)
  case FORTIFIED_ETHER_GUARD extends SkillType(Some(ClassType.ROYAL_SUMMONER), Material.BEEHIVE, true)
  case HERE_I_SHALL_STAND extends SkillType(Some(ClassType.ROYAL_SUMMONER), Material.FLINT_AND_STEEL, false)

  case TIT_FOR_TAT extends SkillType(Some(ClassType.NOPONIC_CHAMPION), Material.COMPASS, false)
  case NANOMACHINE_REPAIR extends SkillType(Some(ClassType.NOPONIC_CHAMPION), Material.APPLE, true)
  case VELOCITY_GEAR extends SkillType(Some(ClassType.NOPONIC_CHAMPION), Material.RABBIT_FOOT, false)
  case OVERCLOCK extends SkillType(Some(ClassType.NOPONIC_CHAMPION), Material.COMPASS, true)

end SkillType
