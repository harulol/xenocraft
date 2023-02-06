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

/** The wide slash art.
  */
object WideSlash extends Art(ArtType.WIDE_SLASH):

  private def dealDamage(event: PlayerDealDamageEvent, times: Int): Unit = CombatManager.dealDamage(event)

  override def use(player: Player, user: User, fusion: Boolean): Boolean =
    val entity = getEnemiesFront(player).headOption.orNull
    if entity == null then return false

    user.isEvading = true
    user.isInAnimation = true
    val event = getEvent(player, entity, fusion, artCritMod = -0.8)

    Tasks.run(_ => dealDamage(event, 1)).delay(5).run()
    Tasks.run(_ => dealDamage(event, 2)).delay(25).run()
    Tasks.run(_ => {
      user.isInAnimation = false
      user.isEvading = false
    }).async(true).delay(30).run()

    true

end WideSlash
