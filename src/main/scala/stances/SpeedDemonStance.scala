package dev.hawu.plugins.xenocraft
package stances

import data.User
import events.combat.PlayerDealDamageEvent

import org.bukkit.event.EventHandler

/** A stance that boosts evasion chance by 30% but at the cost of 15% lower damage.
  */
object SpeedDemonStance extends Stance:

  private val EVASION_CHANCE_BOOST = 0.3
  private val DAMAGE_REDUCTION = 0.15

  override def apply(user: User): Unit = user.evasionChance += EVASION_CHANCE_BOOST

  override def unapply(user: User): Unit = user.evasionChance -= EVASION_CHANCE_BOOST

  @EventHandler
  private def onDamageEvent(event: PlayerDealDamageEvent): Unit = if isApplied(event.user) then event.damageBonus1 -= DAMAGE_REDUCTION
