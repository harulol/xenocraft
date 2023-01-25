package dev.hawu.plugins.xenocraft
package commands

import dev.hawu.plugins.api.commands.*
import dev.hawu.plugins.xenocraft.UserMap.user
import dev.hawu.plugins.xenocraft.gui.StatsGui

/**
 * The command handlers for /stats.
 */
class StatsCommand extends CommandRegistrable:

  @Command("stats", senders = Array(SenderType.PLAYER))
  def statsCommand(sender: CommandSource, args: CommandArgument): Unit =
    sender.getPlayer.get().user.foreach(_.sheathe())
    StatsGui.mainMenu(sender.getPlayer.get())
