package dev.hawu.plugins.xenocraft
package commands

import dev.hawu.plugins.api.commands.*
import dev.hawu.plugins.xenocraft.UserMap.user
import dev.hawu.plugins.xenocraft.arts.swordfighter.GroundBeat

class ArtCommand extends CommandRegistrable:

  @Command("art", senders = Array(SenderType.PLAYER))
  def onCommand(source: CommandSource, args: CommandArgument): Unit =
    val player = source.getPlayer.get()
    GroundBeat.startAnimation(player, player.user.get)