package dev.hawu.plugins.xenocraft
package combat

import scala.collection.mutable
import java.util.UUID
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import dev.hawu.plugins.xenocraft.data.EnemyEntity
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Entity
import org.bukkit.entity.Mob
import scala.jdk.CollectionConverters.*
import dev.hawu.plugins.api.Strings

/** Represents a manager for managing bossbars for enemies.
  */
object BossbarManager:

  private val bars = mutable.Map.empty[UUID, BossBar]

  /** Clears bossbar for an entity.
    *
    * @param entity
    *   the entity
    */
  def clear(entity: Entity): Unit =
    println("Clear was called for " + entity.getType().name())
    bars.remove(entity.getUniqueId()).foreach(_.removeAll())

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

  /** Updates the bossbar for an entity.
    */
  def updateBossbar(entity: Entity): Option[BossBar] =
    if entity == null || entity.isDead() || !entity.isInstanceOf[Mob] then
      clear(entity)
      return None

    val enemy = CombatManager.makeEnemy(entity.asInstanceOf[Mob])
    val bar = makeBar(enemy)
    bar.setProgress((enemy.hp / enemy.maxHp) min 1.0 max 0.0)
    Some(bar)

end BossbarManager
