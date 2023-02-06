package dev.hawu.plugins.xenocraft
package data

import org.bukkit.entity.Mob
import java.util.concurrent.ThreadLocalRandom
import scala.collection.mutable
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.FileConfigurationOptions
import org.bukkit.entity.Entity
import dev.hawu.plugins.xenocraft.data.EnemyEntity.kebabCase
import java.io.File
import org.bukkit.configuration.file.YamlConfiguration
import scala.util.Try
import scala.util.Success
import java.io.InputStreamReader
import scala.ref.WeakReference
import org.bukkit.EntityEffect
import dev.hawu.plugins.xenocraft.data.EnemyEntity.standardize
import dev.hawu.plugins.xenocraft.combat.BossbarManager

/** Represents an enemy.
  */
class EnemyEntity(val entity: Mob) extends Attributable(entity.getUniqueId()):

  private var _maxHp = 0.0
  private var _attack = 50.0
  private var _healing = 0.0
  private var _dexterity = 0.0
  private var _agility = 0.0
  private var _critRate = 0.0
  private var _physDef = 0.0
  private var _etherDef = 0.0
  var name: String = standardize(entity)
  var guardFront = 0.0
  var guardBack = 0.0
  var guardLeft = 0.0
  var guardRight = 0.0
  var breakResistance = 0.0
  var toppleResistance = 0.0
  var launchResistance = 0.0
  var smashResistance = 0.0
  var dazeResistance = 0.0
  var burstResistance = 0.0
  var physicalDebuffResistance = 0.0
  var etherDebuffResistance = 0.0
  var knockbackResistance = 0.0
  var blowdownResistance = 0.0

  // Reset
  if entity != null && !entity.isDead() then
    entity.setHealth(entity.getMaxHealth())
    _maxHp = entity.getMaxHealth()
    _hp = _maxHp

  override def setHp(value: Double): Unit =
    if entity != null && !entity.isDead() then
      val percentage = value / _maxHp
      val realHealth = (percentage * entity.getMaxHealth()) min entity.getMaxHealth() max 0

      if realHealth < entity.getHealth() then
        entity.playEffect(EntityEffect.HURT)
        entity.setNoDamageTicks(10)

      val bar = BossbarManager.makeBar(this)
      entity.setHealth(realHealth)
      bar.setProgress(percentage min 1 max 0)
      _hp = value
    else BossbarManager.clear(entity)

  override def attack: Double = _attack

  override def healing: Double = _healing

  override def dexterity: Double = _dexterity

  override def critRate: Double = _critRate

  override def etherDef: Double = _etherDef

  override def physicalDef: Double = _physDef

  override def maxHp: Double = _maxHp

  override def agility: Double = _agility

end EnemyEntity

/** Companion object for [[EnemyEntity]].
  */
object EnemyEntity:

  private val configs = mutable.Map.empty[String, FileConfiguration]

  /** Makes the name of the entity type in kebab case
    *
    * @param entity
    *   the entity
    * @return
    *   the standardized name
    */
  def kebabCase(entity: Entity): String = entity.getType().name().toLowerCase().replace('_', '-')

  /** Standardizes the name of the entity type.
    *
    * @param entity
    *   the entity
    * @return
    *   the standardized name
    */
  def standardize(entity: Entity): String = entity.getType().name().split("_").map(_.toLowerCase().capitalize)
    .mkString(" ")

  /** Creates an enemy entity with a random state.
    *
    * @param entity
    *   the entity
    * @return
    *   the enemy entity
    */
  def apply(entity: Mob): EnemyEntity =
    val result = ThreadLocalRandom.current().nextInt(100)
    val state = if result < 75 then 0 else if result < 95 then 1 else 2
    EnemyEntity(state, entity)

  /** Clears all cached configs.
    */
  def reloadConfig(): Unit = configs.clear()

  private def canLoadConfig(path: String): Boolean =
    val file = File(Xenocraft.getInstance.getDataFolder(), s"$path.yml")
    if file.exists() then
      val result = Try(YamlConfiguration.loadConfiguration(file))
      result match
        case Success(value) =>
          configs.put(path, value)
          return true
        case _ => ()

    val resource = Xenocraft.getInstance.getResource(s"$path.yml")
    if resource == null then return false

    Xenocraft.getInstance.saveResource(s"$path.yml", true)
    configs(path) = YamlConfiguration.loadConfiguration(InputStreamReader(resource))
    true

  // PATH DOES NOT INCLUDE .YML
  private def applyFromConfig(entity: EnemyEntity, path: String): Unit =
    if !canLoadConfig(path) then return

    val config = configs(path)
    if config.getString("name") != null then entity.name = config.getString("name")
    entity._maxHp = config.getDouble("max-hp")
    entity._attack = config.getDouble("attack")
    entity._healing = config.getDouble("healing")
    entity._dexterity = config.getDouble("dexterity")
    entity._agility = config.getDouble("agility")
    entity._critRate = config.getDouble("crit-rate")
    entity._physDef = config.getDouble("phys-def")
    entity._etherDef = config.getDouble("ether-def")
    entity.guardFront = config.getDouble("guard-front")
    entity.guardBack = config.getDouble("guard-back")
    entity.guardLeft = config.getDouble("guard-left")
    entity.guardRight = config.getDouble("guard-right")
    entity.breakResistance = config.getDouble("break-resistance")
    entity.toppleResistance = config.getDouble("topple-resistance")
    entity.launchResistance = config.getDouble("launch-resistance")
    entity.smashResistance = config.getDouble("smash-resistance")
    entity.dazeResistance = config.getDouble("daze-resistance")
    entity.burstResistance = config.getDouble("burst-resistance")
    entity.physicalDebuffResistance = config.getDouble("phys-debuff-resistance")
    entity.etherDebuffResistance = config.getDouble("ether-debuff-resistance")
    entity.knockbackResistance = config.getDouble("knockback-resistance")
    entity.blowdownResistance = config.getDouble("blowdown-resistance")

  end applyFromConfig

  /** Attempts to create an enemy entity.
    *
    * @param state
    *   0 is a normal enemy, 1 is an elite, 2 is a unique one.
    * @param entity
    *   the entity
    * @return
    *   the enemy entity
    */
  def apply(state: Int, entity: Mob): EnemyEntity =
    val enemy = new EnemyEntity(entity)
    val configName = state match
      case 0 => applyFromConfig(enemy, s"entities/${kebabCase(entity)}/${kebabCase(entity)}_normal")
      case 1 => applyFromConfig(enemy, s"entities/${kebabCase(entity)}/${kebabCase(entity)}_elite")
      case 2 => applyFromConfig(enemy, s"entities/${kebabCase(entity)}/${kebabCase(entity)}_unique")
      case _ => ()

    enemy.setHp(enemy.maxHp)
    enemy

end EnemyEntity
