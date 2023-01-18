package dev.hawu.plugins.xenocraft

import dev.hawu.plugins.xenocraft.classes.{ClassPreset, ClassType}
import org.bukkit.plugin.java.JavaPlugin

/**
 * Represents the plugin entrypoint.
 */
class Xenocraft extends JavaPlugin:

  override def onEnable(): Unit =
    ClassType.initialize(this)
    ClassPreset.initialize(this)
