package dev.hawu.plugins.xenocraft
package data

import managers.GemsManager

import org.bukkit.configuration.serialization.ConfigurationSerializable

import java.util
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters.*

/** Represents a memory locket for holding predefined class data.
  *
  * So when a player switches to a class that have already configured before, there's no need to configure it again.
  *
  * Each locket is completely independent of a User, these have to be properly managed within User class.
  *
  * @param masterArts
  *   the master arts chosen
  * @param arts
  *   the arts chosen
  * @param gems
  *   the gems chosen
  * @param talentArt
  *   the talent art chosen
  */
case class ClassMemory(
  weapon: Option[WeaponType] = None,
  masterArts: Array[ArtType] = Array.ofDim(3),
  masterSkills: Array[SkillType] = Array.ofDim(3),
  arts: Array[ArtType] = Array.ofDim(3),
  gems: Array[(GemType, Int)] = Array.ofDim(3),
  talentArt: Option[ArtType] = None,
) extends ConfigurationSerializable:

  /** Applies this memory to the specified user.
    *
    * @param user
    *   the user
    * @since 2.0
    */
  def apply(user: User): Unit =
    user.weapon = weapon
    masterArts.copyToArray(user.masterArts)
    arts.copyToArray(user.arts)

    masterSkills.copyToArray(user.masterSkills)

    user.gems.filter(_ != null).foreach((gem, lvl) => GemsManager.unapplyGem(user, gem, lvl))
    gems.copyToArray(user.gems)
    gems.filter(_ != null).foreach((gem, lvl) => GemsManager.applyGem(user, gem, lvl))

    if talentArt.isDefined then user.talentArt = if user.canUseArtAs(talentArt.get, "talent") then talentArt else None
    else user.talentArt = None

  override def serialize(): util.Map[String, Any] = Map(
    "weapon" -> weapon.map(_.toString).orNull,
    "master-arts" -> masterArts.map(Option.apply).map(_.map(_.toString).orNull).toList.asJava,
    "master-skills" -> masterSkills.map(skill => if skill != null then skill.toString else null).toList.asJava,
    "arts" -> arts.map(Option.apply).map(_.map(_.toString).orNull).toList.asJava,
    "gems" -> {
      val buffer = ArrayBuffer.empty[String]
      for gem <- gems do if gem != null then buffer += s"${gem._1.toString}:${gem._2}" else buffer += null
      buffer.asJava
    },
    "talent-art" -> talentArt.map(_.toString).orNull,
  ).asJava

end ClassMemory

/** Companion object for [[ClassMemory]].
  */
object ClassMemory:

  /** Factory method for creating a ClassMemory from a user.
    *
    * @param user
    *   the user
    * @return
    *   the class memory
    */
  def apply(user: User): ClassMemory = new ClassMemory(
    weapon = user.weapon,
    masterArts = user.masterArts.clone(),
    arts = user.arts.clone(),
    gems = user.gems.clone(),
    talentArt = user.talentArt,
  )

  def deserialize(map: util.Map[String, Any]): ClassMemory = ClassMemory(
    masterArts = map.get("master-arts").asInstanceOf[util.List[String]].asScala
      .map(art => if art != null then ArtType.valueOf(art) else null).toArray,
    arts = map.get("arts").asInstanceOf[util.List[String]].asScala.map(art => if art != null then ArtType.valueOf(art) else null).toArray,
    gems = map.get("gems").asInstanceOf[util.List[String]].asScala.map(gem =>
      if gem != null then
        val arr = gem.split(":")
        GemType.valueOf(arr(0)) -> arr(1).toInt
      else null,
    ).toArray,
    talentArt = Option(map.get("talent-art")).map(_.asInstanceOf[String]).map(ArtType.valueOf),
  )

end ClassMemory
