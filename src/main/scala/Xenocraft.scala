package dev.hawu.plugins.xenocraft

import dev.hawu.plugins.api.commands.CommandRegistry
import dev.hawu.plugins.xenocraft.commands.{PartyCommand, PluginBaseCommand, StatsCommand}
import dev.hawu.plugins.xenocraft.data.{Character, ClassType, User}
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
    loadClassWielders()

    ConfigurationSerialization.registerClass(classOf[User])
    I18n.initialize(this)
    UserMap.initialize(this)

    CommandRegistry.register(this, new StatsCommand, new PartyCommand, PluginBaseCommand(this))

  private def loadCharacterDescriptions(): Unit =
    val resource = InputStreamReader(getResource("presets.yml"))
    val config = YamlConfiguration.loadConfiguration(resource)
    for char <- Character.values do
      char.description = config.getString(char.toString.toLowerCase)
    resource.close()

  private def loadClassWielders(): Unit =
    val resource = InputStreamReader(getResource("classes.yml"))
    val config = YamlConfiguration.loadConfiguration(resource)
    for cls <- ClassType.values do
      val kebabCase = if !cls.isSoulhacker then cls.toString.toLowerCase.replace('_', '-') else "soulhacker"
      cls.wielderName = config.getString(s"$kebabCase.wielder-name")
      cls.wielderTitle = config.getString(s"$kebabCase.wielder-title")
    resource.close()

  override def onDisable(): Unit =
    UserMap.save(this)
    I18n.clear()
    CommandRegistry.unregister(this)
