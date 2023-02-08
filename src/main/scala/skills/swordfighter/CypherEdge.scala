package dev.hawu.plugins.xenocraft
package skills.swordfighter

import dev.hawu.plugins.xenocraft.data.{SkillType, User}
import dev.hawu.plugins.xenocraft.skills.Skill

/** The skill handler for the skill that increases critical hit chance.
 */
object CypherEdge extends Skill(SkillType.CYPHER_EDGE):
  private val CRIT_VALUE = 0.5

  override def apply(user: User): Unit = user.noncombatPctCrit += CRIT_VALUE

  override def unapply(user: User): Unit = user.noncombatPctCrit -= CRIT_VALUE
