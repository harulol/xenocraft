package dev.hawu.plugins.xenocraft
package data

/**
 * Represents the gem that a player can equip.
 */
enum GemType(val category: GemCategory):

  case TAILWIND extends GemType(GemCategory.DEFENDER)
  case STEEL_PROTECTION extends GemType(GemCategory.DEFENDER)
  case ULTIMATE_COUNTER extends GemType(GemCategory.DEFENDER)
  case BRIMMING_SPIRIT extends GemType(GemCategory.DEFENDER)
  case PERILOUS_PRESENCE extends GemType(GemCategory.DEFENDER)

  case LIFEBEARER extends GemType(GemCategory.HEALER)
  case SOOTHING_BREATH extends GemType(GemCategory.HEALER)
  case LIFESAVING_EXPERTISE extends GemType(GemCategory.HEALER)
  case SWELLING_BLESSING extends GemType(GemCategory.HEALER)
  case REFINED_BLESSING extends GemType(GemCategory.HEALER)

  case STEELCLEAVER extends GemType(GemCategory.ATTACKER)
  case ACCURATE_GRACE extends GemType(GemCategory.ATTACKER)
  case ANALYZE_WEAKNESS extends GemType(GemCategory.ATTACKER)
  case SWELLING_SCOURGE extends GemType(GemCategory.ATTACKER)
  case REFINED_INCANTATION extends GemType(GemCategory.ATTACKER)

  case IRON_CLAD extends GemType(GemCategory.SPECIAL)
  case STEADY_STRIKER extends GemType(GemCategory.SPECIAL)
  case DOUBLESTRIKE extends GemType(GemCategory.SPECIAL)
  case EMPOWERED_COMBO extends GemType(GemCategory.SPECIAL)
  case DISPERSE_BLOODLUST extends GemType(GemCategory.SPECIAL)
