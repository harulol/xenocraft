package dev.hawu.plugins.xenocraft
package commands

import dev.hawu.plugins.api.commands.*
import dev.hawu.plugins.xenocraft.I18n.*
import dev.hawu.plugins.xenocraft.data.{Character, ClassType}
import dev.hawu.plugins.xenocraft.gui.{CharactersGUI, MainGUI}
import org.bukkit.plugin.java.JavaPlugin

import java.util
import scala.jdk.CollectionConverters.*

/** The handlers for the base commands of this plugin Xenocraft.
  */
class PluginBaseCommand(private val plugin: JavaPlugin) extends CommandRegistrable:

  @Command("xenocraft") @CommandPermission(Array("xenocraft.base"))
  def xenocraftCommand(sender: CommandSource, args: CommandArgument): Unit = sender.getBase
    .tl("current-version", "version" -> plugin.getDescription.getVersion)

  @TabExecute("xenocraft") @CommandPermission(Array("xenocraft.base"))
  def xenocraftTab(sender: CommandSource, args: CommandArgument): util.List[String] =
    if args.size() == 1 then List("reload").asJava else List.empty[String].asJava

  @Command("xenocraft reload") @CommandPermission(Array("xenocraft.reload"))
  def reloadCommand(sender: CommandSource, args: CommandArgument): Unit =
    val cli = CommandLine().withFlag("-f")
    val force = args.parse(cli).getSecond.containsKey("-f")

    reload(force)
    if force then sender.getBase.tl("overwritten-messages") else sender.getBase.tl("reloaded-messages")

  private def reload(force: Boolean): Unit = Xenocraft.getModels.foreach(_.reload(force))

  @TabExecute("xenocraft reload") @CommandPermission(Array("xenocraft.reload"))
  def reloadTab(sender: CommandSource, args: CommandArgument): util.List[String] =
    if args.size() == 1 && !args.getUnderlyingList.contains("-f") then List("-f").asJava else List.empty[String].asJava

end PluginBaseCommand
