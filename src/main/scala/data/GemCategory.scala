package dev.hawu.plugins.xenocraft
package data

/**
 * The category for gems.
 */
enum GemCategory(val colorCode: Char):

  case ATTACKER extends GemCategory('c')
  case DEFENDER extends GemCategory('b')
  case HEALER extends GemCategory('a')
  case SPECIAL extends GemCategory('6')
