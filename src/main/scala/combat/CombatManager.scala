package dev.hawu.plugins.xenocraft
package combat

import UserMap.user
import data.{ClassRole, EnemyEntity}
import events.PlayerDealDamageEvent

import org.bukkit.*
import org.bukkit.Particle.{DustOptions, TOTEM, TOWN_AURA}
import org.bukkit.attribute.Attribute
import org.bukkit.entity.{LivingEntity, Mob, Player}
import org.bukkit.metadata.FixedMetadataValue

import java.security.SecureRandom
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters.*

/** The singleton object dedicated to managing the battlefield and spawning holograms in the right place.
 */
object CombatManager:

  private val secureRandom = SecureRandom()
  private val entities = mutable.Map.empty[UUID, EnemyEntity]

  /** Heals the player for the specified health value.
    *
    * @param player
    *   the player
   * @param value
   *    the value
   */
  def heal(player: Player, value: Double): Unit =
    val user = player.user.get
    val oldHp = user.hp
    user.setHp(user.hp + value)
    val change = user.hp - oldHp
    spawnHologramAround(player.getEyeLocation, s"&a${change.intValue}")

  /** Damages the player for the specified damage value.
   *
   * @param player
   * the player
   * @param value
   * the damage
   */
  def damage(player: Player, value: Double, spawn: Boolean = false): Unit =
    val user = player.user.get
    val damage = value min 9999999 max 0
    user.setHp(user.hp - damage)
    if spawn then spawnHologramAround(player.getEyeLocation, s"&c${damage.intValue}")

  private def spawnHologramAround(location: Location, lines: String*): Unit =
    Hologram(location.clone().add(getOffset, getOffset, getOffset), ArrayBuffer.from(lines), 60).spawn()

  private def getOffset: Double =
    val value = secureRandom.nextGaussian() / 2
    val signum = if secureRandom.nextBoolean() then -1 else 1
    signum * value

  def dealDamage(event: PlayerDealDamageEvent): Unit =
    Bukkit.getPluginManager.callEvent(event)
    if !event.isCancelled && !event.isEvaded && event.isHit then event.entity.setHp(event.entity.hp - event.finalDamage)

  /** Makes an enemy and retroeves it.
    *
    * @param entity
    *   the entity
    * @param state
    *   the state
    * @return
    *   the enemy
    */
  def makeEnemy(entity: Mob, state: Int = -1): EnemyEntity =
    if entities.contains(entity.getUniqueId) then return entities(entity.getUniqueId)
    val enemy = if state >= 0 then EnemyEntity(state, entity) else EnemyEntity.apply(entity)
    entities.put(entity.getUniqueId, enemy)
    enemy

  /** Checks if the mob is already made an enemy.
    *
    * @param entity
    *   the entity
    * @return
    *   true if the entity is an enemy, false otherwise
    */
  def isEnemy(entity: Mob): Boolean = entities.contains(entity.getUniqueId)

  /** Clears the enemy from the manager.
    */
  def clearEnemy(entity: Mob): Unit = entities.remove(entity.getUniqueId)

  /** Draws the aggro target line from an entity to a player.
    *
    * @param entity
    *   the entity
    * @param player
    *   the player
    */
  def drawAggroLine(entity: LivingEntity, player: Player): Unit =
    val user = player.user.get
    val dustOptions = if user.cls.exists(_.classRole == ClassRole.DEFENDER) then DustOptions(Color.AQUA, 1) else DustOptions(Color.RED, 1)
    drawLine(entity.getEyeLocation.add(0.0, 1.0, 0.0), player.getEyeLocation.add(0.0, 1.0, 0.0), dustOptions)

  private def drawLine(from: Location, to: Location, dustOptions: DustOptions): Unit =
    val distance = to.distance(from)
    val count = distance.longValue * 3
    val step = distance / count
    val direction = to.clone().subtract(from).toVector.normalize().multiply(step)

    var angle = 0.0
    var trigStep = math.Pi / count
    val current = from.clone()

    while current.distanceSquared(to) >= step * step do
      val elevation = math.sin(angle) * 5
      val loc = current.clone().add(0.0, elevation, 0.0)

      current.getWorld.spawnParticle(Particle.REDSTONE, loc, 1, 0.0, 0.0, 0.0, 0.0, dustOptions)
      current.add(direction)
      angle += trigStep
  end drawLine

end CombatManager
