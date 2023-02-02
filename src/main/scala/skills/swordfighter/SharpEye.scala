package dev.hawu.plugins.xenocraft
package skills.swordfighter

import dev.hawu.plugins.xenocraft.data.SkillType
import dev.hawu.plugins.xenocraft.skills.Skill
import dev.hawu.plugins.xenocraft.data.User
import dev.hawu.plugins.xenocraft.skills.SkillManager

/** The skill handler for
  */
object SharpEye extends Skill(SkillType.SHARP_EYE):
  private val DEXTERITY_VALUE = 0.5

  override def apply(user: User): Unit = user.pctDexterity += DEXTERITY_VALUE

  override def unapply(user: User): Unit = user.pctDexterity -= DEXTERITY_VALUE
