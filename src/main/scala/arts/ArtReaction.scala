package dev.hawu.plugins.xenocraft
package arts

import dev.hawu.plugins.xenocraft.arts.ArtReaction.BLOWDOWN

/**
 * The type of reaction an art can inflict
 * on the enemy.
 */
enum ArtReaction:

  /**
   * Checks if this reaction is one of the special reactions.
   *
   * Special reactions don't linger or do any damage. They simply
   * apply a special effect to the enemy.
   *
   * [[BLOWDOWN]] hits the enemy away flying. [[KNOCKBACK]] makes the enemy
   * step back a little. [[DOOM]] instantly kills any enemy.
   *
   * @return whether it is special
   */
  def isSpecial: Boolean = this match
    case BLOWDOWN | KNOCKBACK | DOOM => true
    case _                           => false

  case BREAK, TOPPLE, LAUNCH, SMASH, DAZE, BURST
  case BLOWDOWN, KNOCKBACK, DOOM
