package dev.hawu.plugins.xenocraft
package data

import dev.hawu.plugins.api.collections.tuples.Pair
import dev.hawu.plugins.api.i18n.{LanguageModule, Locale}
import dev.hawu.plugins.xenocraft.data.ClassType.*
import dev.hawu.plugins.xenocraft.gui.ClassesGUI
import org.bukkit.plugin.java.JavaPlugin

import scala.collection.mutable

/**
 * Represents a type of class a player can pick.
 */
enum ClassType(
  val classRole: ClassRole,
  val classHp: Double,
  val classAttack: Double,
  val classHealingPower: Double,
  val classDexterity: Double,
  val classAgility: Double,
  val classPhysDef: Double,
  val classEtherDef: Double,
  val weaponType: WeaponType,
  val upgradedWeaponType: Option[WeaponType] = None,
  name: Option[String] = None
):

  val arts: mutable.ArrayBuffer[ArtType] = mutable.ArrayBuffer.empty[ArtType]
  private val keyName = this.name.getOrElse(this.toString.replace('_', '-')).toLowerCase

  /**
   * Retrieves the name of the hero who wields this class.
   *
   * @param locale the locale
   * @return the wielder's name
   */
  def wielderName(locale: Locale): String =
    ClassesGUI.getModule.translate(locale, s"$keyName-wielder")

  /**
   * Retrieves the proper title of the hero who wields this class.
   *
   * @param locale the locale
   * @return the wielder's title
   */
  def wielderTitle(locale: Locale): String =
    ClassesGUI.getModule.translate(locale, s"$keyName-title")

  /**
   * Retrieves the properly displayed name of the class type.
   *
   * @param locale the locale
   * @return the name
   */
  def displayName(locale: Locale): String =
    ClassesGUI.getModule.translate(locale, s"$keyName-name")

  /**
   * Checks if this class type should be displayed in class selecting options.
   *
   * @return whether it should be displayed
   */
  def shouldDisplay: Boolean = this match
    case ATTACKER_LUCKY_SEVEN | DEFENDER_LUCKY_SEVEN | HEALER_LUCKY_SEVEN => false
    case cls: ClassType if cls.isSoulhacker && cls != SOULHACKER_STRIKE => false
    case _ => true

  /**
   * Retrieves the full name for the Soulhacker class with the
   * soul involved.
   *
   * @param locale the locale
   * @return the full name
   */
  def soulhackerName(locale: Locale): String = if isSoulhacker then
    ClassesGUI.getModule.translate(locale, "soulhacker-soul", Pair.of("soul", soulName(locale)))
  else throw new IllegalAccessException("This property only works with SOULHACKER classes.")

  /**
   * Retrieves the soul name of the Soulhacker class.
   *
   * @param locale the locale
   * @return the soul name
   */
  def soulName(locale: Locale): String = if isSoulhacker then
    val soul = this.toString.split("_")(1).toLowerCase
    ClassesGUI.getModule.translate(locale, s"$soul-soul")
  else throw new IllegalAccessException("This property only works with SOULHACKER classes.")

  /**
   * Checks if this class is one of the soul hacker classes.
   *
   * @return whether this is a soulhacker class
   */
  def isSoulhacker: Boolean = this.toString.startsWith("SOULHACKER")

  case SWORDFIGHTER extends ClassType(
    classRole = ClassRole.ATTACKER,
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
  case ZEPHYR extends ClassType(
    classRole = ClassRole.DEFENDER,
    classHp = 1.1,
    classAttack = 0.75,
    classHealingPower = 0.25,
    classDexterity = 1.05,
    classAgility = 1.3,
    classPhysDef = 0.35,
    classEtherDef = 0.5,
    weaponType = WeaponType.DUAL_MOONBLADES,
    upgradedWeaponType = Some(WeaponType.SUNDANCERS),
  )
  case MEDIC_GUNNER extends ClassType(
    classRole = ClassRole.HEALER,
    classHp = 0.6,
    classAttack = 0.6,
    classHealingPower = 1.2,
    classDexterity = 1.05,
    classAgility = 0.75,
    classPhysDef = 0.15,
    classEtherDef = 0.15,
    weaponType = WeaponType.GUNROD,
    upgradedWeaponType = Some(WeaponType.CADUCEUS_GUNROD),
  )
  case TACTICIAN extends ClassType(
    classRole = ClassRole.HEALER,
    classHp = 0.65,
    classAttack = 0.65,
    classHealingPower = 1.15,
    classDexterity = 1.1,
    classAgility = 0.8,
    classPhysDef = 0.1,
    classEtherDef = 0.3,
    weaponType = WeaponType.SPELL_TAGS_MONDO,
    upgradedWeaponType = Some(WeaponType.SPELL_TAGS_TOTALITY),
  )
  case HEAVY_GUARD extends ClassType(
    classRole = ClassRole.DEFENDER,
    classHp = 1.3,
    classAttack = 0.8,
    classHealingPower = 0.25,
    classDexterity = 1,
    classAgility = 0.75,
    classPhysDef = 0.5,
    classEtherDef = 0.35,
    weaponType = WeaponType.SHIELDBLADE,
    upgradedWeaponType = Some(WeaponType.ADAMANT_SHELL),
  )
  case OGRE extends ClassType(
    classRole = ClassRole.ATTACKER,
    classHp = 0.8,
    classAttack = 1.2,
    classHealingPower = 0.25,
    classDexterity = 1,
    classAgility = 0.85,
    classPhysDef = 0.2,
    classEtherDef = 0.1,
    weaponType = WeaponType.COMET_MALLET,
    upgradedWeaponType = Some(WeaponType.METEOR_SMASHER),
  )

  case FLASH_FENCER extends ClassType(
    classRole = ClassRole.ATTACKER,
    classHp = 0.75,
    classAttack = 1.1,
    classHealingPower = 0.25,
    classDexterity = 1.2,
    classAgility = 1.1,
    classPhysDef = 0.15,
    classEtherDef = 0.15,
    weaponType = WeaponType.DUAL_RAPIERS,
  )
  case WAR_MEDIC extends ClassType(
    classRole = ClassRole.HEALER,
    classHp = 0.6,
    classAttack = 0.55,
    classHealingPower = 1.25,
    classDexterity = 1.2,
    classAgility = 0.75,
    classPhysDef = 0.15,
    classEtherDef = 0.15,
    weaponType = WeaponType.RELIEF_RIFLE,
  )
  case GUARDIAN_COMMANDER extends ClassType(
    classRole = ClassRole.DEFENDER,
    classHp = 1.2,
    classAttack = 0.75,
    classHealingPower = 0.35,
    classDexterity = 1.05,
    classAgility = 0.75,
    classPhysDef = 0.5,
    classEtherDef = 0.35,
    weaponType = WeaponType.GUTSY_GLADIUS,
  )
  case THAUMATURGE extends ClassType(
    classRole = ClassRole.HEALER,
    classHp = 0.75,
    classAttack = 0.75,
    classHealingPower = 1.1,
    classDexterity = 1.2,
    classAgility = 0.75,
    classPhysDef = 0.1,
    classEtherDef = 0.3,
    weaponType = WeaponType.MARTIAL_LINKSTAFF,
  )
  case YUMSMITH extends ClassType(
    classRole = ClassRole.ATTACKER,
    classHp = 0.8,
    classAttack = 1.1,
    classHealingPower = 0.35,
    classDexterity = 1.3,
    classAgility = 0.75,
    classPhysDef = 0.15,
    classEtherDef = 0.15,
    weaponType = WeaponType.VARIABLE_ARMS,
  )
  case FULL_METAL_JAGUAR extends ClassType(
    classRole = ClassRole.ATTACKER,
    classHp = 0.7,
    classAttack = 1.1,
    classHealingPower = 0.45,
    classDexterity = 1.3,
    classAgility = 1.1,
    classPhysDef = 0.15,
    classEtherDef = 0.15,
    weaponType = WeaponType.GUN_DRONES,
  )
  case STRATEGOS extends ClassType(
    classRole = ClassRole.HEALER,
    classHp = 0.65,
    classAttack = 0.65,
    classHealingPower = 1.15,
    classDexterity = 1.1,
    classAgility = 0.85,
    classPhysDef = 0.1,
    classEtherDef = 0.3,
    weaponType = WeaponType.DISC_LAUNCHER,
  )
  case STALKER extends ClassType(
    classRole = ClassRole.ATTACKER,
    classHp = 0.7,
    classAttack = 1.1,
    classHealingPower = 0.25,
    classDexterity = 1.3,
    classAgility = 1.3,
    classPhysDef = 0.15,
    classEtherDef = 0.15,
    weaponType = WeaponType.LUMINESCENT_BOW,
  )
  case LONE_EXILE extends ClassType(
    classRole = ClassRole.DEFENDER,
    classHp = 1.1,
    classAttack = 0.95,
    classHealingPower = 0.15,
    classDexterity = 1.1,
    classAgility = 1.25,
    classPhysDef = 0.35,
    classEtherDef = 0.5,
    weaponType = WeaponType.TWINSABER,
  )
  case INCURSOR extends ClassType(
    classRole = ClassRole.ATTACKER,
    classHp = 0.75,
    classAttack = 1.2,
    classHealingPower = 0.2,
    classDexterity = 1.05,
    classAgility = 1.0,
    classPhysDef = 0.2,
    classEtherDef = 0.1,
    weaponType = WeaponType.EXPONENTIAL_EDGE,
  )
  case LOST_VANGUARD extends ClassType(
    classRole = ClassRole.DEFENDER,
    classHp = 1.25,
    classAttack = 0.8,
    classHealingPower = 0.35,
    classDexterity = 1.2,
    classAgility = 0.9,
    classPhysDef = 0.5,
    classEtherDef = 0.35,
    weaponType = WeaponType.DEFENSE_MACE,
  )
  case SIGNIFIER extends ClassType(
    classRole = ClassRole.HEALER,
    classHp = 0.6,
    classAttack = 0.85,
    classHealingPower = 0.75,
    classDexterity = 1.05,
    classAgility = 0.9,
    classPhysDef = 0.1,
    classEtherDef = 0.3,
    weaponType = WeaponType.WAR_STANDARD,
  )
  case MARTIAL_ARTIST extends ClassType(
    classRole = ClassRole.ATTACKER,
    classHp = 0.75,
    classAttack = 1.1,
    classHealingPower = 0.25,
    classDexterity = 1.2,
    classAgility = 1.2,
    classPhysDef = 0.15,
    classEtherDef = 0.15,
    weaponType = WeaponType.TURBO_CESTUS,
  )
  case TROUBADOUR extends ClassType(
    classRole = ClassRole.HEALER,
    classHp = 0.55,
    classAttack = 0.6,
    classHealingPower = 1.15,
    classDexterity = 1.1,
    classAgility = 1,
    classPhysDef = 0.1,
    classEtherDef = 0.3,
    weaponType = WeaponType.SIKEN_IVY,
  )
  case SERAPH extends ClassType(
    classRole = ClassRole.ATTACKER,
    classHp = 0.8,
    classAttack = 1.3,
    classHealingPower = 0.15,
    classDexterity = 1.0,
    classAgility = 0.85,
    classPhysDef = 0.2,
    classEtherDef = 0.1,
    weaponType = WeaponType.HEATSHIMMER_SPEAR,
  )
  case MACHINE_ASSASSIN extends ClassType(
    classRole = ClassRole.ATTACKER,
    classHp = 0.8,
    classAttack = 1.25,
    classHealingPower = 0.25,
    classDexterity = 1.2,
    classAgility = 0.75,
    classPhysDef = 0.2,
    classEtherDef = 0.1,
    weaponType = WeaponType.DOOM_AXES,
  )
  case LIFESAGE extends ClassType(
    classRole = ClassRole.HEALER,
    classHp = 0.65,
    classAttack = 0.75,
    classHealingPower = 1.3,
    classDexterity = 1.2,
    classAgility = 1.1,
    classPhysDef = 0.15,
    classEtherDef = 0.25,
    weaponType = WeaponType.ANIMA_SWORD,
  )
  case ROYAL_SUMMONER extends ClassType(
    classRole = ClassRole.ATTACKER,
    classHp = 0.65,
    classAttack = 1.3,
    classHealingPower = 0.6,
    classDexterity = 1.3,
    classAgility = 1.1,
    classPhysDef = 0.15,
    classEtherDef = 0.15,
    weaponType = WeaponType.ELEMENTAL_STAFF,
  )
  case NOPONIC_CHAMPION extends ClassType(
    classRole = ClassRole.DEFENDER,
    classHp = 1.2,
    classAttack = 0.95,
    classHealingPower = 0,
    classDexterity = 1.2,
    classAgility = 1.1,
    classPhysDef = 0.45,
    classEtherDef = 0.4,
    weaponType = WeaponType.INOSABERS,
  )

  case SOULHACKER_POWER extends ClassType(
    classRole = ClassRole.ATTACKER,
    classHp = 0.75,
    classAttack = 1.3,
    classHealingPower = 0.15,
    classDexterity = 1.05,
    classAgility = 0.75,
    classPhysDef = 0.2,
    classEtherDef = 0.1,
    weaponType = WeaponType.MIMIC_KNUCKLES,
    name = Some("Soulhacker"),
  )
  case SOULHACKER_STRIKE extends ClassType(
    classRole = ClassRole.ATTACKER,
    classHp = 0.75,
    classAttack = 1.15,
    classHealingPower = 0.3,
    classDexterity = 1.2,
    classAgility = 0.95,
    classPhysDef = 0.15,
    classEtherDef = 0.15,
    weaponType = WeaponType.MIMIC_KNUCKLES,
    name = Some("Soulhacker"),
  )
  case SOULHACKER_TOUGH extends ClassType(
    classRole = ClassRole.DEFENDER,
    classHp = 1.25,
    classAttack = 0.95,
    classHealingPower = 0.15,
    classDexterity = 0.95,
    classAgility = 0.65,
    classPhysDef = 0.45,
    classEtherDef = 0.35,
    weaponType = WeaponType.MIMIC_KNUCKLES,
    name = Some("Soulhacker"),
  )
  case SOULHACKER_DODGE extends ClassType(
    classRole = ClassRole.DEFENDER,
    classHp = 1.1,
    classAttack = 0.95,
    classHealingPower = 0.15,
    classDexterity = 1.05,
    classAgility = 1.2,
    classPhysDef = 0.35,
    classEtherDef = 0.45,
    weaponType = WeaponType.MIMIC_KNUCKLES,
    name = Some("Soulhacker"),
  )
  case SOULHACKER_HEAL extends ClassType(
    classRole = ClassRole.HEALER,
    classHp = 0.7,
    classAttack = 0.75,
    classHealingPower = 1.0,
    classDexterity = 1.1,
    classAgility = 0.9,
    classPhysDef = 0.1,
    classEtherDef = 0.3,
    weaponType = WeaponType.MIMIC_KNUCKLES,
    name = Some("Soulhacker"),
  )
  case SOULHACKER_BALANCED extends ClassType(
    classRole = ClassRole.HEALER,
    classHp = 0.65,
    classAttack = 0.9,
    classHealingPower = 0.75,
    classDexterity = 1.05,
    classAgility = 0.9,
    classPhysDef = 0.1,
    classEtherDef = 0.3,
    weaponType = WeaponType.MIMIC_KNUCKLES,
    name = Some("Soulhacker"),
  )

  case ATTACKER_LUCKY_SEVEN extends ClassType(
    classRole = ClassRole.ATTACKER,
    classHp = 0.9,
    classAttack = 1.4,
    classHealingPower = 0.5,
    classDexterity = 1.5,
    classAgility = 1.0,
    classPhysDef = 0.3,
    classEtherDef = 0.3,
    weaponType = WeaponType.LUCKY_SEVEN,
  )
  case HEALER_LUCKY_SEVEN extends ClassType(
    classRole = ClassRole.HEALER,
    classHp = 0.8,
    classAttack = 0.9,
    classHealingPower = 1.5,
    classDexterity = 1.5,
    classAgility = 1.0,
    classPhysDef = 0.2,
    classEtherDef = 0.4,
    weaponType = WeaponType.LUCKY_SEVEN,
  )
  case DEFENDER_LUCKY_SEVEN extends ClassType(
    classRole = ClassRole.DEFENDER,
    classHp = 1.2,
    classAttack = 1.0,
    classHealingPower = 0.5,
    classDexterity = 1.5,
    classAgility = 1.4,
    classPhysDef = 0.6,
    classEtherDef = 0.6,
    weaponType = WeaponType.LUCKY_SEVEN,
  )
