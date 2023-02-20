package dev.hawu.plugins.xenocraft
package arts.swordfighter

import arts.Art
import data.{ArtReaction, ArtType, Directional, User}
import managers.ReactionManager

import org.bukkit.entity.Player

/** Sword Strike (Swordfighter): Inflicts Break on enemy when attacking from side
  */
object SwordStrike extends Art(ArtType.SWORD_STRIKE):

  override def use(player: Player, user: User, fusion: Boolean, master: Boolean): Boolean =
    val enemy = getEnemiesFront(player).headOption.orNull
    if enemy != null then
      val damageEvent = getEvent(player).targeting(enemy).fusion(fusion).build
      if dealDamage(damageEvent) && (damageEvent.direction == Directional.LEFT || damageEvent.direction == Directional.RIGHT) then ()
        schedule(15, ReactionManager.inflict(user, enemy, ArtReaction.BREAK))
      true
    else false
