package dev.hawu.plugins.xenocraft
package arts.zephyr

import arts.Art
import data.{ArtType, User}
import events.combat.PlayerDealDamageEvent
import managers.StancesManager
import stances.{SpeedDemonStance, Stance}

import dev.hawu.plugins.api.events.Events
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

/** Speed Demon (Zephyr): Boosts evasion by 30% and reduces damage dealt by 15%
  */
object SpeedDemon extends Art(ArtType.SPEED_DEMON):

  Events.registerEvents(Xenocraft.getInstance, SpeedDemonStance)

  override def use(player: Player, user: User, fusion: Boolean, master: Boolean): Boolean =
    schedule(35, StancesManager.applyStance(player, SpeedDemonStance, fusion))
    scheduleAnimation(40, user)
    true
