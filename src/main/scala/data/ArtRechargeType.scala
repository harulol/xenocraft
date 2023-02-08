package dev.hawu.plugins.xenocraft
package data

import gui.ArtsGUI

import dev.hawu.plugins.api.i18n.Locale

/** Represents how an art should recharge.
 */
enum ArtRechargeType:

  /** Retrieves the display name of the recharge type.
   *
   * @param locale
    *   the locale
    * @return
    *   the display name
    */
  def name(locale: Locale): String = ArtsGUI.getModule
    .translate(locale, s"${toString.toLowerCase.replace('_', '-')}-recharge")

  case TIME, AUTO_ATTACK, ROLE_ACTION
