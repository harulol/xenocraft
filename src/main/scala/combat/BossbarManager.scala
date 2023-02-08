package dev.hawu.plugins.xenocraft
package combat

import data.EnemyEntity

import dev.hawu.plugins.api.Strings
import org.bukkit.Bukkit
import org.bukkit.boss.{BarColor, BarStyle, BossBar}
import org.bukkit.entity.{Entity, Mob, Player}

import java.util.UUID
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

/** Represents a manager for managing bossbars for enemies.
 */
object BossbarManager:

  private val bars = mutable.Map.empty[UUID, BossBar]

  /** Updates the bossbar for an entity.
   */
  def updateBossbar(entity: Entity): Option[BossBar] =
    if entity == null || entity.isDead || !entity.isInstanceOf[Mob] then
      clear(entity)
      return None

    val enemy = CombatManager.makeEnemy(entity.asInstanceOf[Mob])
    val bar = makeBar(enemy)
    bar.setProgress((enemy.hp / enemy.maxHp) min 1.0 max 0.0)
    Some(bar)

  /** Clears bossbar for an entity.
   *
   * @param entity
   * the entity
   */
  def clear(entity: Entity): Unit = bars.remove(entity.getUniqueId).foreach(_.removeAll())

  /** Attempts to make an entity's bossbar.
   *
    * @param enemy
    *   The enemy entity to make a bossbar for.
    */
  def makeBar(enemy: EnemyEntity): BossBar =
    if bars.contains(enemy.uuid) then return bars(enemy.uuid)

    val bar = Bukkit.createBossBar(Strings.color(s"&f${enemy.name}"), BarColor.RED, BarStyle.SOLID)
    bar.setProgress((enemy.hp / enemy.maxHp) min 1.0 max 0.0)
    bars(enemy.uuid) = bar
    bar

end BossbarManager
