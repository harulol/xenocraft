package dev.hawu.plugins.xenocraft
package arts.zephyr

import dev.hawu.plugins.xenocraft.arts.Art
import dev.hawu.plugins.xenocraft.data.ArtType
import org.bukkit.entity.Player
import dev.hawu.plugins.xenocraft.data.User
import dev.hawu.plugins.xenocraft.data.Character
import dev.hawu.plugins.api.adapters.UserAdapter
import org.bukkit.entity.Mob
import java.util.concurrent.ThreadLocalRandom
import dev.hawu.plugins.api.particles.ParticleEnum
import dev.hawu.plugins.api.particles.ParticleEffect
import dev.hawu.plugins.api.particles.PredefParticles
import org.bukkit.Bukkit
import org.bukkit.Sound
import scala.jdk.CollectionConverters.*
import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.xenocraft.combat.CombatManager
import dev.hawu.plugins.xenocraft.events.PlayerDealDamageEvent

object AirFang extends Art(ArtType.AIR_FANG):

  private def drawSlash(entity: Mob, event: PlayerDealDamageEvent): Unit =
    val random = ThreadLocalRandom.current()
    val start = entity.getLocation().add(0.0, entity.getHeight() / 2, 0.0)
      .add(random.nextGaussian(), random.nextGaussian(), random.nextGaussian())
    val direction = entity.getEyeLocation().subtract(start).toVector().normalize()
    val end = start.clone().add(direction.multiply(1.5))

    val particle = ParticleEffect.of(ParticleEnum.FIREWORKS_SPARK).longDistance(true).offset(0, 0, 0)
      .setParticleCount(0).setParticleData(0).build()
    PredefParticles.drawLine(start, end, 0.2, particle, Bukkit.getOnlinePlayers().asScala.toList.asJava)
    entity.getWorld().playSound(entity.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f)
    CombatManager.dealDamage(event)

  override def use(player: Player, user: User, fusion: Boolean): Boolean =
    val entity = getEnemiesFront(player).headOption.orNull
    if entity == null then return false

    val locale = UserAdapter.getAdapter().getUser(player).getLocale()
    player.sendTitle(" ", "§b" + artType.name(locale), 0, 30, 0)
    val event = getEvent(player, entity, fusion)
    if entity.entity.getTarget() != null && entity.entity.getTarget().getUniqueId().equals(user.uuid) then
      event.damageBonus1 += 1.0
    if user.char.contains(Character.MIO) then event.hits = 3

    for i <- 1 to event.hits do Tasks.run(_ => drawSlash(entity.entity, event)).delay(i * 10).run()
    true

end AirFang
