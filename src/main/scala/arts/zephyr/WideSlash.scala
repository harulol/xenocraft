package dev.hawu.plugins.xenocraft
package arts.zephyr

import arts.Art
import data.{ArtType, User}
import events.combat.PlayerDealDamageEvent

import dev.hawu.plugins.api.misc.Raytracing
import org.bukkit.Particle
import org.bukkit.entity.Player

/** Wide Slash (Zephyr): While Art is active, evades enemy attacks at the cost of a low Critical Rate
  */
object WideSlash extends Art(ArtType.WIDE_SLASH):

  override def use(player: Player, user: User, fusion: Boolean, master: Boolean): Boolean =
    user.isEvading = true
    schedule(20, user.isEvading = false)

    val enemy = getEnemiesFront(player).headOption
    if enemy.isEmpty then return false

    val events = generateEvents(2, getEvent(player).targeting(enemy.get).artCritMod(-0.8).fusion(fusion).build)

    schedule(10, animateHit(events(0)))
    schedule(15, animateHit(events(1)))
    scheduleAnimation(20, user)
    true

  private def animateHit(event: PlayerDealDamageEvent): Unit =
    val mob = event.entity.entity
    val from = mob.getLocation.add(0, mob.getHeight + 1, 0)
    val to = mob.getLocation

    Raytracing.startNew().origin(from).distance(to.distance(from)).step(0.2).direction(to.toVector.subtract(from.toVector))
      .eachStep(loc => loc.getWorld.spawnParticle(Particle.FIREWORKS_SPARK, loc, 1, 0, 0, 0, 0.1)).raytrace()
    dealDamage(event)
