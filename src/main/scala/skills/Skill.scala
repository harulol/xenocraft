package dev.hawu.plugins.xenocraft
package skills

import org.bukkit.event.Listener
import dev.hawu.plugins.xenocraft.data.SkillType
import dev.hawu.plugins.xenocraft.data.User

/** Represents a handler for a skill. Skills usually are listeners that act on an event, so inherently a skill always
  * implements a Listener
  *
  * @param skillType
  *   the skill type
  */
abstract class Skill(val skillType: SkillType) extends Listener:

  /** Applies this skill to a player.
    *
    * @param user
    *   the user
    */
  def apply(user: User): Unit

  /** Unapplies this skill from a player.
    *
    * @param user
    *   the player
    */
  def unapply(user: User): Unit

end Skill
