package dev.hawu.plugins.xenocraft
package listener

import I18n.tl
import UserMap.user
import data.Directional
import events.PlayerIncapacitateEvent
import events.combat.{EnemyAutoAttackEvent, EnemyDamagePlayerEvent, PlayerAutoAttackEvent, PlayerDealDamageEvent}
import managers.*
import utils.Hologram

import dev.hawu.plugins.api.events.Events
import dev.hawu.plugins.api.{MathUtils, Tasks}
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.event.entity.*
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason
import org.bukkit.event.player.{PlayerInteractEvent, PlayerQuitEvent}
import org.bukkit.event.{EventHandler, EventPriority, Listener}
import org.bukkit.plugin.java.JavaPlugin

import java.util.UUID
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters.*

/** The singleton object dedicated to listening for events related to fields.
  */
object BattlefieldListener extends Listener:

  // Handling for other types of damage.
  @EventHandler
  private def onEntityDamage(event: EntityDamageEvent): Unit = event.getEntity match
    case player: Player =>
      if event.getCause == DamageCause.CUSTOM || event.getCause == DamageCause.ENTITY_ATTACK then return () else event.setCancelled(true)

      val user = player.user.get
      val percentage = event.getFinalDamage / getMaxHealth(player)
      CombatManager.damage(user, user.maxHp * percentage)

      player.playEffect(EntityEffect.HURT)
      player.setNoDamageTicks(20)
    case _: Mob => event.setCancelled(true)
    case _ => ()

  private def getMaxHealth(entity: LivingEntity): Double = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue

  @EventHandler
  private def onEntityDamageEntity(event: EntityDamageByEntityEvent): Unit = event.getDamager match
    case player: Player if !player.user.get.bladeUnsheathed && event.getEntity.isInstanceOf[Mob] && !event.getEntity.isDead =>
      // Unsheathe if player has not done and clicking at entity.
      val mob = event.getEntity.asInstanceOf[Mob]
      player.user.foreach(_.unsheathe())
      mob.setTarget(player)
      AggroManager.map(mob, player)
      EnemyManager.getBossbar(mob).foreach(_.addPlayer(player))
      event.setCancelled(true)

    case player: Player if player.user.get.bladeUnsheathed && event.getEntity.isInstanceOf[Mob] && !event.getEntity.isDead =>
      event.setCancelled(true)

      val mob = event.getEntity.asInstanceOf[Mob]
      val entity = EnemyManager.getEnemy(mob).get
      mob.setTarget(player)
      if BattlefieldManager.callAutoAttackEvent(player, mob, true) then return ()

      val damageEvent = new PlayerDealDamageEvent(player, BattlefieldManager.calculateDirection(player, mob), entity, true)
      Bukkit.getPluginManager.callEvent(damageEvent)

      if !damageEvent.isCancelled && !damageEvent.isEvaded && damageEvent.isHit then CombatManager.damage(entity, damageEvent.finalDamage)

    case mob: Mob if event.getEntity.isInstanceOf[Player] =>
      event.setCancelled(true)

      val player = event.getEntity.asInstanceOf[Player]
      val enemy = EnemyManager.getEnemy(mob).get
      if BattlefieldManager.callAutoAttackEvent(player, mob, false) then return ()

      val damageEvent = EnemyDamagePlayerEvent(mob, enemy, player)
      Bukkit.getPluginManager.callEvent(damageEvent)

      if !damageEvent.isCancelled && !damageEvent.isEvaded && damageEvent.isHit then
        CombatManager.damage(player.user.get, damageEvent.finalDamage)
