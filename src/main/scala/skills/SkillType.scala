package dev.hawu.plugins.xenocraft
package skills

/**
 * Represents a type of the skill for enumerations.
 * One enum should only correspond to one skill.
 */
enum SkillType(masterSkill: Boolean = false, name: String = null):

  private val computedName = this.toString.split("_")
    .map { word =>
      word.length match
        case 1 | 2 => word.toLowerCase
        case _     => word.toLowerCase.capitalize
    }
    .mkString(" ")

  /**
   * Retrieves the display name of this skill type.
   *
   * @return the display name
   */
  def displayName: String = Option(name).getOrElse(computedName)

  case Dummy extends SkillType(false)
