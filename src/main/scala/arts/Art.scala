package dev.hawu.plugins.xenocraft
package arts

import dev.hawu.plugins.xenocraft.data.{ArtType, User}
import org.bukkit.entity.Player

/**
 * The very abstract implementation of an art.
 *
 * This class only provides a skeleton for arts
 * to implement.
 *
 * @param artType the art enum type
 */
abstract class Art(val artType: ArtType):

  /**
   * Starts the animation for the art.
   *
   * @param player the player to start the animation for
   * @param user   the user
   */
  def startAnimation(player: Player, user: User): Unit
