package dev.hawu.plugins.xenocraft
package combat

import dev.hawu.plugins.xenocraft.UserMap.user
import dev.hawu.plugins.xenocraft.data.User
import org.bukkit.Bukkit

import java.util.UUID
import scala.collection.mutable

/**
 * Represents a battlefield that has ongoing battles.
 *
 * @param players the players involved
 * @param enemies the entities involved
 */
class Battlefield(
  val players: Party,
  val enemies: mutable.Map[UUID, User] = mutable.Map.empty[UUID, User],
):

  /**
   * The battlefield is considered engaged if one of the party member,
   * no matter anyone has not unsheathed their weapons.
   *
   * @return whether the current battlefield is engaged
   */
  def isEngaged: Boolean =
    players.members.appended(players.leader).map(Bukkit.getOfflinePlayer).map(_.user)
      .map(_.get).exists(_.bladeUnsheathed)
