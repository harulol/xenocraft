package dev.hawu.plugins.xenocraft
package combat

import dev.hawu.plugins.xenocraft.UserMap.user
import dev.hawu.plugins.xenocraft.data.ClassRole
import org.bukkit.Particle.{DustOptions, TOTEM, TOWN_AURA}
import org.bukkit.entity.{LivingEntity, Player}
import org.bukkit.{Color, Location, Particle}

import java.security.SecureRandom
import scala.collection.mutable.ArrayBuffer

/** The singleton object dedicated to managing the battlefield and spawning holograms in the right place.
  */
object CombatManager:

  private val secureRandom = SecureRandom()

  /** Heals the player for the specified health value.
    *
    * @param player
    *   the player
    * @param value
    *   the value
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
    *   the player
    * @param value
    *   the damage
    */
  def damage(player: Player, value: Double): Unit =
    val user = player.user.get
    val damage = value min 9999999 max 0
    user.setHp(user.hp - damage)
    spawnHologramAround(player.getEyeLocation, s"&c${damage.intValue}")

  def damageEntity(entity: LivingEntity, value: Double): Unit =
    entity.setHealth((entity.getHealth - value) max 0)
    spawnHologramAround(entity.getLocation.add(0, entity.getEyeHeight, 0), s"&c${value.intValue}")

  private def spawnHologramAround(location: Location, lines: String*): Unit =
    Hologram(location.clone().add(getOffset, getOffset, getOffset), ArrayBuffer.from(lines), 60).spawn()

  private def getOffset: Double =
    val value = secureRandom.nextGaussian() / 2
    val signum = if secureRandom.nextBoolean() then -1 else 1
    signum * value

  /** Draws the aggro target line from an entity to a player.
    *
    * @param entity
    *   the entity
    * @param player
    *   the player
    */
  def drawAggroLine(entity: LivingEntity, player: Player): Unit =
    val user = player.user.get
    val dustOptions =
      if user.cls.exists(_.classRole == ClassRole.DEFENDER) then DustOptions(Color.AQUA, 1)
      else DustOptions(Color.RED, 1)
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
