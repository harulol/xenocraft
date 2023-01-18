package dev.hawu.plugins.xenocraft
package arts

/**
 * Represents the type of action needed
 * to recharge a certain art.
 *
 * All Kevesi arts use TIME_RECHARGE, while Agnusian
 * arts use AUTO_ATTACK_RECHARGE. Only talent arts use
 * ROLE_ACTION_RECHARGE.
 */
enum ArtRechargeType:

  case AUTO_ATTACK_RECHARGE, TIME_RECHARGE, ROLE_ACTION_RECHARGE
