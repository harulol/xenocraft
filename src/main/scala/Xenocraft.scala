package dev.hawu.plugins.xenocraft

import dev.hawu.plugins.api.commands.CommandRegistry
import dev.hawu.plugins.xenocraft.arts.{ArtType, EnumArt}
import dev.hawu.plugins.xenocraft.classes.{ClassPreset, EnumClass}
import dev.hawu.plugins.xenocraft.commands.LookupCommand
import dev.hawu.plugins.xenocraft.skills.EnumSkill
import org.bukkit.plugin.java.JavaPlugin

/**
 * Represents the plugin entrypoint.
 */
class Xenocraft extends JavaPlugin:

  override def onEnable(): Unit =
    // Initialize all enums and objects.
    EnumClass.initialize(this)
    ClassPreset.initialize(this)
    EnumArt.initialize(this)
    EnumSkill.initialize(this)

    // Commands
    CommandRegistry.register(this, new LookupCommand)
