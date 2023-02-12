package dev.hawu.plugins.xenocraft
package skills.swordfighter

import data.{SkillType, User}
import skills.Skill

/** Covert Attack (Swordfighter): Reduces aggro generated from attacks by 65%
 */
object CovertAttack extends Skill(SkillType.COVERT_ATTACK):

  private val AGGRO_REDUCTION = 0.65

  override def apply(user: User): Unit = user.autoAggroGeneration -= AGGRO_REDUCTION

  override def unapply(user: User): Unit = user.autoAggroGeneration += AGGRO_REDUCTION
