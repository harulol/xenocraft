package dev.hawu.plugins.xenocraft
package data

import data.User.{deserialize, tryGetting}
import events.blades.{PlayerPostSheatheEvent, PlayerPostUnsheatheEvent, PlayerPreSheatheEvent}
import events.{PlayerIncapacitateEvent, PlayerSheatheEvent, PlayerUnsheatheEvent}
import gui.{ArtsGUI, ClassesGUI}
import managers.{GemsManager, HotbarManager}
import skills.SkillManager
import utils.Formulas

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.i18n.LanguageModule
import dev.hawu.plugins.api.items.ItemStackBuilder
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.{ItemFlag, ItemStack}
import org.bukkit.{Bukkit, Effect, EntityEffect, Material, OfflinePlayer}

import java.util
import java.util.UUID
import scala.collection.{GenMap, mutable}
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Random, Success, Try}

/** Represents a player's data.
  *
  * @param uuid
  *   The UUID of the player.
  * @param cls
  *   The class of the player.
  * @param char
  *   The character preset of the player.
  * @param masterArts
  *   The master arts.
  * @param arts
  *   The class arts.
  * @param gems
  *   The equipped gems.
  * @param talentArt
  *   The equipped talent art.
  */
case class User(
  private val _uuid: UUID,
  var cls: Option[ClassType] = None,
  var weapon: Option[WeaponType] = None,
  var char: Option[Character] = None,
  masterArts: Array[ArtType] = Array.ofDim(3),
  arts: Array[ArtType] = Array.ofDim(3),
  gems: Array[(GemType, Int)] = Array.ofDim(3),
  masterSkills: Array[SkillType] = Array.ofDim(3),
  var talentArt: Option[ArtType] = None,
) extends ConfigurationSerializable with Attributable(_uuid):

  private val inventory = mutable.Map.empty[Int, ItemStack]
  private val classMemory = mutable.Map.empty[ClassType, ClassMemory]

  var bladeUnsheathed = false
  var lastSoulhackerSoul: Option[ClassType] = None

  /** Checks if a certain art is currently already being selected.
    *
    * @param art
    *   the art
    * @return
    *   the result
    */
  def isArtSelected(art: ArtType): Boolean = masterArts.contains(art) || arts.contains(art)

  /** Attempts to equip an art.
    *
    * @param art
    *   the art
    * @param slot
    *   the slot
    * @param master
    *   whether it is a master art
    */
  def equipArt(art: ArtType, slot: Int, master: Boolean): Unit =
    val arr = if master then masterArts else arts
    val index = arr.indexOf(art)

    if index >= 0 then
      arr(index) = arr(slot)
      arr(slot) = art
    else arr(slot) = art

  /** Attempts to equip a skill.
    *
    * @param skill
    *   the skill
    * @param slot
    *   the slot
    */
  def equipMasterSkill(skill: SkillType, slot: Int): Unit =
    val index = masterSkills.indexOf(skill)
    if index >= 0 then
      masterSkills(index) = masterSkills(slot)
      masterSkills(slot) = skill
    else masterSkills(slot) = skill

  /** Attempts to apply a character, ridding the player of the talent art they would not have access to.
    *
    * @param char
    *   the character
    */
  def applyCharacter(char: Character): Unit =
    this.char = Option(char)
    talentArt.foreach(art => if !canUseArtAs(art, "talent") then talentArt = None)

  /** Checks if the user can use an art given the context.
    *
    * @param art
    *   the art
    * @param what
    *   the context
    * @return
    *   whether they are eligible
    */
  def canUseArtAs(art: ArtType, what: "master" | "class" | "talent"): Boolean =
    // Since Soulhacker is a Kevesi class, master arts must be of agnian classes.
    // Other classes can not use Soulhacker's arts.
    what match
      case "master" =>
        if cls.exists(_.isSoulhacker) then art.isAgnian && art.isSoulhacker else art.isMaster && !art.isTalent && !art.isSoulhacker
      case "class" =>
        if cls.exists(_.isSoulhacker) then art.isKevesi && art.isSoulhacker
        else cls.isDefined && art.cls.contains(cls.get) && !art.isTalent && !art.isSoulhacker
      case "talent" =>
        // Noah and Mio can still use respective talent arts while using Soulhacker.
        art match
          case ArtType.INFINITY_BLADE | ArtType.UNLIMITED_SWORD => char.contains(Character.NOAH)
          case ArtType.DOMINION_FLOWER                          => char.contains(Character.MIO)
          case ArtType.FINAL_LUCKY_SEVEN                        => false
          case _ => if cls.exists(_.isSoulhacker) then art.isSoulhacker && art.isTalent else art.isTalent

  /** Apply the class provided and put up the arts, skills and gems memory.
    *
    * @param clazz
    *   the class to apply
    */
  def applyClass(clazz: Option[ClassType]): Unit =
    if cls.exists(_.isSoulhacker) then classMemory.put(ClassType.SOULHACKER_POWER, ClassMemory(this))
    else cls.foreach(classMemory.put(_, ClassMemory(this)))

    if cls.isDefined then
      SkillType.values.filter(_.cls.get == cls.get).map(SkillManager.get).filter(_.isDefined).map(_.get).foreach(_.safeUnapply(this))

    if clazz.isDefined then
      val memory = classMemory.get(if clazz.exists(_.isSoulhacker) then ClassType.SOULHACKER_POWER else clazz.get)
      if memory.isDefined then
        memory.get.apply(this)
        cls = clazz
        if weapon.isEmpty then weapon = Some(cls.get.weaponType)
      else
        cls = clazz
        weapon = Some(clazz.get.weaponType)
        Array.ofDim[ArtType](3).copyToArray(masterArts)
        Array.ofDim[ArtType](3).copyToArray(arts)
        ArtType.values.filter(_.cls.contains(clazz.get)).take(3).copyToArray(arts)
        Array.ofDim[(GemType, Int)](3).copyToArray(gems)
        talentArt = ArtType.values.filter(_.isTalent).find(_.cls.contains(clazz.get))
        applyClass(cls)

      SkillType.values.filter(_.cls.get == clazz.get).map(SkillManager.get).filter(_.isDefined).map(_.get).foreach(_.safeApply(this))
    end if

  end applyClass

  /** Unsheathe the blade.
    */
  def unsheathe(): Unit =
    if bladeUnsheathed || cls.isEmpty || weapon.isEmpty then return ()
    bladeUnsheathed = true

    inventory.clear()
    player.foreach(p => {
      val preEvent = PlayerPreSheatheEvent(p)
      Tasks.run(() => Bukkit.getPluginManager.callEvent(preEvent)).plugin(Xenocraft.getInstance).run()
      if !preEvent.isCancelled then
        // Main unsheathing logic
        p.getInventory.getContents.zipWithIndex.foreach((item, index) => {
          inventory += index -> item
          p.getInventory.setItem(index, null)
        })

        val postEvent = PlayerPostUnsheatheEvent(p)
        Tasks.run(() => Bukkit.getPluginManager.callEvent(postEvent)).plugin(Xenocraft.getInstance).run()
        p.getInventory.setHeldItemSlot(0)
    })

  end unsheathe

  /** Sheathes the blade back and disables combat.
    */
  def sheathe(): Unit =
    if !bladeUnsheathed then return ()
    bladeUnsheathed = false

    player.foreach { p =>
      val preEvent = PlayerPreSheatheEvent(p)
      Tasks.run(() => Bukkit.getPluginManager.callEvent(preEvent)).plugin(Xenocraft.getInstance).run()
      if !preEvent.isCancelled then
        inventory.foreach((index, item) => p.getInventory.setItem(index, item))

        val postEvent = PlayerPostSheatheEvent(p)
        Tasks.run(() => Bukkit.getPluginManager.callEvent(postEvent)).plugin(Xenocraft.getInstance).run()
    }
    inventory.clear()

  /** Attempts to retrieve the player instance from the user.
    *
    * @return
    *   the player instance
    */
  def player: Option[Player] = Option(Bukkit.getPlayer(uuid))

  override def maxHp: Double = Formulas.calculateHp(this)

  override def attack: Double = Formulas.calculateAttack(this)

  override def healing: Double = Formulas.calculateHealingPower(this)

  override def dexterity: Double = Formulas.calculateDexterity(this)

  override def agility: Double = Formulas.calculateAgility(this)

  override def critRate: Double = Formulas.calculateDisplayCritRate(this)

  override def blockRate: Double = Formulas.calculateDisplayBlockRate(this)

  override def physicalDef: Double = Formulas.calculateDisplayPhysDefense(this)

  override def etherDef: Double = Formulas.calculateDisplayEtherDefense(this)

  /** Retrieves the offline player instance.
    *
    * @return
    *   the offline player
    */
  def offlinePlayer: OfflinePlayer = Bukkit.getOfflinePlayer(uuid)

  /** Checks if the current user is having a combination of character and class that allows them to upgrade their blade.
    *
    * @return
    *   whether they can upgrade
    */
  def canChooseWeapon: Boolean = char match
    case Some(Character.NOAH)  => cls.contains(ClassType.SWORDFIGHTER)
    case Some(Character.MIO)   => cls.contains(ClassType.ZEPHYR)
    case Some(Character.EUNIE) => cls.contains(ClassType.MEDIC_GUNNER)
    case Some(Character.TAION) => cls.contains(ClassType.TACTICIAN)
    case Some(Character.LANZ)  => cls.contains(ClassType.HEAVY_GUARD)
    case Some(Character.SENA)  => cls.contains(ClassType.OGRE)
    case _                     => false

  override def serialize(): util.Map[String, AnyRef] = Map(
    "uuid" -> uuid.toString,
    "class" -> cls.map(_.toString).orNull,
    "weapon" -> weapon.map(_.toString).orNull,
    "character" -> char.map(_.toString).orNull,
    "masterArts" -> masterArts.map(art => if art != null then art.toString else null).toList.asJava,
    "masterSkills" -> masterSkills.map(skill => if skill != null then skill.toString else null).toList.asJava,
    "arts" -> arts.map(art => if art != null then art.toString else null).toList.asJava,
    "gems" -> gems.map(tuple => if tuple != null then s"${tuple._1.toString}:${tuple._2}" else null).toList.asJava,
    "talentArt" -> talentArt.map(_.toString).orNull,
    "memory" -> classMemory.map(entry => entry._1.toString -> entry._2).asJava,
    "latestSoul" -> lastSoulhackerSoul.map(_.toString()).orNull,
  ).asJava

