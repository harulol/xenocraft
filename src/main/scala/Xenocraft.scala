package dev.hawu.plugins.xenocraft

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.api.commands.CommandRegistry
import dev.hawu.plugins.api.events.Events
import dev.hawu.plugins.xenocraft.Xenocraft.instance
import dev.hawu.plugins.xenocraft.combat.{BattlefieldListener, ChatHologramListener}
import dev.hawu.plugins.xenocraft.commands.{ArtCommand, PluginBaseCommand, StatsCommand}
import dev.hawu.plugins.xenocraft.data.{Character, ClassType, User}
import dev.hawu.plugins.xenocraft.gui.StatsGui
import dev.hawu.plugins.xenocraft.utils.Configuration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.java.JavaPlugin

import java.io.InputStreamReader

/**
 * Represents the plugin entrypoint.
 */
class Xenocraft extends JavaPlugin:

  override def onEnable(): Unit =
    instance = this
    Character.initialize(this)
    ClassType.initialize(this)
    I18n.initialize(this)

    ConfigurationSerialization.registerClass(classOf[User])
    StatsGui.initialize(this)
    UserMap.initialize(this)
    BattlefieldListener.initialize(this)
    Configuration.initialize(this)

    ChatHologramListener.initialize(this)
    CommandRegistry.register(this, new StatsCommand, PluginBaseCommand(this), new ArtCommand)
    Events.registerEvents(this, ChatHologramListener, UserMap)

  override def onDisable(): Unit =
    UserMap.save(this)
    I18n.clear()
    CommandRegistry.unregister(this)
    Tasks.cancelAllTasks(this)

/**
 * Object singleton for [[Xenocraft]].
 */
object Xenocraft:

  private var instance: Xenocraft = _

  /**
   * Retrieves the instance of the plugin.
   *
   * @return the instance
   */
  def getInstance: Xenocraft = instance
