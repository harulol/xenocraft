package dev.hawu.plugins.xenocraft
package data

/** Represents a skill type.
  */
enum SkillType(_name: Option[String] = None, val isMaster: Boolean = false):

  /** Retrieves the properly formatted name of the skill type.
    *
    * @return
    *   the name
    */
  def name = _name.getOrElse(toString.split("_").map(_.toLowerCase.capitalize).mkString(" "))

  // ================================= //
  // SWORDFIGHTER SKILLS
  // ================================= //
  case SHARP_EYE, INSPIRIT
  case COVERT_ATTACK extends SkillType(isMaster = true)
  case CYPHER_EDGE extends SkillType(isMaster = true)
