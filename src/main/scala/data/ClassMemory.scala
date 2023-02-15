package dev.hawu.plugins.xenocraft
package data

import org.bukkit.configuration.serialization.ConfigurationSerializable

import java.util
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters.*

/** Represents a memory data for class setups. The benefit would be that the setup is always synced and saved so switching back and forth
  * between classes doesn't require full reconfiguration.
  *
  * Each locket is completely independent of a User, these have to be properly managed within User class.
  */
case class ClassMemory(
  var weapon: Option[WeaponType] = None,
  masterArts: Array[ArtType] = Array.ofDim(3),
  masterSkills: Array[SkillType] = Array.ofDim(3),
  arts: Array[ArtType] = Array.ofDim(3),
  gems: Array[(GemType, Int)] = Array.ofDim(3),
  var talentArt: Option[ArtType] = None,
) extends ConfigurationSerializable:

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

/** Companion object for [[ClassMemory]].
  */
object ClassMemory:

  def deserialize(map: util.Map[String, Any]): ClassMemory =
    given util.Map[String, Any] = map

    ClassMemory(
      masterArts = getList("master-arts", ArtType.valueOf).toArray,
      arts = getList("arts", ArtType.valueOf).toArray,
      gems = getList(
        "arts",
        gem =>
          val arr = gem.split(":")
          GemType.valueOf(arr(0)) -> arr(1).toInt,
      ).toArray,
      talentArt = Option(map.get("talent-art")).map(_.asInstanceOf[String]).map(ArtType.valueOf),
    )

  private def getList[T](key: String, f: String => T)(using map: util.Map[String, Any]): List[T] = map.get(key)
    .asInstanceOf[util.List[String]].asScala.map(item => if item != null then f(item) else null.asInstanceOf[T]).toList

end ClassMemory
