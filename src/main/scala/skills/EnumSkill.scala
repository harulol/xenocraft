package dev.hawu.plugins.xenocraft
package skills

import dev.hawu.plugins.xenocraft.classes.EnumClass
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin

import java.io.InputStreamReader

/**
 * Represents a type of the skill for enumerations.
 * One enum should only correspond to one skill.
 */
enum EnumSkill(val cls: EnumClass, val isMaster: Boolean = false, name: String = null):

  private val computedName = this.toString.split("_")
    .map { word =>
      word.length match
        case 1 | 2 => word.toLowerCase
        case _ => word.toLowerCase.capitalize
    }
    .mkString(" ")
  private var _description: Option[String] = None

  /**
   * Retrieves the display name of this skill type.
   *
   * @return the display name
   */
  def displayName: String = Option(name).getOrElse(computedName)

  /**
   * Retrieves the description of this skill type.
   *
   * @return the description
   */
  def description: String = _description.getOrElse("No description provided.")

  case SHARP_EYE extends EnumSkill(EnumClass.SWORDFIGHTER, false)
  case COVERT_ATTACK extends EnumSkill(EnumClass.SWORDFIGHTER, true)
  case CYPHER_EDGE extends EnumSkill(EnumClass.SWORDFIGHTER, true)
  case INSPIRIT extends EnumSkill(EnumClass.SWORDFIGHTER, false)

/**
 * The companion object of the [[EnumSkill]] enumeration.
 */
object EnumSkill:

  /**
   * Initializes all skills that may have the description.
   *
   * @param pl the plugin instance
   */
  def initialize(pl: JavaPlugin): Unit =
    val resource = InputStreamReader(pl.getResource("skills.yml"))
    val yaml = YamlConfiguration.loadConfiguration(resource)
    for skill <- values do
      skill._description = Option(yaml.getString(skill.toString.replace('_', '-').toLowerCase))
  end initialize
