package dev.hawu.plugins.xenocraft
package data

/**
 * The bonus that will be granted to the art
 * if it was used as a fusion art.
 *
 * Since fields related should be non-nullable,
 * for no bonus, please use [[ArtFusionBonus.NONE]].
 */
enum ArtFusionBonus:

  case DAMAGE, HEALING, DURATION, AGGRO, RANGE, BLOCK, NONE
