package dev.hawu.plugins.xenocraft
package data

import dev.hawu.plugins.api.i18n.Locale
import dev.hawu.plugins.xenocraft.gui.ArtsGUI

/** Represents a type of target an art can have.
  */
enum ArtTarget:

  /** Retrieves the name of the target localized.
    *
    * @param locale
    *   the locale to localize to.
    * @return
    *   the localized name of the target
    */
  def name(locale: Locale): String = ArtsGUI.getModule
    .translate(locale, s"target-${toString.toLowerCase.replace('_', '-')}")

  case SINGLE, FRONT_AOE, SELF, CIRCLE_AOE, ENEMY_AOE, PARTY
