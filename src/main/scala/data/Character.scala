package dev.hawu.plugins.xenocraft
package data

import dev.hawu.plugins.api.i18n.{LanguageModule, Locale}
import dev.hawu.plugins.xenocraft.gui.CharactersGUI
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

/** Represents a character that a user can select as the base stats holder.
  */
enum Character(
  val icon: Material,
  val baseHp: Double,
  val baseAttack: Double,
  val baseHealingPower: Double,
  val baseDexterity: Double,
  val baseAgility: Double,
):

  /** The default name of this character.
    *
    * @return
    *   the character
    */
  def name: String = name(Locale.en_US)

  /** Retrieves the localized name of this character.
    *
    * @param locale
    *   the locale
    * @return
    *   the character name
    */
  def name(locale: Locale): String = CharactersGUI.getModule.translate(locale, this.toString.toLowerCase)

  /** Retrieves the description with the default locale of en_US.
    *
    * @return
    *   the description
    */
  def description: String = description(Locale.en_US)

  /** Retrieves the description with the provided locale.
    *
    * @param locale
    *   the locale
    * @return
    *   the description
    */
  def description(locale: Locale): String = CharactersGUI.getModule
    .translate(locale, s"${this.toString.toLowerCase}-desc")

  case NOAH
    extends Character(
      icon = Material.IRON_SWORD,
      baseHp = 6994,
      baseAttack = 388,
      baseHealingPower = 368,
      baseDexterity = 310,
      baseAgility = 215,
    )

  case MIO
    extends Character(
      icon = Material.ENDER_PEARL,
      baseHp = 6754,
      baseAttack = 361,
      baseHealingPower = 341,
      baseDexterity = 306,
      baseAgility = 234,
    )

  case EUNIE
    extends Character(
      icon = Material.FEATHER,
      baseHp = 6364,
      baseAttack = 346,
      baseHealingPower = 409,
      baseDexterity = 298,
      baseAgility = 223,
    )

  case TAION
    extends Character(
      icon = Material.PAPER,
      baseHp = 6371,
      baseAttack = 368,
      baseHealingPower = 381,
      baseDexterity = 325,
      baseAgility = 197,
    )

  case LANZ
    extends Character(
      icon = Material.DIAMOND_CHESTPLATE,
      baseHp = 7755,
      baseAttack = 392,
      baseHealingPower = 329,
      baseDexterity = 297,
      baseAgility = 284,
    )

  case SENA
    extends Character(
      icon = Material.DIAMOND_AXE,
      baseHp = 7125,
      baseAttack = 398,
      baseHealingPower = 346,
      baseDexterity = 282,
      baseAgility = 216,
    )

end Character
