package dev.hawu.plugins.xenocraft
package modules

import dev.hawu.plugins.api.i18n.LanguageModule
import org.bukkit.plugin.java.JavaPlugin

import scala.util.Try

/** The internationalization module.
  */
object I18n extends Module:

  private var _messages: Option[LanguageModule] = None

  /** Retrieves the [[LanguageModule]] of the module.
    */
  def messages: Option[LanguageModule] = _messages

  override def setup(pl: JavaPlugin): Boolean =
    _messages = Some(LanguageModule(pl, "messages"))
    true

  override def reload(pl: JavaPlugin, force: Boolean): Boolean =
    Try(_messages.get.saveResources(force)).isSuccess

  override def tearDown(pl: JavaPlugin): Unit =
    _messages = None
    
end I18n
