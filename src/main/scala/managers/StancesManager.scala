package dev.hawu.plugins.xenocraft
package managers

import UserMap.user
import events.stances.PlayerStanceChangeEvent
import listener.StancesListener
import stances.Stance

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.api.events.{ClosableListener, Events}
import dev.hawu.plugins.api.particles.{ParticleEffect, ParticleEnum, PredefParticles}
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

import java.util.UUID
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

/** Singleton object for dealing with stances.
  */
object StancesManager extends Initializable:

  private val currentStances = mutable.Map.empty[UUID, Stance]
  private val cancelTasks = mutable.Map.empty[UUID, BukkitTask]

  private var plugin: Option[JavaPlugin] = None

  /** Applies a new [[stance]] to the [[player]], while also clearing the stance beforehand.
    */
  def applyStance(player: Player, stance: Stance, fusion: Boolean = false): Unit =
    clearStance(player)
    val event = PlayerStanceChangeEvent(player, fusion)
    Bukkit.getPluginManager.callEvent(event)
    stance.safeApply(player.user.get)

    val ticks = (event.finalDuration * 20).round
    currentStances += player.getUniqueId -> stance
    cancelTasks += player.getUniqueId -> Tasks.run(_ => stance.safeUnapply(player.user.get)).plugin(plugin.get).delay(ticks).run()

  /** Clears the stance of a [[player]].
    */
  def clearStance(player: Player): Unit =
    cancelTasks.remove(player.getUniqueId).foreach(_.cancel())
    currentStances.remove(player.getUniqueId).foreach(_.safeUnapply(player.user.get))

  override def setUp(pl: JavaPlugin): Unit =
    plugin = Some(pl)
    Events.registerEvents(pl, StancesListener)
    Tasks.run(_ => Bukkit.getOnlinePlayers.asScala.filter(isStanceApplied).foreach(playStanceEffect)).delay(0).period(4).run()

  /** Plays the stance effect for the [[player]].
    */
  def playStanceEffect(player: Player): Unit =
    val effect = ParticleEffect.of(ParticleEnum.CLOUD).offset(0, 0, 0).setParticleCount(1).setParticleData(0.1).build()
    PredefParticles.drawFlatCircle(1, math.Pi / 20, player.getLocation, effect, Bukkit.getOnlinePlayers.asScala.toList.asJava)

  /** Checks if the [[player]] has a current active stance.
    */
  def isStanceApplied(player: Player): Boolean = currentStances.get(player.getUniqueId).exists(_.isApplied(player.user.get))

  override def tearDown(pl: JavaPlugin): Unit =
    currentStances.values.foreach(HandlerList.unregisterAll)
    cancelTasks.values.foreach(_.cancel())
    cancelTasks.clear()
    currentStances.clear()
