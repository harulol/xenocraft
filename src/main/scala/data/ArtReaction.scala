package dev.hawu.plugins.xenocraft
package data

import gui.ArtsGUI

import dev.hawu.plugins.api.i18n.Locale

/** Represents a reaction an art can affect enemies.
 */
enum ArtReaction:

  def name(locale: Locale): String = ArtsGUI.getModule.translate(locale, s"reaction-${this.toString.toLowerCase}")

  case BREAK, TOPPLE, LAUNCH, SMASH, DAZE, BURST
  case KNOCKBACK, BLOWDOWN
