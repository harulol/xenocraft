package dev.hawu.plugins.xenocraft
package combat

import dev.hawu.plugins.xenocraft.data.User

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
)
