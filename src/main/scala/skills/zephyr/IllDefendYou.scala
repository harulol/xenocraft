package dev.hawu.plugins.xenocraft
package skills.zephyr

import I18n.tl
import UserMap.user
import data.{SkillType, User}
import events.combat.EnemyDamagePlayerEvent
import skills.Skill

import org.bukkit.entity.Player
import org.bukkit.event.{EventHandler, EventPriority}

import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

/** I'll Defend You (Zephyr): 16% chance to prevent attacks on allies within a fixed radius
 */
object IllDefendYou extends Skill(SkillType.ILL_DEFEND_YOU):

  private val RADIUS = 4.0
  private val CHANCE = 0.16
  private val players = mutable.Set.empty[UUID]

  override def apply(user: User): Unit = players += user.uuid

  override def unapply(user: User): Unit = players -= user.uuid

  @EventHandler(priority = EventPriority.LOWEST)
  private def onEnemyAttack(event: EnemyDamagePlayerEvent): Unit =
    val list = event.player.getWorld.getEntitiesByClass(classOf[Player]).asScala.filter(p => p.getUniqueId != event.player.getUniqueId)
      .filter(p => p.getLocation.distance(event.player.getLocation) <= RADIUS).flatMap(_.user).filter(u => players.contains(u.uuid))
      .map(u => u -> (ThreadLocalRandom.current().nextDouble() <= CHANCE))
    for (user, value) <- list if value do
      user.player.foreach(_.tl("ill-defend-you-proc", "player" -> event.player.getName))
      event.setCancelled(true)
