package dev.hawu.plugins.xenocraft
package managers

import data.SkillType
import skills.*

import dev.hawu.plugins.api.events.Events
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

import scala.collection.mutable

/** Represents a single object to manage how skill types are bound to a specific skill object.
 */
object SkillManager extends Initializable:

  private val map = mutable.Map.empty[SkillType, Skill]

  /** Attempts to initialize the skill manager.
   */
  override def setUp(pl: JavaPlugin): Unit =
    bind(swordfighter.SharpEye, swordfighter.CypherEdge, swordfighter.CovertAttack, swordfighter.Inspirit)
    bind(zephyr.StormyGale, zephyr.IllDefendYou, zephyr.SplitSecondCounter, zephyr.EthersSanctuary)

  /** Binds a skill.
   *
   * @param skills
   * the skills
   */
  def bind(skills: Skill*): Unit =
    skills.foreach(it => map += it.skillType -> it)
    Events.registerEvents(Xenocraft.getInstance, skills *)

  override def tearDown(pl: JavaPlugin): Unit = map.clear()

  /** Unbinds a skill.
   *
   * @param skill
   * the skill
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
