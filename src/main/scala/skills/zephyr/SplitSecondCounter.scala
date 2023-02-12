package dev.hawu.plugins.xenocraft
package skills.zephyr

import I18n.tl
import UserMap.user
import data.{SkillType, User}
import events.combat.EnemyDamagePlayerEvent
import managers.CombatManager
import skills.Skill

import org.bukkit.event.{EventHandler, EventPriority}

import java.util.UUID
import scala.collection.mutable

/** Split-Second Counter (Zephyr): Deals 200% of Attack damage when you evade an attack
 */
object SplitSecondCounter extends Skill(SkillType.SPLIT_SECOND_COUNTER):

  private val MULTIPLIER = 2.0
  private val players = mutable.Set.empty[UUID]

  override def apply(user: User): Unit = players += user.uuid

  override def unapply(user: User): Unit = players -= user.uuid

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private def onEnemyDamage(event: EnemyDamagePlayerEvent): Unit =
    if (event.isEvaded || !event.isHit) && players.contains(event.player.getUniqueId) then
      val damage = MULTIPLIER * event.player.user.get.attack
      event.player.tl("split-second-counter-proc", "damage" -> damage.round, "entity" -> event.getEntity.getCustomName)
      CombatManager.damage(event.enemy, damage, "&b")
