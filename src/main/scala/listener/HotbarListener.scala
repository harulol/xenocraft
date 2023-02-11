package dev.hawu.plugins.xenocraft
package listener

import UserMap.user
import arts.ArtManager
import data.ArtType
import events.blades.{PlayerPostUnsheatheEvent, PlayerPreSheatheEvent}
import events.combat.PlayerAutoAttackEvent
import managers.HotbarManager

import dev.hawu.plugins.api.Tasks
import org.bukkit.entity.Player
import org.bukkit.event.inventory.{InventoryClickEvent, InventoryOpenEvent}
import org.bukkit.event.player.*
import org.bukkit.event.{EventHandler, EventPriority, Listener}
import org.bukkit.scheduler.BukkitTask

import java.util.UUID
import scala.collection.mutable

/** Additional logic on events for hotbar or arts palette during battle.
  */
object HotbarListener extends Listener:

  private val tasks = mutable.Map.empty[UUID, BukkitTask]

  // Recharge Agnian arts based on auto-attacks.
  @EventHandler
  private def onAutoAttack(event: PlayerAutoAttackEvent): Unit =
    val user = event.getPlayer.user.get
    user.masterArts.appendedAll(user.arts).filter(_ != null).filter(_.isAgnian).foreach(user.rechargeArt(_))

  // Dropping items, running commands and inventory events should be cancelled when unsheathed.
  @EventHandler
  private def onItemDrop(event: PlayerDropItemEvent): Unit =
    val user = event.getPlayer.user.get
    if user.bladeUnsheathed then event.setCancelled(true)

  @EventHandler
  private def onCommandPreprocess(event: PlayerCommandPreprocessEvent): Unit =
    val user = event.getPlayer.user.get
    if user.bladeUnsheathed then event.setCancelled(true)

  @EventHandler
  private def onInventoryEvent(event: InventoryOpenEvent): Unit = event.getPlayer match
    case player: Player => if player.user.exists(_.bladeUnsheathed) then event.setCancelled(true)
    case _              => ()

  @EventHandler
  private def onInventoryClick(event: InventoryClickEvent): Unit = event.getWhoClicked match
    case player: Player => if player.user.exists(_.bladeUnsheathed) then event.setCancelled(true)
    case _              => ()

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  private def onUnsheathe(event: PlayerPostUnsheatheEvent): Unit = startTask(event.getPlayer)

  private def startTask(player: Player): Unit =
    removeTask(player)
    tasks += player.getUniqueId -> Tasks.run(_ => HotbarManager.applyHotbar(player)).delay(0).period(1).run()
    player.user.get.startKevesiCooldown()

  @EventHandler
  private def onSwapHands(event: PlayerSwapHandItemsEvent): Unit =
    if event.getPlayer.user.exists(_.bladeUnsheathed) then event.setCancelled(true)

  // Arts activation.
  @EventHandler
  private def onSlotChange(event: PlayerItemHeldEvent): Unit =
    val user = event.getPlayer.user.get
    if user.bladeUnsheathed then
      event.setCancelled(true)
      event.getNewSlot match
        case 2 => trySingleArt(event.getPlayer, user.masterArts(0))
        case 3 => trySingleArt(event.getPlayer, user.masterArts(1))
        case 4 => trySingleArt(event.getPlayer, user.masterArts(2))
        case 5 => tryFusion(event.getPlayer, 0)
        case 6 => tryFusion(event.getPlayer, 1)
        case 7 => tryFusion(event.getPlayer, 2)
        case 8 => trySingleArt(event.getPlayer, user.talentArt.orNull)
        case _ => ()

  private def trySingleArt(player: Player, art: ArtType): Unit =
    val user = player.user.get

    // Single art will be used if it's not on cooldown or the user's not in backswing.
    if user.isInAnimation || art == null || user.isOnCooldown(art) then return ()
    ArtManager.useSingleArt(player, art)

  private def tryFusion(player: Player, index: Int): Unit =
    val user = player.user.get
    val classArt = user.arts(index)
    val masterArt = user.masterArts(index)

    // Fusion fails completely if the class art is not off cooldown.
    if classArt == null || user.isOnCooldown(classArt) then return ()

    // Fusion art can not be used if the master art was not bound.
    if masterArt == null || user.isOnCooldown(masterArt) then
      ArtManager.useSingleArt(player, classArt)
      return ()

    // If both are off cooldown, and player is sneaking. It will be a fusion art.
    if player.isSneaking then ArtManager.useFusionArt(player, classArt, masterArt) else ArtManager.useSingleArt(player, classArt)

  @EventHandler
  private def onQuit(event: PlayerQuitEvent): Unit = removeTask(event.getPlayer)

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  private def onSheathe(event: PlayerPreSheatheEvent): Unit = removeTask(event.getPlayer)

  private def removeTask(player: Player): Unit =
    tasks.remove(player.getUniqueId).foreach(_.cancel())
    player.user.get.resetCooldowns()
    player.user.get.stopKevesiCooldown()