end User

/** Companion object for [[User]].
  */
object User:

  def deserialize(map: util.Map[String, Any]): User =
    given util.Map[String, Any] = map

    val uuid = UUID.fromString(map.get("uuid").toString)
    val cls = tryGetting("class", ClassType.valueOf)
    val weapon = tryGetting("weapon", WeaponType.valueOf)
    val char = tryGetting("character", Character.valueOf)
    val talentArt = tryGetting("talentArt", ArtType.valueOf)
    val lastSoulhackerSoul = tryGetting("latestSoul", ClassType.valueOf)

    val masterArts = tryGettingArray("masterArts", ArtType.valueOf).toArray[ArtType]
    val masterSkills = tryGettingArray("masterSkills", SkillType.valueOf).toArray[SkillType]
    val arts = tryGettingArray("arts", ArtType.valueOf).toArray[ArtType]
    val gems = tryGettingArray("gems", String.valueOf).toArray[String].map(s => {
      if s != null && s != "null" then
        val arr = s.split(":")
        GemType.valueOf(arr(0)) -> arr(1).toInt
      else null
    })

    val memory = map.get("memory").asInstanceOf[util.Map[String, ClassMemory]].asScala.map(entry => ClassType.valueOf(entry._1) -> entry._2)

    val user = User(uuid, cls, weapon, char, masterArts, arts, gems, masterSkills, talentArt)
    gems.filter(_ != null).foreach((gem, lvl) => GemsManager.applyGem(user, gem, lvl))
    user.classMemory ++= memory
    user.lastSoulhackerSoul = lastSoulhackerSoul
    user

  end deserialize

  private def tryGetting[T](key: String, f: String => T)(using map: util.Map[String, Any]): Option[T] =
    val result = Option(map.get(key)).map(_.toString)
    if result.isEmpty then return None
    Try(f(result.get)) match
      case Success(value) => Some(value)
      case Failure(_)     => None

  private def tryGettingArray[T](key: String, f: String => T, size: Int = 3)(using map: util.Map[String, Any]): List[T] = map.get(key)
    .asInstanceOf[util.List[String]].asScala.take(size).map(s => if s != null then f(s) else null.asInstanceOf[T]).toList

end User
