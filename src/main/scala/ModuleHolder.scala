package dev.hawu.plugins.xenocraft

import dev.hawu.plugins.api.i18n.LanguageModule
import org.bukkit.plugin.java.JavaPlugin

/** Represents an object that holds a language module.
  */
trait ModuleHolder(val name: String):

  protected var module: Option[LanguageModule] = None

  /** Initializes the module.
   *
   * @param pl
   * the plugin to initialize with
   */
  def initialize(pl: JavaPlugin): Unit = module = Some(LanguageModule(pl, name))

  /** Retrieves the module it holds in an option.
    *
   * @return
   * the module
   */
  def getModule: LanguageModule = module.get

  /** Reloads the module it holds.
   *
   * @param force
   * whether to overwrite
   */
  def reload(force: Boolean = false): Unit = module.foreach(_.saveResources(force))

end ModuleHolder
