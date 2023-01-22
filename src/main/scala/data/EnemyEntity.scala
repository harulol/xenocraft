package dev.hawu.plugins.xenocraft
package data

import dev.hawu.plugins.xenocraft.data.EnemyEntity.load
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.file.{FileConfiguration, YamlConfiguration}
import org.bukkit.entity.{EntityType, LivingEntity}

import java.io.InputStreamReader
import java.util.UUID
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

/**
 * Represents an enemy within a battlefield.
 *
 * @param uuid the uuid
 */
class EnemyEntity(uuid: UUID) extends Attributable(uuid):

  private val config = load(Bukkit.getEntity(uuid).getType)

  // DIRECTIONAL GUARD VALUES
  var guardFront: Double = config.getDouble("guard-front")
  var guardBack: Double = config.getDouble("guard-back")
  var guardLeft: Double = config.getDouble("guard-left")
  var guardRight: Double = config.getDouble("guard-right")

  // RESISTANCES
  var breakResistance: Double = config.getDouble("break-resistance")
  var blowdownResistance: Double = config.getDouble("blowdown-resistance")
  var knockbackResistance: Double = config.getDouble("knockback-resistance")
  var physDebuffDef: Double = config.getDouble("phys-debuff-def")
  var etherDebuffDef: Double = config.getDouble("ether-debuff-def")

  override def setHp(value: Double): Unit =
    this._hp = value max 0 min maxHp
    Option(Bukkit.getEntity(uuid)).map(_.asInstanceOf[LivingEntity]).foreach(e => {
      val percentage = hp / maxHp
      val maxHeath = e.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue
      e.setHealth(maxHeath * percentage)
    })

  override def maxHp: Double = config.getDouble("hp")

  override def attack: Double = config.getDouble("attack")

  override def healing: Double = config.getDouble("healing")

  override def dexterity: Double = config.getDouble("dexterity")

  override def agility: Double = config.getDouble("agility")

  override def critRate: Double = config.getDouble("crit-rate")

  override def physicalDef: Double = config.getDouble("phys-def")

  override def etherDef: Double = config.getDouble("ether-def")

/**
 * Companion object for enemy entities.
 */
object EnemyEntity:

  private val map = mutable.Map.empty[EntityType, FileConfiguration]

  private def load(entityType: EntityType): FileConfiguration =
    if map.contains(entityType) then return map(entityType)
    val resource = InputStreamReader(Xenocraft.getInstance.getResource(s"entities/${entityType.name()}.yml"))
    val config = YamlConfiguration.loadConfiguration(resource)
    map += entityType -> config
    config
