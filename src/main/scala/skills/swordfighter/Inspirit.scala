package dev.hawu.plugins.xenocraft
package skills.swordfighter

import data.{SkillType, User}
import events.combat.PlayerDealDamageEvent
import skills.Skill

import org.bukkit.event.EventHandler

import java.util.UUID
import scala.collection.mutable

/** Inspirit (Swordfighter): Boosts damage dealt by 150% when fighting a unique or boss monster
 */
object Inspirit extends Skill(SkillType.INSPIRIT):

  private val DAMAGE_BOOST = 1.5
  private val players = mutable.Set.empty[UUID]

  override def apply(user: User): Unit = players += user.uuid

  override def unapply(user: User): Unit = players -= user.uuid

  @EventHandler
  private def onPlayerDamage(event: PlayerDealDamageEvent): Unit =
    if (event.entity.isUnique || event.entity.isElite) && players.contains(event.user.uuid) then event.damageBonus1 += DAMAGE_BOOST
