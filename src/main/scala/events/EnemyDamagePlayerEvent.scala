package dev.hawu.plugins.xenocraft
package events

import org.bukkit.entity.Player
import dev.hawu.plugins.xenocraft.data.EnemyEntity
import org.bukkit.event.entity.EntityEvent
import org.bukkit.entity.Mob
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import java.util.concurrent.ThreadLocalRandom
import dev.hawu.plugins.xenocraft.utils.Formulas
import dev.hawu.plugins.xenocraft.UserMap.user
import dev.hawu.plugins.xenocraft.data.ArtReaction

/** Represents an event where an enemy damages a player.
  *
  * This should be much much more simplier.
  *
  * @param entity
  * @param enemy
  * @param player
  */
class EnemyDamagePlayerEvent(entity: Mob, val enemy: EnemyEntity, val player: Player)
  extends EntityEvent(entity) with Cancellable:

  private val random = ThreadLocalRandom.current()
  private val user = player.user.get
  private val criticalHit = Formulas.canCrit(enemy)
  private val blockedHit = Formulas.canBlock(user)
  private val landedHit = Formulas.canHit(enemy, user)

  val critMultiplier = if isCritical then 1.25 + enemy.critDamage else 1.0
  val comboMultiplier = if user.reaction.contains(ArtReaction.LAUNCH) then 1.5 else 1.0
  val blockedMultiplier = if isBlocked then 1 - user.flatBlockStrength else 1.0
  val randomMultiplier = ThreadLocalRandom.current().nextDouble(0.9, 1.1)
  var damageReduction = 0.0
  var isEvaded = user.isEvading
  var isHit = landedHit
  var shouldOverride = true

  private var cancelled = false

  /** Checks if this damage event is a critical hit.
    *
    * @return
    *   true if the damage event is a critical hit, false otherwise
    */
  def isCritical: Boolean = criticalHit

  /** Checks if this damage event is a blocked hit.
    *
    * @return
    *   true if the damage event is a blocked hit, false otherwise
    */
  def isBlocked: Boolean = blockedHit

  /** Calculates the total damage of this event.
    *
    * @return
    *   the total damage
    */
  def totalDamage: Double = enemy.attack * (1 - damageReduction) * critMultiplier * comboMultiplier *
    blockedMultiplier * randomMultiplier

  /** Calculates the final damage of this event.
    *
    * @return
    *   the final damage
    */
  def finalDamage: Double = totalDamage min 9999999 max 0

  override def getHandlers(): HandlerList = EnemyDamagePlayerEvent.handlers

  override def isCancelled(): Boolean = cancelled

  override def setCancelled(cancel: Boolean): Unit = cancelled = cancel

end EnemyDamagePlayerEvent

object EnemyDamagePlayerEvent:
  private val handlers = HandlerList()

  def getHandlerList(): HandlerList = handlers
