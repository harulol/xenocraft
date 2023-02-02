package dev.hawu.plugins.xenocraft
package skills

import dev.hawu.plugins.xenocraft.data.SkillType
import scala.collection.mutable
import dev.hawu.plugins.api.events.Events
import org.bukkit.event.HandlerList
import dev.hawu.plugins.xenocraft.skills.swordfighter.*

/** Represents a single object to manage how skill types are bound to a specific skill object.
  */
object SkillManager:

  private val map = mutable.Map.empty[SkillType, Skill]

  /** Attempts to initialize the skill manager.
    */
  def initialize(): Unit = bind(SharpEye)

  /** Binds a skill.
    *
    * @param skill
    *   the skill
    */
  def bind(skill: Skill): Unit =
    map += skill.skillType -> skill
    Events.registerEvents(Xenocraft.getInstance, skill)

  /** Unbinds a skill.
    *
    * @param skill
    *   the skill
    */
  def unbind(skill: Skill | SkillType): Unit = skill match
    case s: Skill =>
      map -= s.skillType
      HandlerList.unregisterAll(s)
    case s: SkillType => map.remove(s).foreach(HandlerList.unregisterAll)

  /** Retrieves the skill bound to the skill type.
    *
    * @param skill
    *   the skill type
    * @return
    *   the skill, if any
    */
  def get(skill: SkillType): Option[Skill] = map.get(skill)

end SkillManager
