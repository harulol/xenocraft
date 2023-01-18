package dev.hawu.plugins.xenocraft
package classes

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin

import java.io.InputStreamReader

/**
 * The preset that comes with characters. You will utilize
 * their base stats for all classes. Each character has a
 * specialty in a certain stat.
 */
enum ClassPreset:

  private var _description: Option[String] = None

  /**
   * Gets the full fleshed out description
   * of the character preset.
   *
   * @return the description
   */
  def description = _description.get

  case NOAH, MIO, EUNIE, TAION, LANZ, SENA

/**
 * The companion object of the [[ClassPreset]] enum.
 */
object ClassPreset:

  /**
   * Attempts to initialize with the [[pl]].
   *
   * @param pl the plugin
   */
  def initialize(pl: JavaPlugin): Unit =
    val resource = InputStreamReader(pl.getResource("presets.yml"))
    val config = YamlConfiguration.loadConfiguration(resource)
    for preset <- ClassPreset.values do
      preset._description = Option(config.getString(preset.toString.toLowerCase))
  end initialize
