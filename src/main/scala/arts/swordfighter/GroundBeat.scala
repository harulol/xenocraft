package dev.hawu.plugins.xenocraft
package arts.swordfighter

import arts.Art
import data.{ArtFusionBonus, ArtType, User}
import utils.Configuration

import dev.hawu.plugins.api.Tasks
import org.bukkit.{Color, Particle, Sound, SoundCategory}
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Player

/** Ground Beat (Swordfighter)
  */
object GroundBeat extends Art(ArtType.GROUND_BEAT):

  override def use(player: Player, user: User, fusion: Boolean, master: Boolean): Boolean =
    val range = if fusion then 4 * Configuration.fusionBonus(ArtFusionBonus.RANGE) else 4
    val enemies = getEnemiesFront(player, range)
    if enemies.isEmpty then return false

    val mainDamage = getEvent(player).setMaster(master).targeting(enemies.head).artType(artType).build
    val aoeDamages = enemies.tail.map(getEvent(player).setMaster(master).targeting(_).artType(artType).setAoE(true).build)

    val startLocation = player.getLocation.add(player.getLocation.getDirection.multiply(range)).add(0, 3, 0)
    var distanceCovered = 0.0

    startLocation.getWorld.playSound(startLocation, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.BLOCKS, 1, 1)
    Tasks.run(runnable => {
      if distanceCovered > 3 then
        runnable.cancel()
        aoeDamages.appended(mainDamage).foreach(dealDamage)
        startLocation.getWorld.spawnParticle(Particle.EXPLOSION_HUGE, startLocation, 3, 0, 0, 0, 0)
        startLocation.getWorld.playSound(startLocation, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1, 1)
      else
        startLocation.getWorld.spawnParticle(Particle.REDSTONE, startLocation, 1, 0, 0, 0, 0.1, DustOptions(Color.BLUE, 1))
        startLocation.subtract(0, 0.1, 0)
        distanceCovered += 0.1
    }).delay(0).period(1).plugin(Xenocraft.getInstance).run()
    true
