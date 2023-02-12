package dev.hawu.plugins.xenocraft
package listener

import data.User
import events.EntityHealthChangeEvent
import events.combat.{EnemyDamagePlayerEvent, PlayerDealDamageEvent}
import managers.{ChatHologramManager, EnemyManager}
import utils.Hologram

import dev.hawu.plugins.api.Tasks
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.player.{AsyncPlayerChatEvent, PlayerQuitEvent}
import org.bukkit.event.{EventHandler, EventPriority, Listener}

import scala.collection.mutable.ArrayBuffer

/** Listener dedicated to turn chat messages into floating holograms above their head or floating damage holograms.
  */
object ChatHologramListener extends Listener:

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  def onEntityDamagePlayer(event: EnemyDamagePlayerEvent): Unit =
    val eyeLocation = event.player.getEyeLocation
    val finalDamage = event.finalDamage.round

    if event.isEvaded then Hologram.spawnAround(eyeLocation, 40, "&fEvaded!")
    else if event.isBlocked then Hologram.spawnAround(eyeLocation, 40, s"&7$finalDamage")
    else if !event.isHit then Hologram.spawnAround(eyeLocation, 40, "&fMissed!")
    else if event.isCritical then Hologram.spawnAround(eyeLocation, 40, s"&e✦&f$finalDamage&e✦")
    else Hologram.spawnAround(eyeLocation, 40, s"&f$finalDamage")

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private def onHealthChange(event: EntityHealthChangeEvent): Unit =
    val loc = Bukkit.getEntity(event.attributable.uuid).asInstanceOf[LivingEntity].getEyeLocation
    val change = (event.newHp - event.oldHp).intValue
    if !event.attributable.isInstanceOf[User] || change < 0 then return ()
    Hologram.spawnAround(loc, 60, s"&a$change")

  @EventHandler
  private def onPlayerQuit(event: PlayerQuitEvent): Unit = ChatHologramManager.clearHologram(event.getPlayer)

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private def onAsyncChat(event: AsyncPlayerChatEvent): Unit =
    if event.isAsynchronous then Tasks.run(_ => ChatHologramManager.spawnHologram(event.getPlayer, event.getMessage)).run()
    else ChatHologramManager.spawnHologram(event.getPlayer, event.getMessage)

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private def onPlayerDamageEntity(event: PlayerDealDamageEvent): Unit =
    val eyeLocation = event.entity.entity.getEyeLocation
    val finalDamage = event.finalDamage.round
    EnemyManager.syncBossbar(event.entity.entity)

    if event.isEvaded then Hologram.spawnAround(eyeLocation, 40, "&fEvaded!")
    else if !event.isHit then Hologram.spawnAround(eyeLocation, 40, "&fMissed!")
    else if event.isBlocked then Hologram.spawnAround(eyeLocation, 40, s"&7&l$finalDamage")
    else if event.isPreemptive then Hologram.spawnAround(eyeLocation, 40, s"&e&l$finalDamage")
    else if event.isCritical then Hologram.spawnAround(eyeLocation, 40, s"&e&l✦&f&l$finalDamage&e&l✦")
    else Hologram.spawnAround(eyeLocation, 40, s"&f&l$finalDamage")
