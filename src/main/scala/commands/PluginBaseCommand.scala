package dev.hawu.plugins.xenocraft
package commands

import dev.hawu.plugins.api.commands.*
import dev.hawu.plugins.xenocraft.I18n.*
import org.bukkit.plugin.java.JavaPlugin

/**
 * The handlers for the base commands of this plugin
 * Xenocraft.
 */
class PluginBaseCommand(private val plugin: JavaPlugin) extends CommandRegistrable:

  @Command("xenocraft")
  @CommandPermission(Array("xenocraft.base"))
  def xenocraftCommand(sender: CommandSource, args: CommandArgument): Unit =
    sender.getBase.tl("current-version", "version" -> plugin.getDescription.getVersion)

  @Command("xenocraft reload")
  @CommandPermission(Array("xenocraft.reload"))
  def reloadCommand(sender: CommandSource, args: CommandArgument): Unit =
    I18n.reload()
    sender.getBase.tl("reloaded")
