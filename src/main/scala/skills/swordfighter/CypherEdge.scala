package dev.hawu.plugins.xenocraft
package skills.swordfighter

import data.{SkillType, User}
import skills.Skill

/** Cypher Edge (Swordfighter): Boosts Critical Rate by 50%
  */
object CypherEdge extends Skill(SkillType.CYPHER_EDGE):

  private val CRIT_VALUE = 0.5

  override def apply(user: User): Unit = user.noncombatPctCrit += CRIT_VALUE

  override def unapply(user: User): Unit = user.noncombatPctCrit -= CRIT_VALUE
