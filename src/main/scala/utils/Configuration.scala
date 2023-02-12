package dev.hawu.plugins.xenocraft
package utils

import data.ArtFusionBonus

import org.bukkit.plugin.java.JavaPlugin

/** Represents the configuration file programmatically.
  */
object Configuration:

  private var plugin: Option[JavaPlugin] = None

  /** Initializes the configuration singleton object
    *
    * @param pl
    *   the plugin
    */
  def initialize(pl: JavaPlugin): Unit =
    plugin = Some(pl)
    plugin.get.saveDefaultConfig()

  /** Retrieves the difficulty coerced in a range.
    *
    * @return
    *   the difficulty
    */
  def difficulty: Int = plugin.get.getConfig.getInt("difficulty", 2) min 3 max 1

  /** Retrieves the fusion bonus.
    */
  def fusionBonus(bonus: ArtFusionBonus): Double = bonus match
    case ArtFusionBonus.DAMAGE   => plugin.get.getConfig.getDouble("fusion-bonus.damage", 1.5) max 1.0
    case ArtFusionBonus.RANGE    => plugin.get.getConfig.getDouble("fusion-bonus.range", 1.5) max 1.0
    case ArtFusionBonus.AGGRO    => plugin.get.getConfig.getDouble("fusion-bonus.aggro", 1.5) max 1.0
    case ArtFusionBonus.HEALING  => plugin.get.getConfig.getDouble("fusion-bonus.healing", 1.25) max 1.0
    case ArtFusionBonus.DURATION => plugin.get.getConfig.getDouble("fusion-bonus.duration", 2.0) max 1.0
    case ArtFusionBonus.BLOCK    => plugin.get.getConfig.getDouble("fusion-bonus.block", 1.5) max 1.0
    case _                       => 1.0

end Configuration
