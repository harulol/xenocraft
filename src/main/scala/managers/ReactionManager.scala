package dev.hawu.plugins.xenocraft
package managers

import UserMap.user
import data.{ArtReaction, Attributable}
import events.combat.EntityReactionEvent
import utils.Hologram

import dev.hawu.plugins.api.Tasks
import org.bukkit.Bukkit
import org.bukkit.entity.{LivingEntity, Mob}
import org.bukkit.plugin.java.JavaPlugin

import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.util.Try

/** Singleton object for managing combo effects.
  */
//noinspection DuplicatedCode
object ReactionManager extends Initializable:

  private val KNOCKBACK_TEXT = "&e◖ Knockback"
  private val BLOWDOWN_TEXT = "&e⯊ Blowdown"
  private val tracked = mutable.Set.empty[Attributable]

  private var plugin: Option[JavaPlugin] = None

  override def setUp(pl: JavaPlugin): Unit =
    plugin = Some(pl)
    Tasks.run(_ => countDownAll()).delay(0).period(1).async(true).plugin(pl).run()

  /** Countdown all reaction frames counter for all attributables currently being tracked.
    */
  def countDownAll(): Unit = tracked.filter(_.reaction.isDefined)
    .foreach(u => if u.reactionFrames <= 0 then u.reaction = None else u.reactionFrames -= 1)

  /** Apply the effect Knockback to a [[targetAttributable]] by [[attackerAttributable]].
    */
  def inflictKnockback(attackerAttributable: Attributable, targetAttributable: Attributable): Unit =
    val attacker = Bukkit.getEntity(attackerAttributable.uuid).asInstanceOf[LivingEntity]
    val target = Bukkit.getEntity(targetAttributable.uuid).asInstanceOf[LivingEntity]

    if callEvent(attacker -> attackerAttributable, target -> targetAttributable, ArtReaction.KNOCKBACK) then return ()

    val direction = attacker.getLocation.getDirection
    val rotation = attacker.getLocation.setDirection(direction.clone().multiply(-1))
    target.setRotation(rotation.getYaw, rotation.getPitch)
    Tasks.run(_ => {
      Hologram.spawnAround(target.getEyeLocation, 40, KNOCKBACK_TEXT)
      target.setVelocity(direction.setY(0).normalize())
    }).plugin(plugin.get).run()

  /** Apply the effect Blowdown to a [[target]] by [[attacker]].
    */
  def inflictBlowdown(attacker: Attributable, target: Attributable): Unit =
    val entity = Bukkit.getEntity(target.uuid).asInstanceOf[LivingEntity]
    val damager = Bukkit.getEntity(attacker.uuid).asInstanceOf[LivingEntity]

    if callEvent(damager -> attacker, entity -> target, ArtReaction.BLOWDOWN) then return ()
    val direction = damager.getLocation.getDirection
    val rotation = damager.getLocation.setDirection(direction.clone().multiply(-1))
    entity.setRotation(rotation.getYaw, rotation.getPitch)
    Tasks.run(_ => {
      Hologram.spawnAround(entity.getEyeLocation, 40, BLOWDOWN_TEXT)
      entity.setVelocity(direction.setY(0.25).normalize())
    }).plugin(plugin.get).run()

  private def callEvent(attacker: (LivingEntity, Attributable), target: (LivingEntity, Attributable), reaction: ArtReaction): Boolean =
    val event = EntityReactionEvent(attacker._1, target._1, attacker._2, target._2, reaction)
    Bukkit.getPluginManager.callEvent(event)
    event.isCancelled || event.canResist
