package dev.hawu.plugins.xenocraft
package data

import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import org.bukkit.{Bukkit, OfflinePlayer}

import java.util
import java.util.UUID
import scala.annotation.tailrec
import scala.jdk.CollectionConverters.*
import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

/**
 * Represents a player's data.
 *
 * @param uuid       The UUID of the player.
 * @param cls        The class of the player.
 * @param char       The character preset of the player.
 * @param masterArts The master arts.
 * @param arts       The class arts.
 * @param gems       The equipped gems.
 * @param talentArt  The equipped talent art.
 */
case class User(
  uuid: UUID,
  var cls: Option[ClassType] = None,
  var char: Option[Character] = None,
  masterArts: Array[ArtType] = Array.ofDim(3),
  arts: Array[ArtType] = Array.ofDim(3),
  gems: Array[GemType] = Array.ofDim(3),
  var talentArt: Option[ArtType] = None,
) extends ConfigurationSerializable:

  /**
   * Attempts to retrieve the player instance from the user.
   *
   * @return the player instance
   */
  def player: Option[Player] = Option(Bukkit.getPlayer(uuid))

  /**
   * Retrieves the offline player instance.
   *
   * @return the offline player
   */
  def offlinePlayer: OfflinePlayer = Bukkit.getOfflinePlayer(uuid)

  override def serialize(): util.Map[String, AnyRef] = Map(
    "uuid" -> uuid.toString,
    "class" -> cls.map(_.toString).orNull,
    "character" -> char.map(_.toString).orNull,
    "masterArts" -> masterArts.map(_.toString).toList.asJava,
    "arts" -> arts.map(_.toString).toList.asJava,
    "gems" -> gems.map(_.toString).toList.asJava,
    "talentArt" -> talentArt.map(_.toString).orNull,
  ).asJava

/**
 * Companion object for [[User]].
 */
object User:

  def deserialize(map: util.Map[String, Any]): User =
    given util.Map[String, Any] = map

    val uuid = UUID.fromString(map.get("uuid").toString)
    val cls = tryGetting("class", ClassType.valueOf)
    val char = tryGetting("character", Character.valueOf)
    val talentArt = tryGetting("talentArt", ArtType.valueOf)

    val masterArts = tryGettingArray("masterArts", ArtType.valueOf).asInstanceOf[Array[ArtType]]
    val arts = tryGettingArray("arts", ArtType.valueOf).asInstanceOf[Array[ArtType]]
    val gems = tryGettingArray("gems", GemType.valueOf).asInstanceOf[Array[GemType]]

    User(uuid, cls, char, masterArts, arts, gems, talentArt)

  private def tryGetting[T](key: String, f: String => T)(using map: util.Map[String, Any]): Option[T] =
    val result = Option(map.get(key)).map(_.toString)
    if result.isEmpty then return None
    Try(f(result.get)) match
      case Success(value) => Some(value)
      case Failure(_) => None

  private def tryGettingArray[T <: Any](key: String, f: String => T, size: Int = 3)(using map: util.Map[String, Any]): Array[Any] =
    map.get(key).asInstanceOf[util.List[String]].asScala
      .take(size)
      .map { s => if s == null then null else f.apply(s) }
      .toArray
