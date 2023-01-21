package dev.hawu.plugins.xenocraft
package commands

import dev.hawu.plugins.api.commands.*
import dev.hawu.plugins.xenocraft.I18n.*
import dev.hawu.plugins.xenocraft.combat.PartyManager
import org.bukkit.entity.Player
import org.bukkit.{Bukkit, OfflinePlayer}

import java.util
import scala.jdk.CollectionConverters.*

/**
 * The handlers for the /party commands.
 */
class PartyCommand extends CommandRegistrable:

  @Command("party", senders = Array(SenderType.PLAYER))
  def partyCommand(source: CommandSource, args: CommandArgument): Unit =
    showPartyList(source.getPlayer.get)

  private def showPartyList(player: Player): Unit =
    val partyOption = PartyManager.getParty(player.getUniqueId)
    if partyOption.isEmpty then
      player.tl("not-in-party")
    else
      val party = partyOption.get
      val leader = statusText(Bukkit.getOfflinePlayer(party.leader))
      val members = party.members.map(Bukkit.getOfflinePlayer).map(statusText).mkString("&7, ")
      val memberString = if members.isEmpty then "&cNone" else members
      player.tl("party-list", "leader" -> leader, "members" -> memberString)

  private def statusText(player: OfflinePlayer): String =
    s"&7${player.getName} ${if player.isOnline then "&a" else "&c"}•"

  @TabExecute("party", senders = Array(SenderType.PLAYER))
  def partyTab(source: CommandSource, args: CommandArgument): util.List[String] =
    if args.size() == 1 then
      List("list", "create").asJava
    else List.empty[String].asJava

  @Command("party list", senders = Array(SenderType.PLAYER))
  def listCommand(source: CommandSource, args: CommandArgument): Unit =
    showPartyList(source.getPlayer.get)

  @Command("party create", senders = Array(SenderType.PLAYER))
  def createCommand(source: CommandSource, args: CommandArgument): Unit =
    createParty(source.getPlayer.get)

  private def createParty(player: Player): Unit =
    if PartyManager.isInParty(player.getUniqueId) then
      player.tl("already-in-party")
    else
      PartyManager.createParty(player.getUniqueId)
      player.tl("party-created")

  @Command("party disband", senders = Array(SenderType.PLAYER))
  def disbandCommand(source: CommandSource, args: CommandArgument): Unit =
    val player = source.getPlayer.get
    if !PartyManager.isPartyLeader(player.getUniqueId) then
      player.tl("not-a-leader")
    else
      PartyManager.disbandParty(player.getUniqueId)
      player.tl("party-disbanded")
