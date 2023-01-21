package dev.hawu.plugins.xenocraft
package combat

import java.util.UUID
import scala.collection.mutable

/**
 * Represents a user's party.
 *
 * @param leader  the leader of the party
 * @param members the members
 */
class Party(
  val leader: UUID,
  val members: mutable.ArrayBuffer[UUID] = mutable.ArrayBuffer.empty[UUID],
)
