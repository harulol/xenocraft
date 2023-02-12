package dev.hawu.plugins.xenocraft

import UserMap.user
import Xenocraft.instance
import arts.ArtManager
import commands.{ArtCommand, PluginBaseCommand, StatsCommand}
import data.{Character, ClassMemory, ClassType, User}
import gui.*
import listener.*
import managers.*
import skills.SkillManager
import utils.Configuration

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.api.commands.CommandRegistry
import dev.hawu.plugins.api.events.Events
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

import java.io.InputStreamReader
import scala.jdk.CollectionConverters.*

/** Represents the plugin entrypoint.
  */
class Xenocraft extends JavaPlugin:

  private val modules = List(CharactersGUI, ClassesGUI, GemsGUI, MainGUI, ArtsGUI, SkillsGUI, I18n)
  private val initializables = List(AggroManager, EnemyManager, GemsManager, CombatManager, BattlefieldManager, HotbarManager, ChatHologramManager)
  private val serializables = List(classOf[User], classOf[ClassMemory])

  override def onEnable(): Unit =
    instance = this
    modules.foreach(_.initialize(this))
    initializables.foreach(_.setUp(this))
    serializables.foreach(ConfigurationSerialization.registerClass)

    ArtManager.initialize()
    SkillManager.initialize()
    UserMap.initialize(this)
    Configuration.initialize(this)

    CommandRegistry.register(this, new StatsCommand, PluginBaseCommand(this), new ArtCommand)
    Events.registerEvents(this, UserMap, DropsListener)

  override def onDisable(): Unit =
    initializables.foreach(_.tearDown(this))

    Bukkit.getOnlinePlayers.asScala.flatMap(_.user).foreach(_.sheathe())

    UserMap.save(this)
    HandlerList.unregisterAll(this)
    CommandRegistry.unregister(this)
    Tasks.cancelAllTasks(this)

end Xenocraft

/** Object singleton for [[Xenocraft]].
  */
object Xenocraft:

  private var instance: Xenocraft = _

  /** Retrieves the instance of the plugin.
    *
    * @return
    *   the instance
    */
  def getInstance: Xenocraft = instance

  /** Retrieves the list of modules available in the plugin.
    *
    * @return
    *   the modules
    */
  def getModels: List[ModuleHolder] = instance.modules
