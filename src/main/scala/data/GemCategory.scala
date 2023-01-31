package dev.hawu.plugins.xenocraft
package data

import org.bukkit.Material

/** The category for gems.
  */
enum GemCategory(val colorCode: Char, val icon: Material):

  /** Colorizes the provided string in this gem category's color.
    *
    * @param str
    *   the string
    * @param bold
    *   whether to apply bold
    * @return
    *   the string formatted
    */
  def colorize(str: String, bold: Boolean = false): String = s"&$colorCode${if bold then "&l" else ""}$str"

  case ATTACKER extends GemCategory('c', Material.REDSTONE)
  case DEFENDER extends GemCategory('b', Material.DIAMOND)
  case HEALER extends GemCategory('a', Material.EMERALD)
  case SPECIAL extends GemCategory('6', Material.GOLD_INGOT)
