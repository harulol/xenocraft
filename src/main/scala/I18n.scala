package dev.hawu.plugins.xenocraft

import dev.hawu.plugins.api.i18n.LanguageModule
import org.bukkit.plugin.java.JavaPlugin

/**
 * Initialization module for this plugin.
 */
object I18n:

  private var module: Option[LanguageModule] = None

  def initialize(pl: JavaPlugin): Unit =
    module = Some(LanguageModule(pl, "messages"))
