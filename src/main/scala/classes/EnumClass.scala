package dev.hawu.plugins.xenocraft
package classes

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin

import java.io.InputStreamReader
import scala.jdk.CollectionConverters.*

/**
 * Represents the type of class a player
 * can pick.
 *
 * Each class should have their own set
 * of skills, arts and ways to have fun.
 *
 * Side note: the [[role]] part does not matter
 * if the class type is SOULHACKER.
 */
enum EnumClass(val nation: ClassNation, val role: ClassRole):

  private var _wielderName: Option[String] = None
  private var _wielderTitle: Option[String] = None
  private var _weaponName: Option[String] = None

  /**
   * Retrieves the properly formatted name of
   * this class type.
   *
   * @return the name of this class type
   */
  def name = this.toString.split("_").map(_.toLowerCase.capitalize).mkString(" ")

  /**
   * Gets the name of this enum in a kebab case.
   *
   * @return the name
   */
  def kebabCase = this.toString.split("_").map(_.toLowerCase).mkString("-")

  /**
   * Attempts to retrieve the name of the wielder
   * for the class. Will throw an error if not initialized.
   *
   * @return the non-null value
   */
  def wielderName = _wielderName.get

  /**
   * Attempts to retrieve the title of the wielder
   * for the class. Will throw an error if not initialized.
   *
   * @return the non-null value
   */
  def wielderTitle = _wielderTitle.get

  /**
   * Attempts to retrieve the name of the weapon
   * for the class. Will throw an error if not initialized.
   *
   * @return the non-null value
   */
  def weaponName = _weaponName.get

  case SWORDFIGHTER extends EnumClass(ClassNation.KEVES, ClassRole.ATTACKER)
  case ZEPHYR extends EnumClass(ClassNation.AGNUS, ClassRole.DEFENDER)
  case MEDIC_GUNNER extends EnumClass(ClassNation.KEVES, ClassRole.HEALER)
  case TACTICIAN extends EnumClass(ClassNation.AGNUS, ClassRole.HEALER)
  case HEAVY_GUARD extends EnumClass(ClassNation.KEVES, ClassRole.DEFENDER)
  case OGRE extends EnumClass(ClassNation.AGNUS, ClassRole.ATTACKER)
  case FLASH_FENCER extends EnumClass(ClassNation.KEVES, ClassRole.ATTACKER)
  case WAR_MEDIC extends EnumClass(ClassNation.KEVES, ClassRole.HEALER)
  case GUARDIAN_COMMANDER extends EnumClass(ClassNation.KEVES, ClassRole.DEFENDER)
  case THAUMATURGE extends EnumClass(ClassNation.AGNUS, ClassRole.HEALER)
  case YUMSMITH extends EnumClass(ClassNation.KEVES, ClassRole.ATTACKER)
  case FULL_METAL_JAGUAR extends EnumClass(ClassNation.KEVES, ClassRole.ATTACKER)
  case STRATEGOS extends EnumClass(ClassNation.AGNUS, ClassRole.HEALER)
  case STALKER extends EnumClass(ClassNation.AGNUS, ClassRole.ATTACKER)
  case LONE_EXILE extends EnumClass(ClassNation.KEVES, ClassRole.DEFENDER)
  case INCURSOR extends EnumClass(ClassNation.AGNUS, ClassRole.ATTACKER)
  case LOST_VANGUARD extends EnumClass(ClassNation.AGNUS, ClassRole.DEFENDER)
  case SIGNIFIER extends EnumClass(ClassNation.AGNUS, ClassRole.HEALER)
  case SOULHACKER extends EnumClass(ClassNation.KEVES, ClassRole.ATTACKER)
  case MARTIAL_ARTIST extends EnumClass(ClassNation.AGNUS, ClassRole.ATTACKER)
  case TROUBADOUR extends EnumClass(ClassNation.AGNUS, ClassRole.HEALER)
  case SERAPH extends EnumClass(ClassNation.AGNUS, ClassRole.ATTACKER)
  case MACHINE_ASSASSIN extends EnumClass(ClassNation.KEVES, ClassRole.ATTACKER)
  case LIFESAGE extends EnumClass(ClassNation.AGNUS, ClassRole.HEALER)
  case ROYAL_SUMMONER extends EnumClass(ClassNation.KEVES, ClassRole.ATTACKER)
  case NOPONIC_CHAMPION extends EnumClass(ClassNation.AGNUS, ClassRole.DEFENDER)

/**
 * The companion object for [[EnumClass]].
 */
object EnumClass:

  /**
   * Initializes the wielder name, wielder title and weapon
   * name for all class types.
   *
   * @param pl the plugin to load the resource
   */
  def initialize(pl: JavaPlugin): Unit =
    val resource = InputStreamReader(pl.getResource("classes.yml"))
    val config = YamlConfiguration.loadConfiguration(resource)

    for enumClass <- EnumClass.values do
      enumClass._wielderName = Option(config.getString(s"${enumClass.kebabCase}.wielder-name"))
      enumClass._wielderTitle = Option(config.getString(s"${enumClass.kebabCase}.wielder-title"))
      enumClass._weaponName = Option(config.getString(s"${enumClass.kebabCase}.weapon-name"))
  end initialize
