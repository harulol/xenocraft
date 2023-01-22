package dev.hawu.plugins.xenocraft
package utils

import org.bukkit.plugin.java.JavaPlugin

/**
 * Represents the configuration file programmatically.
 */
object Configuration:

  private var plugin: Option[JavaPlugin] = None

  /**
   * Initializes the configuration singleton object
   *
   * @param pl the plugin
   */
  def initialize(pl: JavaPlugin): Unit =
    plugin = Some(pl)
    plugin.get.saveDefaultConfig()

  /**
   * Retrieves the difficulty coerced in a range.
   *
   * @return the difficulty
   */
  def difficulty: Int =
    plugin.get.getConfig.getInt("difficulty", 2) min 3 max 1
