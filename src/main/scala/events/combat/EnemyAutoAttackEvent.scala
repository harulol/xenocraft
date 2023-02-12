package dev.hawu.plugins.xenocraft
package events.combat

import data.EnemyEntity

import org.bukkit.entity.{LivingEntity, Mob, Player}
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.{Cancellable, HandlerList}

/** An event that is called when the enemy attempts to auto-attack a player.
  */
class EnemyAutoAttackEvent(entity: Mob, val enemy: EnemyEntity, val player: Player) extends EntityEvent(entity) with Cancellable:

  private var cancelled = false

  override def isCancelled = cancelled

  override def setCancelled(cancel: Boolean) = cancelled = cancel

  override def getHandlers = EnemyAutoAttackEvent.handlers

/** Object singleton for [[EnemyAutoAttackEvent]]
  */
object EnemyAutoAttackEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
