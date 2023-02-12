package dev.hawu.plugins.xenocraft
package managers

import data.EnemyEntity
import listener.EnemyListener

import dev.hawu.plugins.api.Strings
import dev.hawu.plugins.api.events.Events
import org.bukkit.attribute.Attribute
import org.bukkit.boss.{BarColor, BarStyle, BossBar}
import org.bukkit.entity.{LivingEntity, Mob}
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.{Bukkit, ChatColor}

import scala.jdk.CollectionConverters.*

/** A singleton object dedicated to handling enemies stuff.
  */
object EnemyManager extends Initializable:

  /** Initializes the enemy manager with the [[pl]] and registers the corresponding [[EnemyListener]].
    */
  override def setUp(pl: JavaPlugin): Unit =
    Events.registerEvents(pl, EnemyListener)
    Bukkit.getWorlds.asScala.flatMap(_.getLivingEntities.asScala).filter(_ != null).filter(_.isInstanceOf[Mob]).filterNot(_.isDead)
      .map(_.asInstanceOf[Mob]).foreach(markAsEnemy(_))

  /** Marks a [[mob]] as a Xenocraft enemy.
    */
  def markAsEnemy(mob: Mob, overwrite: Boolean = false): Unit = if overwrite || !isMarked(mob) then
    val enemy = EnemyEntity.apply(mob)
    mob.setMetadata("xeno-enemy", FixedMetadataValue(Xenocraft.getInstance, enemy))
    mob.setMetadata("xeno-bossbar", FixedMetadataValue(Xenocraft.getInstance, createBossbar(mob)))
    mob.setCustomName(enemy.name)
    mob.setCustomNameVisible(true)

  /** Returns true if the [[mob]] provided has been marked with a valid [[EnemyEntity]] instance.
    */
  def isMarked(mob: Mob): Boolean = getEnemy(mob).isDefined

  private def createBossbar(mob: Mob, color: BarColor = BarColor.RED, style: BarStyle = BarStyle.SEGMENTED_20): BossBar =
    val enemy = getEnemy(mob).get
    Bukkit.createBossBar(Strings.color(s"&f${enemy.name}"), color, style)

  /** Returns the enemy instance from a mob.
    */
  def getEnemy(entity: Mob): Option[EnemyEntity] = entity.getMetadata("xeno-enemy").asScala.filter(_.value().isInstanceOf[EnemyEntity])
    .find(_.getOwningPlugin == Xenocraft.getInstance).map(_.value().asInstanceOf[EnemyEntity])

  override def tearDown(pl: JavaPlugin) = Bukkit.getWorlds.asScala.flatMap(_.getLivingEntities.asScala).filter(_ != null)
    .filter(_.isInstanceOf[Mob]).map(_.asInstanceOf[Mob]).foreach(unmark(_, true))

  /** Removes marks from a certain mob, and possibly heal them back if possible.
    */
  def unmark(mob: Mob, heal: Boolean = false): Unit =
    getBossbar(mob).foreach(_.removeAll())
    Seq("xeno-enemy", "xeno-bossbar").foreach(mob.removeMetadata(_, Xenocraft.getInstance))
    if heal then mob.setHealth(getMaxHealth(mob))

  /** Returns an option containing the supposed bossbar if it exists.
    */
  def getBossbar(mob: Mob): Option[BossBar] = mob.getMetadata("xeno-bossbar").asScala.filter(_.value().isInstanceOf[BossBar])
    .find(_.getOwningPlugin == Xenocraft.getInstance).map(_.value().asInstanceOf[BossBar])

  private def getMaxHealth(entity: LivingEntity): Double = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue

  /** Syncs the [[mob]]'s bossbar with its health.
    */
  def syncBossbar(mob: Mob): Unit =
    val entity = getEnemy(mob)
    val percentage = entity.map(e => e.hp / e.maxHp).getOrElse(mob.getHealth / getMaxHealth(mob))
    getBossbar(mob).foreach(_.setProgress(percentage min 1 max 0))
