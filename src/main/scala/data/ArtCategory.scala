package dev.hawu.plugins.xenocraft
package data

import gui.ArtsGUI

import dev.hawu.plugins.api.i18n.Locale

/** Represents the category an art can have.
 */
enum ArtCategory:

  /** Retrieves the display name of this category localized.
   *
   * @param locale
    *   the locale
    * @return
    *   the display naem
    */
  def name(locale: Locale): String = ArtsGUI.getModule.translate(locale, s"art-${toString.toLowerCase}")

  case PHYSICAL, ETHER, BUFF, DEBUFF, STANCE, HEALING, FIELD, TAUNT, CLEANSE, DEFENSE
