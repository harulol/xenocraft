package dev.hawu.plugins.xenocraft
package classes

import dev.hawu.plugins.api.Strings

/**
 * The role of the class.
 *
 * Attacker classes are main DPSes of the party,
 * while Defender classes are the ones to take aggro target away
 * from enemies. Healer classes mainly buff all members and heal.
 */
enum ClassRole(val colorCode: Char):

  /**
   * Colorizes the provided string with the
   * respective color code.
   *
   * @param s the string
   * @return the colorized version
   */
  def colorize(s: String = this.toString) =
    Strings.color(s"&$colorCode$s")

  case ATTACKER extends ClassRole('c')
  case DEFENDER extends ClassRole('b')
  case HEALER extends ClassRole('a')
