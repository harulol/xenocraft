package dev.hawu.plugins.xenocraft
package arts

import data.*
import events.combat.PlayerDealDamageEvent
import managers.{BattlefieldManager, CombatManager, EnemyManager}

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.api.misc.Raytracing
import org.bukkit.Bukkit
import org.bukkit.entity.{LivingEntity, Mob, Player}
import org.bukkit.scheduler.BukkitTask

import scala.jdk.CollectionConverters.*

/** The very abstract implementation of an art.
  *
  * This class only provides a skeleton for arts to implement.
  *
  * @param artType
  *   the art enum type
  */
abstract class Art(val artType: ArtType):

  /** Retrieves the event builder instance.
    */
  final def getEvent(player: Player): PlayerDealDamageEvent.Builder = PlayerDealDamageEvent(player).artType(artType)
    .physical(artType.category == ArtCategory.PHYSICAL)

  /** Schedule a task to deal damage after [[ticks]] with [[f]].
    */
  final def schedule(ticks: Long = 0, f: => Unit): Unit = Tasks.run(_ => f).delay(ticks).plugin(Xenocraft.getInstance).run()

  /** Attempts to deal damage to an entity.
    */
  final def dealDamage(event: PlayerDealDamageEvent): Boolean =
    Bukkit.getPluginManager.callEvent(event)
    if event.isHit && !event.isEvaded && !event.isCancelled then 
      CombatManager.damage(event.entity, event.finalDamage)
      true
    else false  

  /** Attempts to retrieve a list of entities in front of the source.
    */
  final def getEnemiesFront(source: LivingEntity, distance: Double = 4.0): Seq[EnemyEntity] =
    val result = Raytracing.startNew().origin(source.getEyeLocation).direction(source.getEyeLocation.getDirection).distance(distance)
      .step(0.2).raytrace()
    result.getEntities.asScala.filter(_ != null).filterNot(_.isDead()).filter(_.isInstanceOf[Mob]).map(_.asInstanceOf[Mob])
      .filter(EnemyManager.isMarked).flatMap(EnemyManager.getEnemy).toSeq

  /** Gets all enemies around the player.
    */
  final def getEnemiesAround(source: LivingEntity, distance: Double = 4.0): Seq[EnemyEntity] = source
    .getNearbyEntities(distance, distance, distance).asScala.filter(_.isInstanceOf[Mob]).map(_.asInstanceOf[Mob])
    .filter(EnemyManager.isMarked).flatMap(EnemyManager.getEnemy).toSeq

  /** Generates a list of damage dealing events as so multi hits are independent instead of being reliant on the first hit.
    */
  final def generateEvents(count: Int, f: => PlayerDealDamageEvent): Array[PlayerDealDamageEvent] = Array.fill(count)(f)

  /** Schedules to set the [[Attributable.isInAnimation]] to false after [[duration]].
    */
  final def scheduleAnimation(duration: Long, attr: Attributable): Unit =
    attr.isInAnimation = true
    Tasks.run(_ => attr.isInAnimation = false).delay(duration).plugin(Xenocraft.getInstance).run()

  /** Starts the animation for the art.
    *
    * @param player
    *   the player to start the animation for
    * @param user
    *   the user version of [[player]]
    * @param fusion
    *   whether this was used as a fusion art
    * @param master
    *   whether this was a master art in a fusion art
    */
  def use(player: Player, user: User, fusion: Boolean, master: Boolean): Boolean
