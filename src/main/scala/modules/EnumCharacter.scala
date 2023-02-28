package dev.hawu.plugins.xenocraft
package modules

import modules.EnumCharacter.dataFile
import modules.Module

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

/** Represents a character that can be chosen to base stats off.
  */
enum EnumCharacter:

  def baseHp: Double = dataFile.get.getDouble(s"${toString.toLowerCase}.hp")
  def baseAttack: Double = dataFile.get.getDouble(s"${toString.toLowerCase}.atk")
  def baseHealing: Double = dataFile.get.getDouble(s"${toString.toLowerCase}.healing")
  def baseDexterity: Double = dataFile.get.getDouble(s"${toString.toLowerCase}.dexterity")
  def baseAgility: Double = dataFile.get.getDouble(s"${toString.toLowerCase}.agility")

  case NOAH, MIO, EUNIE, TAION, LANZ, SENA

/** Object companion for [[EnumCharacter]].
  */
object EnumCharacter extends Module:

  private var dataFile: Option[FileConfiguration] = None

  override def setup(pl: JavaPlugin): Boolean = reload(pl, false)

  override def reload(pl: JavaPlugin, force: Boolean): Boolean =
    dataFile = Some(pl.getDataFolder.resolve("characters.yml").saveResource().loadConfig)
    true

  override def tearDown(pl: JavaPlugin): Unit = dataFile = None
