package dev.hawu.plugins.xenocraft
package arts.zephyr

import arts.Art
import data.{ArtFusionBonus, ArtType, User}
import events.combat.PlayerDealDamageEvent
import utils.Configuration

import dev.hawu.plugins.api.MathUtils
import dev.hawu.plugins.api.misc.Raytracing
import org.bukkit.entity.{Mob, Player}
import org.bukkit.{Particle, Sound}

import java.util.concurrent.ThreadLocalRandom

/** Butterfly Blade (Zephyr): Boosts aggro generated when using Art by 100%
 */
object ButterflyBlade extends Art(ArtType.BUTTERFLY_BLADE):

  override def use(player: Player, user: User, fusion: Boolean): Boolean =
    val addition = user.artAggroGeneration * (if fusion then Configuration.fusionBonus(ArtFusionBonus.AGGRO) else 1.0)
    user.artAggroGeneration += addition

    val enemy = getEnemiesFront(player).headOption
    if enemy.isEmpty then return false

    user.isInAnimation = true
    val events = generateEvents(2, getEvent(player).targeting(enemy.get).build)
    schedule(10, animateHit(enemy.get.entity, events(0)))
    schedule(20, animateHit(enemy.get.entity, events(1)))
    schedule(30, user.isInAnimation = false)
    true

  private def animateHit(target: Mob, event: PlayerDealDamageEvent): Unit =
    val loc = target.getLocation.add(0.0, target.getHeight / 2, 0.0)
    val direction = target.getLocation.getDirection

    val start = loc.clone().add(MathUtils.getLeftUnit(direction))
    val end = loc.clone().add(MathUtils.getRightUnit(direction))
    if ThreadLocalRandom.current().nextBoolean() then
      start.add(direction)
      end.add(direction.multiply(-1))
    else
      end.add(direction)
      start.add(direction.multiply(-1))

    Raytracing.startNew().origin(end).direction(end.toVector.subtract(start.toVector)).step(0.2).distance(end.distance(start))
      .eachStep(loc => loc.getWorld.spawnParticle(Particle.FIREWORKS_SPARK, loc, 1, 0, 0, 0, 0.5))
    target.getWorld.playSound(target.getLocation, Sound.ENTITY_PLAYER_ATTACK_WEAK, 1, 1)
    dealDamage(event)
    event.getPlayer.swingMainHand()
