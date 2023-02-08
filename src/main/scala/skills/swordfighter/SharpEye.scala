package dev.hawu.plugins.xenocraft
package skills.swordfighter

import data.{SkillType, User}
import skills.{Skill, SkillManager}

/** The skill handler for the skill that boosts your Dexterity.
 */
object SharpEye extends Skill(SkillType.SHARP_EYE):
  private val DEXTERITY_VALUE = 0.5

  override def apply(user: User): Unit = user.pctDexterity += DEXTERITY_VALUE

  override def unapply(user: User): Unit = user.pctDexterity -= DEXTERITY_VALUE
