package dev.hawu.plugins.xenocraft
package data

import dev.hawu.plugins.api.collections.tuples.Pair
import dev.hawu.plugins.api.i18n.Locale
import dev.hawu.plugins.xenocraft.gui.GemsGUI

/**
 * Represents the gem that a player can equip.
 */
enum GemType(val category: GemCategory, val value1: (Double, Double), val value2: (Double, Double) = (0, 0), val percentage: Boolean = false):

  /**
   * Retrieves the localized name of the gem type.
   *
   * @param locale the locale
   * @return the localized name
   */
  def name(locale: Locale, level: Int = 0): String =
    val localizedName = GemsGUI.getModule.translate(locale, this.toString.replace('_', '-').toLowerCase)
    if level > 10 || level < 1 then localizedName
    else s"$localizedName ${I18n.toRomans(level)}"

  /**
   * Retrieves the localized description for the gem.
   *
   * @param locale the locale
   * @return the localized description
   */
  def description(locale: Locale, level: Int = 10): String =
    GemsGUI.getModule.translate(locale, s"${this.toString.replace('_', '-').toLowerCase}-desc",
      Pair.of("value1", format(value1At(level), true)), Pair.of("value2", format(value2At(level))),
    )

  private def format(num: Double, checkPercentage: Boolean = false): String =
    if checkPercentage && this.percentage then s"${(num * 100).intValue.toString}%"
    else num.intValue.toString

  /**
   * Calculates the increase of the stat if the gem is at the specified
   * level, using the values from value1 tuple.
   *
   * @param level the level
   * @return the real value
   */
  def value1At(level: Int = 10): Double = valueFrom(value1, level)

  /**
   * Calculates the increase of the stat if the gem is at the specified
   * level, using the values from value2 tuple.
   *
   * This only exists to serve a few Healer's gems, most other gems don't
   * have this attribute.
   *
   * @param level the level
   * @return the real value
   */
  def value2At(level: Int = 10): Double = valueFrom(value2, level)

  private def valueFrom(tuple: (Double, Double), level: Int): Double =
    val lvl = (level max 1 min 10) - 1
    val diff = (tuple._2 - tuple._1) / 9
    tuple._1 + (diff * lvl)

  case TAILWIND extends GemType(GemCategory.DEFENDER, (10, 35))
  case STEEL_PROTECTION extends GemType(GemCategory.DEFENDER, (5, 30))
  case ULTIMATE_COUNTER extends GemType(GemCategory.DEFENDER, (0.8, 2.8), percentage = true)
  case BRIMMING_SPIRIT extends GemType(GemCategory.DEFENDER, (0.2, 0.45), percentage = true)
  case PERILOUS_PRESENCE extends GemType(GemCategory.DEFENDER, (500, 3000))

  case LIFEBEARER extends GemType(GemCategory.HEALER, (0.2, 0.45), percentage = true)
  case SOOTHING_BREATH extends GemType(GemCategory.HEALER, (0.2, 0.45), (5, 15), percentage = true)
  case LIFESAVING_EXPERTISE extends GemType(GemCategory.HEALER, (0.2, 0.45), (5, 15), percentage = true)
  case SWELLING_BLESSING extends GemType(GemCategory.HEALER, (0.25, 0.5), percentage = true)
  case REFINED_BLESSING extends GemType(GemCategory.HEALER, (0.25, 0.5), percentage = true)

  case STEELCLEAVER extends GemType(GemCategory.ATTACKER, (20, 70))
  case ACCURATE_GRACE extends GemType(GemCategory.ATTACKER, (20, 45))
  case ANALYZE_WEAKNESS extends GemType(GemCategory.ATTACKER, (20, 70))
  case SWELLING_SCOURGE extends GemType(GemCategory.ATTACKER, (0.25, 0.5), percentage = true)
  case REFINED_INCANTATION extends GemType(GemCategory.ATTACKER, (0.25, 0.5), percentage = true)

  case IRON_CLAD extends GemType(GemCategory.SPECIAL, (100, 1500))
  case STEADY_STRIKER extends GemType(GemCategory.SPECIAL, (0.15, 0.4), percentage = true)
  case DOUBLESTRIKE extends GemType(GemCategory.SPECIAL, (0.15, 0.4), percentage = true)
  case EMPOWERED_COMBO extends GemType(GemCategory.SPECIAL, (0.25, 0.75), percentage = true)
  case DISPERSE_BLOODLUST extends GemType(GemCategory.SPECIAL, (0.2, 0.45), percentage = true)
