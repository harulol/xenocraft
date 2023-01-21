package dev.hawu.plugins.xenocraft
package combat

import java.util.UUID
import scala.collection.mutable

/**
 * The singleton manager for all parties.
 */
object PartyManager:

  private val parties = mutable.Map.empty[UUID, Party]

  /**
   * Checks if the user is in any party.
   *
   * @param uuid the user
   * @return whether they are in any party
   */
  def isInParty(uuid: UUID): Boolean = isPartyLeader(uuid) || isPartyMember(uuid)

  /**
   * Checks if a user is a leader of any party.
   *
   * @param uuid the uuid
   * @return whether they are a leader
   */
  def isPartyLeader(uuid: UUID): Boolean = parties.contains(uuid)

  /**
   * Checks if a user is a member of any party.
   *
   * @param uuid the uuid
   * @return whether they are in a party
   */
  def isPartyMember(uuid: UUID): Boolean = parties.values.flatMap(_.members).exists(_ == uuid)

  /**
   * Creates a party with the leader
   * provided.
   *
   * This does not provide any checks whether
   * this leader should have a party. Please
   * do so yourself with [[isInParty]], etc.
   *
   * @param uuid the leader
   * @return the party
   */
  def createParty(uuid: UUID): Party =
    val party = Party(uuid)
    parties += uuid -> party
    party

  /**
   * Attempts to retrieve the party of a user, whether
   * they are a leader or a member.
   *
   * @param uuid the user
   * @return the party option
   */
  def getParty(uuid: UUID): Option[Party] =
    val asLeader = parties.get(uuid)
    val asMember = parties.values.find(_.members.contains(uuid))
    if asLeader.isEmpty then asMember else asLeader

  /**
   * Removes the party that [[uuid]] owns.
   *
   * @param uuid the leader
   */
  def disbandParty(uuid: UUID): Unit =
    parties.remove(uuid)
