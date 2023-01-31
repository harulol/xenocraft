package dev.hawu.plugins.xenocraft
package data

import dev.hawu.plugins.api.Strings

/** Represents the role of a class type.
  */
enum ClassRole(val colorCode: Char):

  /** Colorizes the provided string using the color code for this class role.
    *
    * @param s
    *   the string
    * @return
    *   the colorized version
    */
  def colorize(s: String = this.toString.toLowerCase.capitalize) = Strings.color(s"&$colorCode$s")

  case ATTACKER extends ClassRole('c')
  case DEFENDER extends ClassRole('b')
  case HEALER extends ClassRole('a')
