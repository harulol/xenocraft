package dev.hawu.plugins.xenocraft
package arts.zephyr

import dev.hawu.plugins.xenocraft.arts.Art
import dev.hawu.plugins.xenocraft.data.ArtType
import dev.hawu.plugins.xenocraft.arts.ArtManager
import org.bukkit.entity.Player
import dev.hawu.plugins.xenocraft.data.User
import dev.hawu.plugins.xenocraft.data.Directional
import dev.hawu.plugins.xenocraft.data.EnemyEntity
import dev.hawu.plugins.xenocraft.events.PlayerDealDamageEvent
import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.xenocraft.combat.CombatManager
import dev.hawu.plugins.api.MathUtils
import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.particles.ParticleEffect
import dev.hawu.plugins.api.particles.ParticleEnum
import dev.hawu.plugins.api.particles.PredefParticles
import scala.jdk.CollectionConverters.*
import org.bukkit.Bukkit
import org.bukkit.Sound

object WideSlash extends Art(ArtType.WIDE_SLASH):

  private def dealDamage(event: PlayerDealDamageEvent, times: Int): Unit =
    val mob = event.entity.entity
    val direction = mob.getLocation().getDirection()
    val vector = if times == 1 then MathUtils.getLeftUnit(direction) else MathUtils.getRightUnit(direction)

    val start = mob.getEyeLocation().add(vector).add(0.0, 0.5, 0.0)
    val end = mob.getEyeLocation().add(vector.clone().multiply(-1)).add(0.0, -1.0, 0.0)
    val particle = ParticleEffect.of(ParticleEnum.FIREWORKS_SPARK).longDistance(true).offset(0, 0, 0)
      .setParticleCount(0).setParticleData(0).build()
    PredefParticles.drawLine(start, end, 0.2, particle, Bukkit.getOnlinePlayers().asScala.toList.asJava)
    mob.getWorld().playSound(mob.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f)
    CombatManager.dealDamage(event)

  override def use(player: Player, user: User, fusion: Boolean): Boolean =
    val entity = getEnemiesFront(player).headOption.orNull
    if entity == null then return false

    val locale = UserAdapter.getAdapter().getUser(player).getLocale()
    player.sendTitle(" ", "§b" + artType.name(locale), 0, 30, 0)

    user.isEvading = true
    user.isInAnimation = true
    val event = getEvent(player, entity, fusion, artCritMod = -0.8)

    Tasks.run(_ => dealDamage(event, 1)).delay(5).run()
    Tasks.run(_ => dealDamage(event, 2)).delay(8).run()
    Tasks.run(_ => {
      user.isInAnimation = false
      user.isEvading = false
    }).async(true).delay(30).run()

    true

  end use

end WideSlash
