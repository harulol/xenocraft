package dev.hawu.plugins.xenocraft
package data

import gui.SkillsGUI

import dev.hawu.plugins.api.i18n.Locale
import org.bukkit.Material

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
   * the description
   */
  def description(locale: Locale) = SkillsGUI.getModule.translate(locale, s"${toString.toLowerCase().replace('_', '-')}-desc")

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
  case EVASION_EXPERTISE extends SkillType(Some(ClassType.STALKER), Material.RABBIT_FOOT, true)
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
  case ENHANCED_CELLULAR_STIMULUS extends SkillType(Some(ClassType.LIFESAGE), Material.SWEET_BERRIES, true)
  case ABUNDANT_OCEANS extends SkillType(Some(ClassType.LIFESAGE), Material.WATER_BUCKET, true)

  case ELEMENTAL_BOOST extends SkillType(Some(ClassType.ROYAL_SUMMONER), Material.FLINT_AND_STEEL, false)
  case ESSENCE_OF_ETHER extends SkillType(Some(ClassType.ROYAL_SUMMONER), Material.BREWING_STAND, true)
  case FORTIFIED_ETHER_GUARD extends SkillType(Some(ClassType.ROYAL_SUMMONER), Material.BEEHIVE, true)
  case HERE_I_SHALL_STAND extends SkillType(Some(ClassType.ROYAL_SUMMONER), Material.FLINT_AND_STEEL, false)

  case TIT_FOR_TAT extends SkillType(Some(ClassType.NOPONIC_CHAMPION), Material.COMPASS, false)
  case NANOMACHINE_REPAIR extends SkillType(Some(ClassType.NOPONIC_CHAMPION), Material.APPLE, true)
  case VELOCITY_GEAR extends SkillType(Some(ClassType.NOPONIC_CHAMPION), Material.RABBIT_FOOT, false)
  case OVERCLOCK extends SkillType(Some(ClassType.NOPONIC_CHAMPION), Material.COMPASS, true)

  case MAGIC_TRANSMUTATION extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_SWORD, false)
  case BERSERKER_MINDSET extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_SWORD, false)
  case EXOSHELL extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.BONE_BLOCK, false)
  case DUAL_HORN_PRESSURE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.ENDER_EYE, false)
  case WOLF_SPIRIT extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.WOLF_SPAWN_EGG, false)
  case MANO_A_MANO_EVASION extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.RABBIT_FOOT, false)
  case RECHARGE_CHEER extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.CLOCK, false)
  case DEFLECTOR_PLATE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_CHESTPLATE, false)
  case SUPERCHARGED extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_SWORD, false)
  case ENERGY_EFFICIENCY extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.STICK, false)
  case ACTION_REACTION extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.COMPASS, false)
  case NON_STOP_BARRAGE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.TRIPWIRE_HOOK, false)
  case SURPRISE_ATTACK extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_SWORD, false)
  case EXCESSIVE_ARMOR extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.TRIPWIRE_HOOK, false)
  case BREATH_OF_AN_ERA extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.COMPASS, false)
  case AQUATIC_WARRIOR extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.BUBBLE_CORAL, false)
  case RECKLESS_ATTACK extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_SWORD, false)
  case ROYAL_CHARISMA extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.CLOCK, false)
  case QUEEN_OF_MERCY extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.GOLDEN_APPLE, false)
  case CREEPING_PREDATOR extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_SWORD, false)
  case HEALING_MASTERY extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.GOLDEN_APPLE, false)
  case DEFENSE_MASTERY extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.SHIELD, false)
  case ACCELERATING_ATTACKS extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_SWORD, false)
  case JUST_A_MOMENT extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.GOLDEN_APPLE, false)
  case BLASTING_STOMP extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.TRIDENT, false)
  case QUEENLY_PRIDE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_SWORD, false)
  case COUNTER_PUNCHER extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.RABBIT_FOOT, false)
  case MIMIC_TECHNIQUE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.ENDER_EYE, false)
  case ULTRANULL_SHIELD extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.SHIELD, false)
  case EMERGENCY_TURBO extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.CLOCK, false)
  case ROYAL_AIDE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.LEAD, false)
  case SANDSMOKE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.SAND, false)
  case POWER_PICK_ME_UP extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.EXPERIENCE_BOTTLE, false)
  case EAGLE_RUSH extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_AXE, false)
  case SENSE_OF_DANGER extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.NETHER_STAR, false)
  case MONARCHS_HEART extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.COOKED_BEEF, false)
  case SACRIFICIAL_HEAL extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.GOLDEN_APPLE, false)
  case TACTICAL_EYE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_SWORD, false)
  case STRIKE_STRIKER extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.RABBIT_FOOT, false)
  case GRACE_OF_THE_LAND extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.GRASS, false)
  case AIR_BODY extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.LEATHER_CHESTPLATE, false)
  case HEAVY_ARMOR extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.NETHERITE_CHESTPLATE, false)
  case HOT_SOUL extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.TORCH, false)
  case APOTHECARYS_WISDOM extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.STICK, false)
  case STRENGTH_SUPPORT extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.POTION, false)
  case NIMBLENESS_ITSELF extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.RABBIT_FOOT, false)
  case SUPER_EXPLOSION extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.TNT, false)
  case EMPEROR_OF_VIOLENCE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.DIAMOND_SWORD, false)
  case CELESTIAL_CONQUEROR extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_SWORD, false)
  case SYSTEMATIC_DEFENSE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.COMPASS, false)
  case MEGASHOUT extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.SEA_LANTERN, false)
  case UNDERWORLD_RAGE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.NETHERRACK, false)
  case AMAZING_GROWTH extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.BONE_MEAL, false)
  case DANGEROUS_CLAWS extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.NETHER_STAR, false)
  case FIERY_MORALE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.GOLDEN_APPLE, false)
  case NEEDLE_SHELL extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.ENDER_EYE, false)
  case ATTACK_HEAL extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_AXE, false)
  case BATTLE_FEVER extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.FIRE_CHARGE, false)
  case WILD_TENDENCIES extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.BONE, false)
  case ATTACK_MASTERY extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_SWORD, false)
  case DEFENSE_TIME extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.SHIELD, false)
  case INSTANT_CHARGE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.CLOCK, false)
  case DRAIN_GUARD extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.CLOCK, false)
  case REVERSE_LIFE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.APPLE, false)
  case REVENGE_IMPETUS extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.ANVIL, false)
  case AWAKILLING extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.END_CRYSTAL, false)
  case SCATTERED_HEALING extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.GOLDEN_APPLE, false)
  case DAMAGE_SHARE extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.CHAIN, false)
  case LARGE_SCALE_SHOCK extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.MUSIC_DISC_BLOCKS, false)
  case TERRAN_WARRIOR extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.IRON_SWORD, false)
  case ONE_WITH_THE_LAND extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.DIRT, false)
  case DEBUFF_COUNTER extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.CLOCK, false)
  case NATURAL_GUARD extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.SHIELD, false)
  case ACCELERATOR_ORGAN extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.CROSSBOW, false)
  case PHYSICAL_ABSORBER extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.SHIELD, false)
  case SPIRITUAL_ABSORBER extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.SHIELD, false)
  case DEEPLY_TACTICAL extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.EXPERIENCE_BOTTLE, false)
  case POSITIONAL_ATTACKER extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.EXPERIENCE_BOTTLE, false)
  case DODGE_ACCELERATION extends SkillType(Some(ClassType.SOULHACKER_POWER), Material.RABBIT_FOOT, false)

  // Lucky Seven's special skills. These are not displayed anywhere.

  case _CUT_IN_HALF extends SkillType(Some(ClassType.ATTACKER_LUCKY_SEVEN), Material.NETHER_STAR, false)
  case _REALM_OF_THE_MASTER extends SkillType(Some(ClassType.ATTACKER_LUCKY_SEVEN), Material.NETHER_STAR, false)
  case _OPENING_STAND extends SkillType(Some(ClassType.ATTACKER_LUCKY_SEVEN), Material.NETHER_STAR, false)
  case _SILENT_SWORD extends SkillType(Some(ClassType.ATTACKER_LUCKY_SEVEN), Material.NETHER_STAR, false)

  case _SWORD_OF_PROTECTION extends SkillType(Some(ClassType.HEALER_LUCKY_SEVEN), Material.NETHER_STAR, false)
  case _UNWAVERING_WILL extends SkillType(Some(ClassType.HEALER_LUCKY_SEVEN), Material.NETHER_STAR, false)
  case _ENCOURAGEMENT extends SkillType(Some(ClassType.HEALER_LUCKY_SEVEN), Material.NETHER_STAR, false)
  case _EXTINGUISHING_BLOODLUST extends SkillType(Some(ClassType.HEALER_LUCKY_SEVEN), Material.NETHER_STAR, false)

  case _NEVER_AGAIN extends SkillType(Some(ClassType.DEFENDER_LUCKY_SEVEN), Material.NETHER_STAR, false)
  case _FLASH_OF_AVOIDANCE extends SkillType(Some(ClassType.DEFENDER_LUCKY_SEVEN), Material.NETHER_STAR, false)
  case __UNWAVERING_WILL extends SkillType(Some(ClassType.DEFENDER_LUCKY_SEVEN), Material.NETHER_STAR, false)
  case _I_WILL_TAKE_OVER extends SkillType(Some(ClassType.DEFENDER_LUCKY_SEVEN), Material.NETHER_STAR, false)

end SkillType
