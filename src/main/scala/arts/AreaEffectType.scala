package dev.hawu.plugins.xenocraft
package arts

/**
 * In what way will the art effect?
 *
 * Self? Enemy? AOE?
 */
enum AreaEffectType:

  /**
   * Retrieves the properly formatted name of the effect type.
   *
   * @return the name of the effect type.
   */
  def displayName = this.toString.split("_").map(s => if (s == "AOE") "AOE" else s.toLowerCase.capitalize).mkString(" ")

  case ONE_TARGET, FRONTWARD_AOE, SELF, AOE_AROUND_USER
