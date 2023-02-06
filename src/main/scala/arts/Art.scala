package dev.hawu.plugins.xenocraft
package arts

import dev.hawu.plugins.xenocraft.data.{ArtCategory, ArtType, User}
import org.bukkit.entity.Player
import dev.hawu.plugins.xenocraft.data.Directional
import dev.hawu.plugins.xenocraft.data.EnemyEntity
import dev.hawu.plugins.xenocraft.events.PlayerDealDamageEvent
import org.bukkit.Bukkit
import dev.hawu.plugins.api.misc.Raytracing
import scala.jdk.CollectionConverters.*
import org.bukkit.entity.Mob
import dev.hawu.plugins.xenocraft.combat.CombatManager
import dev.hawu.plugins.xenocraft.combat.BattlefieldListener

/** The very abstract implementation of an art.
  *
  * This class only provides a skeleton for arts to implement.
  *
  * @param artType
  *   the art enum type
  */
abstract class Art(val artType: ArtType):

  /** Retrieves an instance of the event.
    *
    * @param player
    *   the player
    * @param direction
    *   the direction
    * @param entity
    *   the entity
    * @param fusion
    *   whether the hit is a fusion hit
    * @param artCritMod
    *   the art crit mod
    * @param artHitChance
    *   the art hit chance
    * @param isPreemptive
    *   whether the hit is preemptive
    * @param isPiercing
    *   whether the hit is piercing
    * @param isAoE
    *   whether the hit is AoE
    * @return
    *   the event
    */
  final def getEvent(
    player: Player,
    entity: EnemyEntity,
    fusion: Boolean = false,
    artCritMod: Double = 0,
    artHitChance: Double = 0,
    isPreemptive: Boolean = false,
    isPiercing: Boolean = false,
    isAoE: Boolean = false,
  ): PlayerDealDamageEvent =
    val event = PlayerDealDamageEvent(
      player,
      direction(player, entity),
      entity,
      artType.category == ArtCategory.PHYSICAL,
      Some(artType),
      fusion,
      artCritMod,
      artHitChance,
      isPreemptive,
      isPiercing,
      isAoE,
    )
    event
  end getEvent

  /** Retrieves the direction.
    *
    * @param player
    *   the player
    * @param enemy
    *   the enemy
    * @return
    *   the direction
    */
  final def direction(player: Player, enemy: EnemyEntity): Directional = BattlefieldListener
    .calculateDirection(player, enemy.entity)

  /** Checks if the event should damage the entity.
    *
    * @param event
    *   the event
    * @return
    *   whether the event should damage the entity
    */
  final def shouldDamage(event: PlayerDealDamageEvent): Boolean =
    Bukkit.getPluginManager().callEvent(event)
    !event.isEvaded && event.isHit && !event.isCancelled

  /** Attempts to retrieve a list of entities in front of the player.
    *
    * @param player
    *   the player to get the entities in front of
    * @param distance
    *   the distance to check
    * @return
    *   the list of entities in front of the player
    */
  final def getEnemiesFront(player: Player, distance: Double = 3.0): Seq[EnemyEntity] =
    val result = Raytracing.startNew().origin(player.getEyeLocation()).direction(player.getEyeLocation().getDirection())
      .distance(distance).step(0.2).raytrace()
    result.getEntities().asScala.filter(_ != null).filterNot(_.isDead()).filter(_.isInstanceOf[Mob])
      .map(_.asInstanceOf[Mob]).filter(CombatManager.isEnemy).map(CombatManager.makeEnemy(_)).toSeq

  /** Gets all enemies around the player.
    *
    * @param player
    *   the player
    * @param distance
    *   the distance to check
    * @return
    *   the list of enemies around the player
    */
  final def getEnemiesAround(player: Player, distance: Double = 3.0): Seq[EnemyEntity] = player
    .getNearbyEntities(distance, distance, distance).asScala.filter(_.isInstanceOf[Mob]).map(_.asInstanceOf[Mob])
    .filter(CombatManager.isEnemy).map(CombatManager.makeEnemy(_)).toSeq

  /** Starts the animation for the art.
    *
    * @param player
    *   the player to start the animation for
    * @param user
    *   the user
    * @param fusion
    *   whether this was used as a fusion art
    */
  def use(player: Player, user: User, fusion: Boolean): Boolean

end Art
