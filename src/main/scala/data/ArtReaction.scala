package dev.hawu.plugins.xenocraft
package data

import dev.hawu.plugins.api.i18n.Locale
import dev.hawu.plugins.xenocraft.gui.ArtsGUI

/** Represents a reaction an art can affect enemies.
  */
enum ArtReaction:

  def name(locale: Locale): String = ArtsGUI.getModule.translate(locale, s"reaction-${this.toString.toLowerCase}")

  case BREAK, TOPPLE, LAUNCH, SMASH, DAZE, BURST
  case KNOCKBACK, BLOWDOWN
