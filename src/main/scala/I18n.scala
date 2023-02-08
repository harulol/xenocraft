package dev.hawu.plugins.xenocraft

import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.collections.tuples.Pair
import dev.hawu.plugins.api.gui.GuiModel
import dev.hawu.plugins.api.i18n.{LanguageModule, Locale}
import dev.hawu.plugins.api.items.ItemStackBuilder
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

import scala.collection.mutable

/** The singleton object for dealing with language modules.
  */
object I18n extends ModuleHolder("messages"):

  private val romanNumerals = mutable.LinkedHashMap(
    1000 -> "M",
    900 -> "CM",
    500 -> "D",
    400 -> "CD",
    100 -> "C",
    90 -> "XC",
    50 -> "L",
    40 -> "XL",
    10 -> "X",
    9 -> "IX",
    5 -> "V",
    4 -> "IV",
    1 -> "I",
  )

  /** Converts a number to roman's counterpart.
    *
    * @param num
    *   the number
    * @return
    *   the roman
    */
  def toRomans(num: Long): String =
    var n = num
    var result = ""
    for (arabic, roman) <- romanNumerals do
      while n >= arabic do
        result += roman
        n -= arabic
    result

  /** Translates the title into a GuiModel.
    *
    * @param size
    *   the size of the model
    * @param key
    *   the key to translate
    * @param replacements
    *   the replacements to translate with
    * @param module
    *   the module to use
    * @param p
    *   the player to get the locale from
    * @return
    *   the GuiModel
    */
  def translateModel(size: Int | InventoryType, key: String, replacements: (String, Any)*)(using
    module: Option[LanguageModule] | LanguageModule,
    p: Player,
  ): GuiModel =
    val mod = matchModule(module)
    val locale = UserAdapter.getAdapter.getUser(p).getLocale
    val title = mod.translate(locale, key, replacements.asLibrary *)

    size match
      case i: Int => GuiModel(i, title)
      case inventoryType: InventoryType => GuiModel(inventoryType, title)
  end translateModel

  private def matchModule(module: Option[LanguageModule] | LanguageModule): LanguageModule = module match
    case option: Option[LanguageModule] => option.get
    case languageModule: LanguageModule => languageModule

  /** Translates an item with the provided key.
   *
   * @param template
   *    the template item, could be a material tuple or a full item stack
   * @param key
   *    the key to translate
   * @param replacements
   *    the replacements to replace with
   * @param module
    *   the module to translate with
    * @param p
    *   the player to get the locale for
    * @return
    *   the item stack
    */
  def translateItem(template: (Material, Int) | ItemStack, key: String, replacements: (String, Any)*)(using
    module: Option[LanguageModule] | LanguageModule,
    p: Player,
  ): ItemStack =
    val mod = matchModule(module)
    val locale = UserAdapter.getAdapter.getUser(p).getLocale
    val item = template match
      case stack: ItemStack              => stack
      case (mat, count): (Material, Int) => ItemStack(mat, count)

    mod.translateItem(locale, item, key, replacements.asLibrary *)
  end translateItem

  /** Converts a scala tuple of 2 to the library's version of pair.
    *
    * @param tuple
    *   the tuple
    * @tparam A
    *   the first value type
    * @tparam B
    *   the second value type
    * @return
    *   the library tuple
    */
  def asLib[A, B](tuple: (A, B)): Pair[A, B] = Pair.of(tuple._1, tuple._2)

  extension [L, R](seq: Iterable[(L, R)]) {

    /** Converts an iterable of tuples into an array of library pairs for splatting in arguments.
      *
      * @return
      *   the array of pairs
      */
    def asLibrary: Array[Pair[L, R]] = seq.map(asLib).toArray

  }

  extension (s: String) {

    /** Translates the [[s]] with the [[args]] provided.
     *
     * @param args
     * the arguments to replace
     * @return
     * the translated string
     */
    def tl(args: (String, Any)*): String = getModule.translate(s, args.map(asLib) *)

    /** Translates the [[s]] with the [[args]] provided in locale [[locale]].
     *
     * @param locale
     * the locale to translate in
     * @param args
     * the arguments to replace
     * @return
     * the translated string
     */
    def tl(locale: Locale, args: (String, Any)*): String = getModule.translate(locale, s, args.map(asLib) *)

  }

  extension (sender: CommandSender) {

    /** Translates a message and then sends it to a player using their preferred locale.
      *
      * @param key
      *   the key to translate
      * @param args
      *   the args to replace
      */
    def tl(key: String, args: (String, Any)*): Unit = sender match
      case player: Player =>
        val extendedUser = UserAdapter.getAdapter.getUser(player)
        val message = getModule.translate(extendedUser.getLocale, key, args.map(asLib) *)
        player.sendMessage(message)
      case _ =>
        val message = getModule.translate(key, args.map(asLib) *)
        sender.sendMessage(message)

  }

end I18n
