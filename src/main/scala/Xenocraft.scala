package dev.hawu.plugins.xenocraft

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.api.commands.CommandRegistry
import dev.hawu.plugins.api.events.Events
import dev.hawu.plugins.xenocraft.Xenocraft.instance
import dev.hawu.plugins.xenocraft.combat.{BattlefieldListener, ChatHologramListener}
import dev.hawu.plugins.xenocraft.commands.{ArtCommand, PluginBaseCommand, StatsCommand}
import dev.hawu.plugins.xenocraft.data.{Character, ClassMemory, ClassType, User}
import dev.hawu.plugins.xenocraft.gui.*
import dev.hawu.plugins.xenocraft.utils.Configuration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.Bukkit
import java.io.InputStreamReader
import dev.hawu.plugins.xenocraft.skills.SkillManager
import scala.jdk.CollectionConverters.*
import dev.hawu.plugins.xenocraft.UserMap.user
import dev.hawu.plugins.xenocraft.combat.HotbarManager
import dev.hawu.plugins.xenocraft.combat.DropsListener
import dev.hawu.plugins.xenocraft.combat.BossbarManager
import org.bukkit.event.HandlerList
import dev.hawu.plugins.xenocraft.arts.ArtManager
import dev.hawu.plugins.xenocraft.combat.CooldownsListener

/** Represents the plugin entrypoint.
  */
class Xenocraft extends JavaPlugin:

  private val modules = List(CharactersGUI, ClassesGUI, GemsGUI, MainGUI, ArtsGUI, SkillsGUI, I18n)
  private val serializables = List(classOf[User], classOf[ClassMemory])

  override def onEnable(): Unit =
    instance = this
    modules.foreach(_.initialize(this))
    serializables.foreach(ConfigurationSerialization.registerClass)

    ArtManager.initialize()
    SkillManager.initialize()
    UserMap.initialize(this)
    BattlefieldListener.initialize(this)
    Configuration.initialize(this)

    ChatHologramListener.initialize(this)
    CommandRegistry.register(this, new StatsCommand, PluginBaseCommand(this), new ArtCommand)
    Events.registerEvents(this, ChatHologramListener, UserMap, DropsListener, HotbarManager, CooldownsListener)

  override def onDisable(): Unit =
    Bukkit.getOnlinePlayers.asScala.flatMap(_.user).foreach(_.sheathe())
    Bukkit.getWorlds().asScala.flatMap(_.getEntities().asScala).filter(_ != null).foreach(BossbarManager.clear)

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
