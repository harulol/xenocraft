package dev.hawu.plugins.xenocraft
package listener

import managers.EnemyManager

import dev.hawu.plugins.api.Tasks
import org.bukkit.entity.Mob
import org.bukkit.event.entity.*
import org.bukkit.event.{EventHandler, EventPriority, Listener}

import scala.util.Try

/** A listener for handling additional logic on events for [[managers.EnemyManager]].
  *
  * This consists of cleaning up and setting up stuff. Cleaning up data when an enemy dies, or somehow leaves the world?
  */
object EnemyListener extends Listener:

  // Unmark all mobs that die.
  @EventHandler(priority = EventPriority.MONITOR)
  private def onEntityDeath(event: EntityDeathEvent): Unit = event.getEntity match
    case mob: Mob => EnemyManager.unmark(mob)
    case _ =>

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private def onEntityExplode(event: EntityExplodeEvent): Unit = Try(event.getEntity.asInstanceOf[Mob]).foreach(EnemyManager.unmark(_))

  // Stupid transforming mobs.
  @EventHandler
  private def onTransform(event: EntityTransformEvent): Unit =
    Try(event.getEntity.asInstanceOf[Mob]).foreach(EnemyManager.unmark(_))
    Try(event.getTransformedEntity.asInstanceOf[Mob]).foreach(EnemyManager.markAsEnemy(_))

  // Zombies can go outside now!
  @EventHandler
  private def onCombust(event: EntityCombustEvent): Unit = event.setCancelled(true)

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private def onSpawn(event: EntitySpawnEvent): Unit =
    Tasks.run(_ => Try(event.getEntity.asInstanceOf[Mob]).foreach(EnemyManager.markAsEnemy(_))).run()
