package dev.hawu.plugins.xenocraft
package commands

import UserMap.user
import gui.MainGUI

import dev.hawu.plugins.api.commands.*

/** The command handlers for /stats.
 */
class StatsCommand extends CommandRegistrable:

  @Command("stats", senders = Array(SenderType.PLAYER))
  def statsCommand(sender: CommandSource, args: CommandArgument): Unit =
    sender.getPlayer.get().user.foreach(_.sheathe())
    MainGUI.openMain(sender.getPlayer.get())
