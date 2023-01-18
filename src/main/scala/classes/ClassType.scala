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
enum ClassType(val nation: ClassNation, val role: ClassRole):

  private var _wielderName: Option[String] = None
  private var _wielderTitle: Option[String] = None
  private var _weaponName: Option[String] = None

  /**
   * Retrieves the properly formatted name of
   * this class type.
   *
   * @return the name of this class type
   */
  def name = this.toString.toLowerCase.capitalize

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

  case SWORDFIGHTER extends ClassType(ClassNation.KEVES, ClassRole.ATTACKER)

/**
 * The companion object for [[ClassType]].
 */
object ClassType:

  /**
   * Initializes the wielder name, wielder title and weapon
   * name for all class types.
   *
   * @param pl the plugin to load the resource
   */
  def initialize(pl: JavaPlugin): Unit =
    val resource = InputStreamReader(pl.getResource("classes.yml"))
    val config = YamlConfiguration.loadConfiguration(resource)
    for key <- config.getKeys(false).asScala do
      val classType = ClassType.valueOf(key.toUpperCase)
      classType._wielderName = Option(config.getString(s"$key.wielder-name"))
      classType._wielderTitle = Option(config.getString(s"$key.wielder-title"))
      classType._weaponName = Option(config.getString(s"$key.weapon-name"))
  end initialize
