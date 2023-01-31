package dev.hawu.plugins.xenocraft
package arts.swordfighter

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.xenocraft.arts.{Art, ArtManager}
import dev.hawu.plugins.xenocraft.data.{ArtType, User}
import dev.hawu.plugins.xenocraft.utils.Formulas
import org.bukkit.*
import org.bukkit.entity.{LivingEntity, Player}

import scala.jdk.CollectionConverters.*

object GroundBeat extends Art(ArtType.GROUND_BEAT):

  ArtManager.bind(this)

  override def startAnimation(player: Player, user: User): Unit =
    player.swingMainHand()
    player.chat("Ground Beat!")

    val source = player.getLocation.add(player.getLocation.getDirection.setY(0.1).normalize().multiply(5))
      .add(0.0, 2.0, 0.0)
    val destination = source.clone().subtract(0.0, 2.0, 0.0)

    Tasks.run(runnable => {
      if source.distanceSquared(destination) <= 0.1 then
        runnable.cancel()
        Tasks.run(_ => act(player, user, destination)).plugin(Xenocraft.getInstance).run()

      source.getWorld.spawnParticle(Particle.CRIT, source, 10, 0.2, 0.2, 0.2, 0.5)
      source.subtract(0.0, 0.1, 0.0)
    }).delay(0).period(2).plugin(Xenocraft.getInstance).async(true).run()

  def act(player: Player, user: User, location: Location): Unit =
    location.getWorld.spawnParticle(Particle.EXPLOSION_HUGE, location, 3)
    location.getWorld.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1f, 1f)

    val entities = location.getWorld.getNearbyEntities(location, 3, 3, 3).asScala.filterNot(_.isInstanceOf[Player])
      .filter(_.isInstanceOf[LivingEntity]).map(_.asInstanceOf[LivingEntity])
    entities.foreach(entity => Formulas.calculateDamage(user, entity))

end GroundBeat
