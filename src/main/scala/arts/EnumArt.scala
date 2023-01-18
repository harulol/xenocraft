package dev.hawu.plugins.xenocraft
package arts

import dev.hawu.plugins.xenocraft.classes.EnumClass
import dev.hawu.plugins.xenocraft.combat.StatusEffect
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin

import java.io.InputStreamReader

/**
 * Represents an art a user can use. One enum should
 * only be bound to one exact art object.
 *
 * I'm bad at designing so there are a lot of stuff
 * in this one enum class.
 */
enum EnumArt(val cls: EnumClass, val isMaster: Boolean = false, name: Option[String] = None):

  private var _artType: Option[ArtType] = None
  private var _aoeType: Option[AreaEffectType] = None
  private var _powerMultiplier: Option[Int] = None
  private var _rechargeGauge: Option[Double] = None
  private var _rechargeType: Option[ArtRechargeType] = None
  private var _buff: Option[StatusEffect] = None
  private var _reaction: Option[ArtReaction] = None
  private var _effect: Option[String] = None

  /**
   * Computes the display name for this art.
   *
   * @return The display name.
   */
  def displayName: String = name.getOrElse(this.toString.split("_").map(_.toLowerCase.capitalize).mkString(" "))

  /**
   * Gets the kebab case of the name.
   *
   * @return The kebab case.
   */
  def kebabCase: String = toString.replace('_', '-').toLowerCase

  /**
   * Gets the art type of this art.
   *
   * @return The art type.
   */
  def artType: ArtType = _artType.get

  /**
   * Gets the area effect type of this art.
   *
   * @return The area effect type.
   */
  def aoeType: AreaEffectType = _aoeType.get

  /**
   * Gets the power multiplier of this art.
   *
   * @return The power multiplier.
   */
  def powerMultiplier: Int = _powerMultiplier.get

  /**
   * Gets the recharge gauge of this art.
   *
   * @return The recharge gauge.
   */
  def rechargeGauge: Double = _rechargeGauge.get

  /**
   * Gets the recharge type of this art.
   *
   * @return The recharge type.
   */
  def rechargeType: ArtRechargeType = _rechargeType.get

  /**
   * Gets the buff of this art.
   *
   * @return The buff.
   */
  def buff: Option[StatusEffect] = _buff

  /**
   * Gets the reaction of this art.
   *
   * @return The reaction.
   */
  def reaction: Option[ArtReaction] = _reaction

  /**
   * The effect of the string as a description.
   *
   * @return the effect.
   */
  def effect: Option[String] = _effect

  case GROUND_BEAT extends EnumArt(EnumClass.SWORDFIGHTER, true)
  case SWORD_STRIKE extends EnumArt(EnumClass.SWORDFIGHTER)
  case EDGE_THRUST extends EnumArt(EnumClass.SWORDFIGHTER)
  case AIR_SLASH extends EnumArt(EnumClass.SWORDFIGHTER)
  case SHADOW_EYE extends EnumArt(EnumClass.SWORDFIGHTER, true)
  case OVERCLOCK_BUSTER extends EnumArt(EnumClass.SWORDFIGHTER)

/**
 * The type of art.
 */
object EnumArt:

  /**
   * Initializes all the enum arts.
   *
   * @param pl the plugin
   */
  def initialize(pl: JavaPlugin): Unit =
    val resource = InputStreamReader(pl.getResource(s"arts.yml"))
    val config = YamlConfiguration.loadConfiguration(resource)

    for art <- values do
      val artConfig = config.getConfigurationSection(art.kebabCase)
      art._artType = Some(ArtType.valueOf(artConfig.getString("type")))
      art._aoeType = Some(AreaEffectType.valueOf(artConfig.getString("aoe")))
      art._powerMultiplier = Some(artConfig.getInt("multiplier"))
      art._rechargeGauge = Some(artConfig.getDouble("recharge"))
      art._rechargeType = Some(ArtRechargeType.valueOf(artConfig.getString("recharge-type")))

      val buff = artConfig.getString("buff")
      if buff != null then art._buff = Some(StatusEffect.valueOf(buff))

      val reaction = artConfig.getString("reaction")
      if reaction != null then art._reaction = Some(ArtReaction.valueOf(reaction))

      val effect = artConfig.getString("effect")
      if effect != null then art._effect = Some(effect)
  end initialize
