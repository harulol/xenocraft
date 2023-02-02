package dev.hawu.plugins.xenocraft
package skills

import org.bukkit.event.Listener
import dev.hawu.plugins.xenocraft.data.SkillType
import dev.hawu.plugins.xenocraft.data.User
import scala.collection.mutable
import java.util.UUID

/** Represents a handler for a skill. Skills usually are listeners that act on an event, so inherently a skill always
  * implements a Listener
  *
  * @param skillType
  *   the skill type
  */
abstract class Skill(val skillType: SkillType) extends Listener:

  protected val applied = mutable.Set.empty[UUID]

  /** Checks if this skill should be applied to a player.
    *
    * @param user
    *   the user
    * @return
    *   true if the skill should be applied, false otherwise
    */
  final def shouldApply(user: User): Boolean = !applied.contains(user.uuid)

  /** Checks if this skill should be unapplied from a player.
    *
    * @param user
    *   the user
    * @return
    *   true if the skill should be unapplied, false otherwise
    */
  final def shouldUnapply(user: User): Boolean = applied.contains(user.uuid)

  /** Applies this skill to a player if it should be applied.
    *
    * @param user
    *   the user
    */
  final def safeApply(user: User): Unit = if shouldApply(user) then
    apply(user)
    applied += user.uuid

  /** Unapplies this skill from a player if it should be unapplied.
    *
    * @param user
    *   the user
    */
  final def safeUnapply(user: User): Unit = if shouldUnapply(user) then
    unapply(user)
    applied -= user.uuid

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
