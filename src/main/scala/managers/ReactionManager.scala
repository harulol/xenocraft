package dev.hawu.plugins.xenocraft
package managers

import UserMap.user
import data.{ArtReaction, ArtType, Attributable}
import events.combat.{EntityComboReactionEvent, EntityReactionEvent, EntitySmashReactionEvent}
import listener.ReactionListener
import utils.Hologram

import dev.hawu.plugins.api.events.Events
import dev.hawu.plugins.api.misc.Raytracing
import dev.hawu.plugins.api.particles.{ParticleEffect, ParticleEnum, PredefParticles}
import dev.hawu.plugins.api.{MathUtils, Tasks}
import org.bukkit.*
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.{LivingEntity, Mob}
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.{PotionEffect, PotionEffectType}
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector

import java.util.UUID
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.util.Try

/** Singleton object for managing combo effects.
  */
object ReactionManager extends Initializable:

  private val KNOCKBACK_TEXT = "&e◖ Knockback"
  private val BLOWDOWN_TEXT = "&e⯊ Blowdown"
  private val BREAK_TEXT = "&c&l⫽ Break"
  private val TOPPLE_TEXT = "&c&l⤾ Topple"
  private val LAUNCH_TEXT = "&c&l⤒ Launch"
  private val SMASH_TEXT = "&c&l⤓ Smash"
  private val DAZE_TEXT = "&c&l✪ Daze"
  private val BURST_TEXT = "&c&l⚙ Burst"
  private val tracked = mutable.Set.empty[Attributable]

  private var plugin: Option[JavaPlugin] = None

  override def setUp(pl: JavaPlugin): Unit =
    plugin = Some(pl)
    Events.registerEvents(pl, ReactionListener)
    Tasks.run(_ => countDownAll()).delay(0).period(1).async(true).plugin(pl).run()

  /** Countdown all reaction frames counter for all attributables currently being tracked.
    */
  def countDownAll(): Unit = for attributable <- tracked.filter(_.reaction.isDefined) do
    if attributable.reactionFrames <= 0 then
      attributable.reaction = None
      tracked -= attributable
    else attributable.reactionFrames -= 1

  override def tearDown(pl: JavaPlugin): Unit =
    tracked.clear()
    plugin = None

  /** Attempts to inflict a [[reaction]] on [[target]]. This will do nothing if [[canInflict]] yields `false`.
    *
    * Does not support ''Smash''.
    */
  def inflict(attacker: Attributable, target: Attributable, reaction: ArtReaction): Unit =
    if reaction == ArtReaction.SMASH || !canInflict(target, reaction) then return ()

    val entity = Bukkit.getEntity(target.uuid).asInstanceOf[LivingEntity]
    val damager = Bukkit.getEntity(attacker.uuid).asInstanceOf[LivingEntity]
    if callEvent(damager -> attacker, entity -> target, reaction) then return ()

    reaction match
      case ArtReaction.KNOCKBACK =>
        rotateEntityToFace(entity, damager)
        Hologram.spawnAround(entity.getEyeLocation, 40, KNOCKBACK_TEXT)
        entity.setVelocity(damager.getLocation.getDirection.setY(0).normalize())
      case ArtReaction.BLOWDOWN =>
        rotateEntityToFace(entity, damager)
        Hologram.spawnAround(entity.getEyeLocation, 40, BLOWDOWN_TEXT)
        entity.setVelocity(damager.getLocation.getDirection.setY(0.25).normalize())
      case _ =>
        val finalFrames = callReactionEvent(attacker, target, reaction).finalDuration.toInt
        reaction match
          case ArtReaction.BREAK =>
            Hologram.spawnAround(entity.getEyeLocation, 40, BREAK_TEXT)
            target.reactionFrames = finalFrames
            target.reaction = Some(reaction)
          case ArtReaction.TOPPLE =>
            Hologram.spawnAround(entity.getEyeLocation, 40, TOPPLE_TEXT)
            target.reactionFrames = finalFrames
            target.reaction = Some(reaction)
          case ArtReaction.LAUNCH =>
            Hologram.spawnAround(entity.getEyeLocation, 40, LAUNCH_TEXT)
            target.reactionFrames += finalFrames
            target.reaction = Some(reaction)
          case ArtReaction.DAZE =>
            Hologram.spawnAround(entity.getEyeLocation, 40, DAZE_TEXT)
            target.reactionFrames += finalFrames
            target.reaction = Some(reaction)
          case ArtReaction.BURST =>
            Hologram.spawnAround(entity.getEyeLocation, 40, BURST_TEXT)
            target.reactionFrames = 0
          case _ => ()

        playReactionSlash(entity)
        tracked += target

  private def rotateEntityToFace(entity: LivingEntity, target: LivingEntity): Unit =
    val direction = target.getLocation.getDirection
    val rotation = target.getLocation.setDirection(direction.clone().multiply(-1))
    entity.setRotation(rotation.getYaw, rotation.getPitch)

  private def playReactionSlash(target: LivingEntity): Unit =
    val middle = target.getLocation.add(0, target.getHeight, 0)
    val direction = target.getLocation.getDirection

    val from = middle.clone().add(MathUtils.getLeftUnit(direction)).add(0, 1, 0)
    val to = middle.clone().add(MathUtils.getRightUnit(direction)).add(0, -1, 0)

    val dustOptions = DustOptions(Color.RED, 1)
    Raytracing.startNew().origin(from).direction(to.subtract(from).toVector.normalize()).step(0.1).ignoresBlocks()
      .eachStep(loc => loc.getWorld.spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0.1, dustOptions)).raytrace()

  private def callEvent(attacker: (LivingEntity, Attributable), target: (LivingEntity, Attributable), reaction: ArtReaction): Boolean =
    val event = EntityReactionEvent(attacker._1, target._1, attacker._2, target._2, reaction)
    Bukkit.getPluginManager.callEvent(event)
    event.isCancelled || event.canResist

  private def callReactionEvent(attacker: Attributable, target: Attributable, reaction: ArtReaction): EntityComboReactionEvent =
    val event = EntityComboReactionEvent(target, attacker, reaction)
    Bukkit.getPluginManager.callEvent(event)
    target.reaction = Some(reaction)
    event

  /** Checks and returns the result whether [[target]] can be inflicted with [[reaction]] at this moment in time.
    *
    * '''KNOCKBACK''' and '''BLOWDOWN''' are always available.
    *
    * '''BREAK''' is only available if [[target]] is not currently in a reaction.
    *
    * '''TOPPLE''' is only available if [[target]] is currently in a reaction of '''BREAK'''.
    *
    * '''LAUNCH''' and '''DAZE''' are only available if [[target]] is currently in a reaction of '''TOPPLE'''.
    *
    * '''SMASH''' is only available if [[target]] is currently in a reaction of '''LAUNCH'''. '''BURST''' is only available if [[target]] is
    * currently in a reaction of '''DAZE'''.
    */
  def canInflict(target: Attributable, reaction: ArtReaction): Boolean = reaction match
    case ArtReaction.KNOCKBACK | ArtReaction.BLOWDOWN => true
    case ArtReaction.BREAK                            => target.reaction.isEmpty
    case ArtReaction.TOPPLE                           => target.reaction.contains(ArtReaction.BREAK)
    case ArtReaction.LAUNCH | ArtReaction.DAZE        => target.reaction.contains(ArtReaction.TOPPLE)
    case ArtReaction.SMASH                            => target.reaction.contains(ArtReaction.LAUNCH)
    case ArtReaction.BURST                            => target.reaction.contains(ArtReaction.DAZE)
    case null                                         => false

  /** Inflicts smash on the [[target]] after [[attacker]] used a set of [[arts]].
    */
  def inflictSmash(target: Attributable, attacker: Attributable, arts: Iterable[ArtType]): Unit =
    if !canInflict(target, ArtReaction.SMASH) then return ()

    val entity = Bukkit.getEntity(target.uuid).asInstanceOf[LivingEntity]
    val smashEvent = EntitySmashReactionEvent(entity, attacker, arts)
    Bukkit.getPluginManager.callEvent(smashEvent)

    target.reaction = None
    target.reactionFrames = 0

    Hologram.spawnAround(entity.getEyeLocation, 40, SMASH_TEXT)
    entity.setFallDistance(100)
    entity.setVelocity(Vector(0, -1, 0))

    Events.newSubscription(classOf[EntityDamageEvent]).filter(_.getEntity.getUniqueId == entity.getUniqueId)
      .filter(_.getCause == DamageCause.FALL).expiresAfterInvocations(1).handler(event => {
        event.setCancelled(true)
        if !entity.isDead then
          BattlefieldManager.getAttributable(entity).foreach(attr => CombatManager.damage(attr, smashEvent.finalDamage, "&f&l"))
          entity.getNearbyEntities(5, 5, 5).asScala.filter(_.isInstanceOf[Mob]).map(_.asInstanceOf[Mob])
            .flatMap(BattlefieldManager.getAttributable).foreach(attr => CombatManager.damage(attr, smashEvent.finalShockDamage, "&f&l"))
      }).build(Xenocraft.getInstance)
