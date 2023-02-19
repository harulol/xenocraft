package dev.hawu.plugins.xenocraft
package listener

import data.{ArtReaction, ArtType, Attributable}
import events.PlayerIncapacitateEvent
import events.blades.PlayerPostUnsheatheEvent
import events.combat.{EntityComboReactionEvent, EntitySmashReactionEvent}

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.api.events.Events
import org.bukkit.entity.{ArmorStand, LivingEntity}
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.{EventHandler, EventPriority, Listener}
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import org.bukkit.{Bukkit, Particle}
import org.spigotmc.event.entity.EntityDismountEvent

import java.util.UUID
import scala.collection.mutable

/** Singleton listener for dealing with reactions.
  */
//noinspection DuplicatedCode
object ReactionListener extends Listener:

  // Map to hold tasks to animate effects.
  private val effects = mutable.Map.empty[UUID, BukkitTask]
  private val cleanUps = mutable.Map.empty[UUID, () => Unit]

  @EventHandler
  private def onQuit(event: PlayerQuitEvent): Unit = cleanUp(event.getPlayer.getUniqueId)

  private def cleanUp(uuid: UUID): Unit =
    effects.remove(uuid).foreach(_.cancel())
    cleanUps.remove(uuid).foreach(_())

  @EventHandler
  private def onDeath(event: EntityDeathEvent): Unit = cleanUp(event.getEntity.getUniqueId)

  @EventHandler
  private def onIncapacitate(event: PlayerIncapacitateEvent): Unit = cleanUp(event.getPlayer.getUniqueId)

  @EventHandler(priority = EventPriority.MONITOR)
  private def onComboReaction(event: EntityComboReactionEvent): Unit = event.reaction match
    case ArtReaction.TOPPLE => startToppleTask(event.target)
    case ArtReaction.LAUNCH => startLaunchTask(event.target)
    case ArtReaction.DAZE   => startDazeTask(event.target)

  private def startToppleTask(target: Attributable): Unit =
    val entity = Bukkit.getEntity(target.uuid).asInstanceOf[LivingEntity]
    val stand: ArmorStand = entity.getWorld.spawn(
      entity.getLocation.subtract(0, entity.getHeight, 0),
      classOf[ArmorStand],
      as => {
        as.setVisible(false)
        as.setGravity(false)
        as.setInvulnerable(true)
      },
    )
    stand.addPassenger(entity)

    val listener = Events.newSubscription(classOf[EntityDismountEvent]).filter(!_.getEntity.isDead)
      .filter(_.getEntity.getUniqueId == target.uuid).filter(_ => target.reaction.contains(ArtReaction.TOPPLE))
      .handler(_.setCancelled(true)).build(Xenocraft.getInstance)

    schedule(
      target,
      0, {
        if entity.isDead || !target.reaction.contains(ArtReaction.TOPPLE) then
          listener.close()
          cleanUp(target.uuid)
      },
    )

  private def startLaunchTask(target: Attributable): Unit =
    val entity = Bukkit.getEntity(target.uuid).asInstanceOf[LivingEntity]
    entity.setVelocity(Vector(0.0, 1.5, 0.0))

    val location = entity.getLocation.add(0.0, 2.0, 0.0)
    var angle = 0.0
    schedule(
      target,
      1, {
        entity.teleport(location)
        entity.getWorld.spawnParticle(Particle.CRIT_MAGIC, location.clone().add(math.sin(angle), 0, math.cos(angle)), 1, 0, 0, 0, 0.1)
        angle += math.Pi / 20
      },
    )

    scheduleCleanUp(target, entity.setFallDistance(0))

  private def scheduleCleanUp(entity: Attributable, action: => Unit): Unit =
    cleanUps.remove(entity.uuid).foreach(_())
    cleanUps.put(entity.uuid, () => action)

  private def startDazeTask(target: Attributable): Unit =
    val entity = Bukkit.getEntity(target.uuid).asInstanceOf[LivingEntity]
    entity.setSwimming(true)
    
    val oldLocation = entity.getLocation
    schedule(target, 1, {
      entity.getWorld.spawnParticle(Particle.CRIT, entity.getLocation, 100, 0.5, 0.5, 0.5, 0.1)
      entity.teleport(oldLocation)
    })

  private def schedule(entity: Attributable, delay: Long, action: => Unit): Unit =
    effects.remove(entity.uuid).foreach(_.cancel())
    val task = Tasks.run(_ => action).plugin(Xenocraft.getInstance).delay(delay).period(1).run()
    effects.put(entity.uuid, task)

  private def startSmashTask(target: Attributable, attacker: Attributable): Unit =
    val entity = Bukkit.getEntity(target.uuid).asInstanceOf[LivingEntity]
    entity.setVelocity(Vector(0.0, -2.0, 0.0))
