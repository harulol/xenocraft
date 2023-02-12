package dev.hawu.plugins.xenocraft
package skills.zephyr

import data.{SkillType, User}
import skills.Skill

/** Ether's Sanctuary (Zephyr): Boosts Ether Defense by 30%
 */
object EthersSanctuary extends Skill(SkillType.ETHERS_SANCTUARY):

  private val PCT_ETHER_DEF_BOOST = 0.3

  override def apply(user: User): Unit = user.pctEtherDef += PCT_ETHER_DEF_BOOST

  override def unapply(user: User): Unit = user.pctEtherDef -= PCT_ETHER_DEF_BOOST
