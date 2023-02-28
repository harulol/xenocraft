package dev.hawu.plugins

import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.collections.tuples.Pair
import dev.hawu.plugins.api.commands.CommandSource
import dev.hawu.plugins.api.i18n.Locale
import dev.hawu.plugins.api.misc.ConsoleLogger
import dev.hawu.plugins.api.user.ExtendedUser
import dev.hawu.plugins.xenocraft.modules.I18n
import org.bukkit.{Bukkit, Location, OfflinePlayer}
import org.bukkit.command.{CommandSender, ConsoleCommandSender}
import org.bukkit.configuration.file.{FileConfiguration, YamlConfiguration}
import org.bukkit.entity.Player
import org.bukkit.util.Vector

import java.io.File
import java.util
import scala.annotation.targetName
import scala.collection.{mutable, IterableOps, MapOps, SeqOps, SetOps}
import scala.jdk.CollectionConverters.*
import scala.util.Try

/** Cool stuff to simplify a lot of stuff for Scala.
  */
package object xenocraft:

  given Conversion[OfflinePlayer, Player] = _.getPlayer
  given Conversion[Location, Vector] = _.toVector

  @targetName("given_Conversion_Set_UtilSet")
  given Conversion[Set[?], util.Set[?]] = _.asJava

  @targetName("given_Conversion_Map_UtilMap")
  given Conversion[Map[?, ?], util.Map[?, ?]] = _.asJava

  @targetName("given_Conversion_List_UtilList")
  given Conversion[List[?], util.List[?]] = _.asJava

  @targetName("given_Conversion_UtilSet_Set")
  given Conversion[util.Set[?], Set[?]] = _.asScala.toSet

  @targetName("given_Conversion_UtilList_List")
  given Conversion[util.Map[?, ?], Map[?, ?]] = _.asScala.toMap

  extension [T](any: T) {

    /** Mimics Kotlin's ?: elvis operator. Returns [[other]] if [[any]] is null.
      */
    @targetName("orElse")
    def ?:(other: T): T = if any == null then other else any

    /** Applies a function [[f]] on the receiver, and retrieves the same receiver.
      */
    def apply(f: T => Unit): T =
      f(any)
      any

    /** Applies a function [[f]] on the receiver, but retrieves the result of the [[f]].
      */
    def run[R](f: T => R): R = if any != null then f(any) else null.asInstanceOf[R]

    /** Wraps the current instance in an [[Option]].
      */
    def wrap: Option[T] = Option(any)

    /** Casts this instance to an instance of type [[R]].
      */
    infix def as[R]: R = any.asInstanceOf[R]

    /** Returns true if [[any]] is contained in the given [[collection]].
      */
    infix def in(collection: Iterable[?]): Boolean = collection match
      case seq: Seq[?]         => seq.contains(any)
      case set: Set[?]         => set.contains(any)
      case map: Map[? >: T, ?] => map.contains(any)
      case _                   => collection.exists(_ == any)

    /** Safely casts this instance to an instance of [[R]].
      */
    def safeCast[R]: Try[R] = Try(any.as[R])

  }

  extension [A, B](pair: Pair[A, B]) {

    /** Converts a [[Pair]] to a Scala tuple.
      */
    def toTuple: (A, B) =
      (pair.getFirstOption.orElse(null.as[A]), pair.getSecondOption.orElse(null.as[B]))

  }

  extension [A, B](tuple: (A, B)) {

    /** Converts a [[Tuple]] to a [[Pair]].
      */
    def toPair: Pair[A, B] = Pair.of(tuple._1, tuple._2)

  }

  extension (s: String) {

    /** Translates into a message with [[s]] as a key and [[args]] for replacements.
      */
    def tl(args: (String, Any)*): String = I18n.messages.get.translate(s, args.map(_.toPair)*)

    /** Translates into a message with [[s]] as a key and [[args]] for replacements, localized to
      * [[locale]].
      */
    def tl(locale: Locale, args: (String, Any)*): String = I18n.messages.get
      .translate(locale, s, args.map(_.toPair)*)

    /** Translates a message and sends that message to the console.
      */
    def log(args: (String, Any)*): Unit = Bukkit.getConsoleSender.sendMessage(s.tl(args*))

  }

  extension (f: File) {

    /** Safely creates a file if it doesn't exist.
      */
    def safelyCreate(): Boolean = if !f.exists() then f.createNewFile() else true

    /** Resolves one step into the folder and returns the children with the provided path.
      */
    def resolve(children: String): File = File(f, children)

    /** Resolves a file in the same directory.
      */
    def sibling(name: String): File = File(f.getParentFile, name)

    /** Saves the resource and returns itself.
      */
    def saveResource(resourcePath: String = f.getName, force: Boolean = false): File =
      if !f.exists() || force then Xenocraft.getInstance.saveResource(resourcePath, true)
      f

    /** Loads a config from the provided file.
      */
    def loadConfig: FileConfiguration = YamlConfiguration.loadConfiguration(f)

  }

  extension (p: OfflinePlayer) {

    /** Returns the extended user version of [[OfflinePlayer]].
      */
    def extendedUser: ExtendedUser = UserAdapter.getAdapter.getUser(p)

  }

  extension (sender: CommandSender) {

    /** Retrieves locale of a [[sender]] if it's a [[Player]], else it's always [[Locale.en_US]].
      */
    def locale: Locale = sender match
      case p: Player => p.extendedUser.getLocale
      case _         => Locale.en_US

    /** Sends a translated message to a sender with the their locale.
      */
    def tl(key: String, args: (String, Any)*): Unit = sender
      .sendMessage(key.tl(sender.locale, args*))

  }

  extension (sender: CommandSource) {

    /** Sends a translated message to a sender with the their locale.
      */
    def tl(key: String, args: (String, Any)*): Unit = sender
      .sendMessage(key.tl(sender.getLocale, args*))

  }

end xenocraft
