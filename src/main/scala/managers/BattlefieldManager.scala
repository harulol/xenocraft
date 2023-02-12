package dev.hawu.plugins.xenocraft
package managers

import UserMap.user
import data.Directional
import events.combat.{EnemyAutoAttackEvent, EnemyAutoAttackSuccessEvent, PlayerAutoAttackEvent, PlayerAutoAttackSuccessEvent}
import listener.BattlefieldListener

import dev.hawu.plugins.api.events.Events
import dev.hawu.plugins.api.{MathUtils, Tasks}
import org.bukkit.Bukkit
import org.bukkit.entity.{Mob, Player}
import org.bukkit.plugin.java.JavaPlugin

import scala.jdk.CollectionConverters.*

/** Represents a singleton object dedicated to managing attacking and damaging on the battlegrounds.
 */
object BattlefieldManager extends Initializable:

  override def setUp(pl: JavaPlugin) =
    Events.registerEvents(pl, BattlefieldListener)
    Tasks.run(_ => healAll()).plugin(pl).delay(20).period(20).run()
    Tasks.run(_ => sheatheAll()).plugin(pl).delay(0).period(5).run()

  /** Heals all players that are not in battle or have blades unsheathed.
   */
  def healAll(): Unit = Bukkit.getOnlinePlayers.asScala.filterNot(AggroManager.isBeingTargeted).flatMap(_.user).filterNot(_.bladeUnsheathed)
    .filter(u => u.hp.round < u.maxHp.round).foreach(user => user.setHp(user.maxHp * 0.1 + user.hp))

  /** Sheathes all players' blades if they are not being targeted.
   */
  def sheatheAll(): Unit = Bukkit.getOnlinePlayers.asScala.filterNot(AggroManager.isBeingTargeted).flatMap(_.user).foreach(_.sheathe())

  /** Calls an auto attack event and returns if the event is cancelled.
   */
  def callAutoAttackEvent(player: Player, mob: Mob, fromPlayer: Boolean): Boolean =
    val enemyEntity = EnemyManager.getEnemy(mob).get

    val autoAttack = if fromPlayer then PlayerAutoAttackEvent(player, enemyEntity) else EnemyAutoAttackEvent(mob, enemyEntity, player)
    Bukkit.getPluginManager.callEvent(autoAttack)
    autoAttack.isCancelled

  /** Calls an auto attack success event.
   */
  def callAutoAttackSuccessEvent(player: Player, mob: Mob, fromPlayer: Boolean): Unit =
    val event = if fromPlayer then PlayerAutoAttackSuccessEvent(player) else EnemyAutoAttackSuccessEvent(mob)
    Bukkit.getPluginManager.callEvent(event)

  /** Calculates the direction which the [[player]] is facing [[target]]. This is more like, relative direction, such as a player looking at
   * the [[target]]'s left will yield [[Directional.LEFT]].
   */
  def calculateDirection(player: Player, target: Mob): Directional =
    // Don't care about the altitude difference.
    val direction = player.getLocation().getDirection.setY(0)
    val enemyDirection = target.getLocation().getDirection.setY(0)

    val angle = enemyDirection.angle(direction)
    if angle <= math.toRadians(45) then Directional.BACK // ranges from 45 -> 0, 0 -> -45
    else if angle >= math.toRadians(135) then Directional.FRONT // ranges from 135 -> 180 and 180 -> -135
    else
      val left = MathUtils.getLeftUnit(enemyDirection).angle(direction)
      val right = MathUtils.getRightUnit(enemyDirection).angle(direction)
      if left < right then Directional.LEFT else Directional.RIGHT
