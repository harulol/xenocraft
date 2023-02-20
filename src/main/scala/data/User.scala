package dev.hawu.plugins.xenocraft
package data

import data.User.{deserialize, tryGetting}
import events.PlayerIncapacitateEvent
import events.arts.PlayerEquipArtEvent
import events.blades.{PlayerPostSheatheEvent, PlayerPostUnsheatheEvent, PlayerPreSheatheEvent, PlayerPreUnsheatheEvent}
import events.skills.PlayerEquipSkillEvent
import gui.{ArtsGUI, ClassesGUI}
import managers.{GemsManager, HotbarManager, SkillManager}
import skills.Skill
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
import scala.collection.{mutable, GenMap}
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Random, Success, Try}

/** Represents a player's data.
  */
case class User(private val _uuid: UUID, var cls: Option[ClassType] = None, var char: Option[Character] = None)
  extends ConfigurationSerializable with Attributable(_uuid):

  private val inventory = mutable.Map.empty[Int, ItemStack]
  private val classMemory = mutable.Map.empty[ClassType, ClassMemory]

  var bladeUnsheathed = false
  var lastSoulhackerSoul: Option[ClassType] = None

  /** Returns true if the player has [[art]] already equipped somewhere in the arts palette.
    */
  def isArtSelected(art: ArtType): Boolean = cls.map(getClassMemory).exists(cls => cls.arts.contains(art) || cls.masterArts.contains(art))

  /** Attempts to equip [[art]] into [[slot]], and [[master]] controls which palette to apply to. Does nothing if [[cls]] is not set.
    *
    * Also emits a [[PlayerEquipArtEvent]].
    */
  def equipArt(art: ArtType, slot: Int, master: Boolean): Unit =
    if cls.isEmpty then return ()
    val event = PlayerEquipArtEvent(this, art, master)
    Bukkit.getPluginManager.callEvent(event)
    if event.isCancelled then return ()

    val destination = if master then getClassMemory(cls.get).masterArts else getClassMemory(cls.get).arts
    swapSlot(destination, art, slot)

  private def swapSlot[T](array: Array[T], item: T, slot: Int): Unit =
    val index = array.indexOf(item)
    if index >= 0 then
      array(index) = array(slot)
      array(slot) = item
    else array(slot) = item

  /** Equips the [[art]] as a talent art. Does nothing if [[cls]] is not set.
    */
  def equipTalentArt(art: Option[ArtType]): Unit = if cls.isDefined then getClassMemory(cls.get).talentArt = art

  /** Equips the [[weapon]] as a weapon. Does nothing if [[cls]] is not set.
    */
  def equipWeapon(weapon: Option[WeaponType]): Unit = if cls.isDefined then getClassMemory(cls.get).weapon = weapon

  /** Attempts to equip a master [[skill]] at [[slot]]. Does nothing if [[cls]] is not set.
    */
  def equipMasterSkill(skill: SkillType, slot: Int): Unit =
    if cls.isEmpty then return ()
    val event = PlayerEquipSkillEvent(this, skill)
    Bukkit.getPluginManager.callEvent(event)
    if event.isCancelled then return ()

    swapSlot(getClassMemory(cls.get).masterSkills, skill, slot)

  /** Attempts to apply a character, ridding the player of the talent art they would not have access to.
    *
    * @param char
    *   the character
    */
  def applyCharacter(char: Character): Unit =
    this.char = Option(char)
    classMemory.foreach((cls, memory) => {
      if memory.talentArt.exists(!canUseArtAs(_, "talent")) then
        val replacement = ArtType.values.filter(_.isTalent).find(_.cls.contains(cls))
        memory.talentArt = replacement
    })

  /** Checks if the user can use an art given the context of (master, class or talent).
    *
    * If the context is a master art, the art must be given as a master art, not a talent art, and not a soulhacker art. If player is using
    * Soulhacker class the art must be a Soulhacker class and an Agnian art as Soulhacker uses Kevesi cooldown.
    *
    * If the context is a class art, the art must belong to such class and can not be a talent art. For Soulhackers, Kevesi arts belong to
    * the class.
    *
    * If the context is a talent art, the art must be a talent art. For Soulhackers, the talent art is always __Final Countdown__.
    *
    * If character chosen is '''Noah''', 2 additional talent arts are permitted regardless of chosen class. Or if character chosen is
    * '''Mio''', 1 additional talent art is permitted regardless of chosen class.
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

  /** Apply the [[clazz]] provided and apply skills if needed.
    */
  def applyClass(clazz: Option[ClassType]): Unit =
    unapplySkills()
    unapplyGems()
    
    cls = clazz
    if cls.isDefined then
      val memory = getClassMemory(cls.get)
      if memory.arts.forall(_ == null) then ArtType.values.filter(_.cls.contains(cls.get)).take(3).copyToArray(memory.arts)
    
    applySkills()
    applyGems()

  /** Unapplies all skills.
    */
  def unapplySkills(): Unit = if cls.isDefined then getAllSkills(cls.get).foreach(_.safeUnapply(this))

  def unapplyGems(): Unit = if cls.isDefined then getClassMemory(cls.get).gems.filter(_ != null).foreach((g, lvl) => GemsManager.unapplyGem(this, g, lvl))

  def applyGems(): Unit = if cls.isDefined then getClassMemory(cls.get).gems.filter(_ != null).foreach((g, lvl) => GemsManager.applyGem(this, g, lvl))

  /** Applies all skills.
    */
  def applySkills(): Unit = if cls.isDefined then getAllSkills(cls.get).foreach(_.safeApply(this))

  /** Unsheathe the blade.
    */
  def unsheathe(): Unit =
    if bladeUnsheathed || cls.isEmpty || getClassMemory(cls.get).weapon.isEmpty then return ()
    bladeUnsheathed = true

    inventory.clear()
    player.foreach(p => {
      val preEvent = PlayerPreUnsheatheEvent(p)
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

  /** Retrieves the weapon currently selected by the player. Always returns null if [[cls]] is not set.
    */
  def weapon: Option[WeaponType] = cls.flatMap(getClassMemory(_).weapon)

  /** Retrieves the array of master arts selected by the players. Always returns an empty detached array if [[cls]] is not set.
    */
  def masterArts: Array[ArtType] = cls.map(getClassMemory(_).masterArts).getOrElse(Array.ofDim(3))

  /** Retrieves the array of arts selected by the players. Always returns an empty detached array if [[cls]] is not set.
    */
  def arts: Array[ArtType] = cls.map(getClassMemory(_).arts).getOrElse(Array.ofDim(3))

  /** Retrieves the array of gems selected by the players. Always returns an empty detached array if [[cls]] is not set.
    */
  def gems: Array[(GemType, Int)] = cls.map(getClassMemory(_).gems).getOrElse(Array.ofDim(3))

  /** Retrieves the array of skills selected by the players. Always returns an empty detached array if [[cls]] is not set.
    */
  def masterSkills: Array[SkillType] = cls.map(getClassMemory(_).masterSkills).getOrElse(Array.ofDim(3))

  private def getClassMemory(cls: ClassType): ClassMemory = classMemory.getOrElseUpdate(
    if cls.isSoulhacker then ClassType.SOULHACKER_POWER else cls, {
      val talentArt =
        if cls.isSoulhacker then Some(ArtType.FINAL_COUNTDOWN) else ArtType.values.filter(_.isTalent).find(_.cls.contains(cls))
      val memory = ClassMemory(
        weapon = Some(cls.weaponType),
        masterArts = Array.ofDim(3),
        arts = Array.ofDim(3),
        masterSkills = Array.ofDim(3),
        gems = Array.ofDim(3),
        talentArt = talentArt,
      )
      classMemory += cls -> memory
      memory
    },
  )

  /** Retrieves the talent art of the player.
    */
  def talentArt: Option[ArtType] = cls.flatMap(getClassMemory(_).talentArt)

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
    "character" -> char.map(_.toString).orNull,
    "memory" -> classMemory.map(entry => entry._1.toString -> entry._2).asJava,
    "latest-soul" -> lastSoulhackerSoul.map(_.toString()).orNull,
  ).asJava

  private def getAllSkills(cls: ClassType): Array[Skill] = SkillType.values.filter(_.cls.contains(cls))
    .appendedAll(getClassMemory(cls).masterSkills).flatMap(SkillManager.get)

end User

/** Companion object for [[User]].
  */
object User:

  def deserialize(map: util.Map[String, Any]): User =
    given util.Map[String, Any] = map

    val uuid = UUID.fromString(map.get("uuid").toString)
    val cls = tryGetting("class", ClassType.valueOf)
    val char = tryGetting("character", Character.valueOf)
    val lastSoulhackerSoul = tryGetting("latest-soul", ClassType.valueOf)
    val memory = map.get("memory").asInstanceOf[util.Map[String, ClassMemory]].asScala.map(entry => ClassType.valueOf(entry._1) -> entry._2)

    val user = User(uuid, cls, char)
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

end User
