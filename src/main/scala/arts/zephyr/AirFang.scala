package dev.hawu.plugins.xenocraft
package arts.zephyr

import arts.Art
import data.{ArtFusionBonus, ArtType, Character, User}
import events.combat.PlayerDealDamageEvent
import managers.AggroManager
import utils.Configuration

import dev.hawu.plugins.api.MathUtils
import org.bukkit.Sound
import org.bukkit.entity.{Mob, Player}

/** Air Fang (Zephyr): Boosts damage dealt when attacking enemies targeting you by 100%
  */
object AirFang extends Art(ArtType.AIR_FANG):

  // noinspection DuplicatedCode
  override def use(player: Player, user: User, fusion: Boolean): Boolean =
    if fusion then user.aggro *= Configuration.fusionBonus(ArtFusionBonus.AGGRO)
    val enemy = getEnemiesFront(player).headOption
    if enemy.isEmpty then return false

    user.isInAnimation = true
    val hitsCount = if user.char.contains(Character.MIO) then 3 else 2

    // Generate events
    val events = generateEvents(
      3, {
        val preemptive = AggroManager.getFor(player).map(_.getUniqueId).contains(enemy.get.uuid)
        val damageEvent = getEvent(player).targeting(enemy.get).setPreemptive(preemptive).build
        if preemptive then damageEvent.damageBonus1 += 1
        damageEvent.hits = hitsCount
        damageEvent
      },
    )
    for i <- 0 until hitsCount do schedule(i * 15, animateHit(player, enemy.get.entity, events(i)))
    schedule(45, user.isInAnimation = false)
    true

  private def animateHit(player: Player, entity: Mob, event: PlayerDealDamageEvent): Unit =
    entity.getWorld.playSound(entity.getLocation, Sound.ITEM_SHIELD_BLOCK, 1, 1)
    dealDamage(event)
    player.swingMainHand()
