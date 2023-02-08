package dev.hawu.plugins.xenocraft
package commands

import UserMap.user

import dev.hawu.plugins.api.commands.*

class ArtCommand extends CommandRegistrable:

  @Command("art", senders = Array(SenderType.PLAYER))
  def onCommand(source: CommandSource, args: CommandArgument): Unit =
    val player = source.getPlayer.get()
