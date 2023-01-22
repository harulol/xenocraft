package dev.hawu.plugins.xenocraft

import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.collections.tuples.Pair
import dev.hawu.plugins.api.i18n.{LanguageModule, Locale}
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * The singleton object for dealing with language modules.
 */
object I18n:

  private var messages: Option[LanguageModule] = None
  private var plugin: Option[JavaPlugin] = None

  /**
   * Initializes the I18n object.
   *
   * @param pl the plugin
   */
  def initialize(pl: JavaPlugin): Unit =
    plugin = Some(pl)
    messages = Some(LanguageModule(pl, "messages"))

  /**
   * Reloads all messages by reinitializing the instance.
   */
  def reload(): Unit =
    messages.foreach(_.saveResources(false))

  /**
   * Attempts to overwrite files by saving resources again
   * and reloading.
   */
  def overwrite(): Unit =
    messages.foreach(_.saveResources(true))

  /**
   * Clears all references in this singleton object.
   */
  def clear(): Unit =
    plugin = None
    messages = None

  private def convertToLibrary[A, B](tuple: (A, B)): Pair[A, B]
  = Pair.of(tuple._1, tuple._2)

  extension (s: String) {

    /**
     * Translates the [[s]] with the [[args]] provided.
     *
     * @param args the arguments to replace
     * @return the translated string
     */
    def tl(args: (String, Any)*): String =
      messages.get.translate(s, args.map(convertToLibrary): _*)

    /**
     * Translates the [[s]] with the [[args]] provided in
     * locale [[locale]].
     *
     * @param locale the locale to translate in
     * @param args   the arguments to replace
     * @return the translated string
     */
    def tl(locale: Locale, args: (String, Any)*): String =
      messages.get.translate(locale, s, args.map(convertToLibrary): _*)

  }

  extension (sender: CommandSender) {

    /**
     * Translates a message and then sends it to a player using
     * their preferred locale.
     *
     * @param key  the key to translate
     * @param args the args to replace
     */
    def tl(key: String, args: (String, Any)*): Unit =
      sender match
        case player: Player =>
          val extendedUser = UserAdapter.getAdapter.getUser(player)
          val message = messages.get.translate(extendedUser.getLocale, key, args.map(convertToLibrary): _*)
          player.sendMessage(message)
        case _ =>
          val message = messages.get.translate(key, args.map(convertToLibrary): _*)
          sender.sendMessage(message)

  }
