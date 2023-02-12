package dev.hawu.plugins.xenocraft
package skills.swordfighter

import data.{SkillType, User}
import skills.Skill

/** Sharp Eye (Swordfighter): Boosts Dexterity by 50%
 */
object SharpEye extends Skill(SkillType.SHARP_EYE):

  private val DEXTERITY_VALUE = 0.5

  override def apply(user: User): Unit = user.pctDexterity += DEXTERITY_VALUE

  override def unapply(user: User): Unit = user.pctDexterity -= DEXTERITY_VALUE
