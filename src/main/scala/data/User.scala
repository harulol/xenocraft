package dev.hawu.plugins.xenocraft
package data

import dev.hawu.plugins.xenocraft.utils.Formulas
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.{Bukkit, Effect, EntityEffect, OfflinePlayer}

import java.util
import java.util.UUID
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
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
  private val _uuid: UUID,
  var cls: Option[ClassType] = None,
  var weapon: Option[WeaponType] = None,
  var char: Option[Character] = None,
  masterArts: Array[ArtType] = Array.ofDim(3),
  arts: Array[ArtType] = Array.ofDim(3),
  gems: Array[GemType] = Array.ofDim(3),
  var talentArt: Option[ArtType] = None,
) extends ConfigurationSerializable with Attributable(_uuid):

  var bladeUnsheathed = false
  private val inventory = mutable.Map.empty[Int, ItemStack]

  /**
   * Unsheathe the blade.
   */
  def unsheathe(): Unit =
    if bladeUnsheathed then return ()
    bladeUnsheathed = true
    inventory.clear()
    player.foreach(p => {
      p.getInventory.getContents.zipWithIndex.foreach((item, index) => inventory += index -> item)
    })

  /**
   * Sheathes the blade back and disables combat.
   */
  def sheathe(): Unit =
    if !bladeUnsheathed then return ()
    bladeUnsheathed = false
    player.foreach(p => {
      inventory.foreach((index, item) => p.getInventory.setItem(index, item))
    })
    inventory.clear()

  /**
   * Sets the HP value of the player.
   *
   * @param value the HP value
   */
  override def setHp(value: Double): Unit =
    val maxHealth = maxHp
    _hp = value min maxHealth max 0
    if maxHealth != 0 then
      val percentage = _hp / maxHealth
      player.foreach(p => {
        val value = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue * percentage
        if value <= 0 then
          setHp(maxHp)
          p.sendTitle("&cYou died!", "&7Guess Ouroboros sucks still...", 20, 100, 20)
          p.teleport(p.getWorld.getSpawnLocation)
          p.playEffect(EntityEffect.TOTEM_RESURRECT)
        else if value < p.getHealth then
          p.setHealth(value)
          p.playEffect(EntityEffect.HURT)
        else p.setHealth(value)
      })

  override def maxHp: Double = Formulas.calculateHp(this)

  /**
   * Attempts to retrieve the player instance from the user.
   *
   * @return the player instance
   */
  def player: Option[Player] = Option(Bukkit.getPlayer(uuid))

  override def attack: Double = Formulas.calculateAttack(this)

  override def healing: Double = Formulas.calculateHealingPower(this)

  override def dexterity: Double = Formulas.calculateDexterity(this)

  override def agility: Double = Formulas.calculateAgility(this)

  override def critRate: Double = Formulas.calculateDisplayCritRate(this)

  override def blockRate: Double = Formulas.calculateDisplayBlockRate(this)

  override def physicalDef: Double = Formulas.calculateDisplayPhysDefense(this)

  override def etherDef: Double = Formulas.calculateDisplayEtherDefense(this)

  /**
   * Retrieves the offline player instance.
   *
   * @return the offline player
   */
  def offlinePlayer: OfflinePlayer = Bukkit.getOfflinePlayer(uuid)

  /**
   * Checks if the current user is having a combination of character
   * and class that allows them to upgrade their blade.
   *
   * @return whether they can upgrade
   */
  def canChooseWeapon: Boolean =
    char match
      case Some(Character.NOAH) => cls.contains(ClassType.SWORDFIGHTER)
      case Some(Character.MIO) => cls.contains(ClassType.ZEPHYR)
      case Some(Character.EUNIE) => cls.contains(ClassType.MEDIC_GUNNER)
      case Some(Character.TAION) => cls.contains(ClassType.TACTICIAN)
      case Some(Character.LANZ) => cls.contains(ClassType.HEAVY_GUARD)
      case Some(Character.SENA) => cls.contains(ClassType.OGRE)
      case _ => false

  override def serialize(): util.Map[String, AnyRef] = Map(
    "uuid" -> uuid.toString,
    "class" -> cls.map(_.toString).orNull,
    "weapon" -> weapon.map(_.toString).orNull,
    "character" -> char.map(_.toString).orNull,
    "masterArts" -> masterArts.map(art => if art != null then art.toString else null).toList.asJava,
    "arts" -> arts.map(art => if art != null then art.toString else null).toList.asJava,
    "gems" -> gems.map(gem => if gem != null then gem.toString else null).toList.asJava,
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
    val weapon = tryGetting("weapon", WeaponType.valueOf)
    val char = tryGetting("character", Character.valueOf)
    val talentArt = tryGetting("talentArt", ArtType.valueOf)

    val masterArts = tryGettingArray("masterArts", ArtType.valueOf).toArray[ArtType]
    val arts = tryGettingArray("arts", ArtType.valueOf).toArray[ArtType]
    val gems = tryGettingArray("gems", GemType.valueOf).toArray[GemType]

    User(uuid, cls, weapon, char, masterArts, arts, gems, talentArt)

  private def tryGetting[T](key: String, f: String => T)(using map: util.Map[String, Any]): Option[T] =
    val result = Option(map.get(key)).map(_.toString)
    if result.isEmpty then return None
    Try(f(result.get)) match
      case Success(value) => Some(value)
      case Failure(_) => None

  private def tryGettingArray[T](key: String, f: String => T, size: Int = 3)(using map: util.Map[String, Any]): List[T] =
    map.get(key).asInstanceOf[util.List[String]].asScala
      .take(size)
      .map(s => if s != null then f(s) else null.asInstanceOf[T])
      .toList
