package dev.hawu.plugins.xenocraft
package skills.zephyr

import data.{SkillType, User}
import skills.Skill

/** Stormy Gale (Zephyr): Boosts Agility by 30%
 */
object StormyGale extends Skill(SkillType.STORMY_GALE):

  private val PCT_AGILITY_BOOST = 0.3

  override def apply(user: User): Unit = user.pctAgility += PCT_AGILITY_BOOST

  override def unapply(user: User): Unit = user.pctAgility -= PCT_AGILITY_BOOST
