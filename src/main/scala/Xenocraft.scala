package dev.hawu.plugins.xenocraft

import dev.hawu.plugins.api.commands.CommandRegistry
import dev.hawu.plugins.xenocraft.commands.StatsCommand
import dev.hawu.plugins.xenocraft.data.{Character, User}
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.java.JavaPlugin

import java.io.InputStreamReader

/**
 * Represents the plugin entrypoint.
 */
class Xenocraft extends JavaPlugin:

  override def onEnable(): Unit =
    loadCharacterDescriptions()

    ConfigurationSerialization.registerClass(classOf[User])
    UserMap.initialize(this)

    CommandRegistry.register(this, new StatsCommand)

  private def loadCharacterDescriptions(): Unit =
    val resource = InputStreamReader(getResource("presets.yml"))
    val config = YamlConfiguration.loadConfiguration(resource)
    for char <- Character.values do
      char.description = config.getString(char.toString.toLowerCase)
    resource.close()

  override def onDisable(): Unit =
    UserMap.save(this)
