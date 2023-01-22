package dev.hawu.plugins.xenocraft
package combat

import dev.hawu.plugins.xenocraft.UserMap.user
import org.bukkit.Location
import org.bukkit.entity.Player

import java.security.SecureRandom
import scala.collection.mutable.ArrayBuffer

/**
 * The singleton object dedicated to managing the battlefield
 * and spawning holograms in the right place.
 */
object CombatManager:

  private val secureRandom = SecureRandom()

  /**
   * Heals the player for the specified health value.
   *
   * @param player the player
   * @param value  the value
   */
  def heal(player: Player, value: Double): Unit =
    val user = player.user.get
    user.setHp(user.hp + value)
    spawnHologramAround(player.getEyeLocation, s"&a${value.intValue}")

  private def spawnHologramAround(location: Location, lines: String*): Unit =
    Hologram(location.clone().add(getOffset, getOffset, getOffset), ArrayBuffer.from(lines), 60).spawn()

  private def getOffset: Double =
    val value = secureRandom.nextGaussian() / 2
    val signum = if secureRandom.nextBoolean() then -1 else 1
    signum * value

  /**
   * Damages the player for the specified damage value.
   *
   * @param player the player
   * @param value  the damage
   */
  def damage(player: Player, value: Double): Unit =
    val user = player.user.get
    user.setHp(user.hp - value)
    spawnHologramAround(player.getEyeLocation, s"&c${value.intValue}")
