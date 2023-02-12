package dev.hawu.plugins.xenocraft
package events.combat

import events.combat.EnemyAutoAttackSuccessEvent.handlers

import org.bukkit.entity.Mob
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityEvent

/** Calls when an enemy has landed an attack successfully, via an auto-attack.
 */
class EnemyAutoAttackSuccessEvent(who: Mob) extends EntityEvent(who):

  override def getHandlers: HandlerList = handlers

object EnemyAutoAttackSuccessEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
