package dev.hawu.plugins.xenocraft
package arts

/**
 * Represents the type of the the art,
 * whether it buffs self or deals damage, etc.
 */
enum ArtType:

  /**
   * Retrieves the properly formatted name.
   *
   * @return the name
   */
  def displayName = this.toString.split("_").map(_.toLowerCase.capitalize).mkString(" ")

  case PHYSICAL_ATTACK, ETHER_ATTACK, BUFF, HEALING, FIELD, STANCE, TAUNT, DEFENSE, CLEANSE
