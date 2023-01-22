package dev.hawu.plugins.xenocraft
package combat

import dev.hawu.plugins.api.events.Events
import dev.hawu.plugins.xenocraft.UserMap.user
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.plugin.java.JavaPlugin

import scala.jdk.CollectionConverters.*

/**
 * The singleton object dedicated to listening for events
 * related to fields.
 */
object BattlefieldListener extends Listener:

  private var plugin: Option[JavaPlugin] = None

  /**
   * Initializes the battlefield listener.
   *
   * @param pl the plugin to initialize with.
   */
  def initialize(pl: JavaPlugin): Unit =
    Events.registerEvents(pl, this)
    pl.getServer.getScheduler.runTaskTimer(pl, _ => {
      Bukkit.getOnlinePlayers.asScala
        .map(p => p -> p.user.get)
        .filter(_._2.battlefield.isEmpty)
        .filter(tup => tup._2.hp.intValue < tup._2.maxHp.intValue)
        .foreach(tuple => CombatManager.heal(tuple._1, math.floor(tuple._2.maxHp * 0.1)))
    }, 0, 20)
    plugin = Some(pl)

  @EventHandler
  private def onDamage(event: EntityDamageEvent): Unit =
    event.getEntity match
      case player: Player =>
        val user = player.user.get
        val percentage = event.getFinalDamage / player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue
        CombatManager.damage(player, user.maxHp * percentage)
      case _ => ()
