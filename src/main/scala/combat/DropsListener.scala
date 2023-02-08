package dev.hawu.plugins.xenocraft
package combat

import I18n.tl
import UserMap.user
import events.{PlayerSheatheEvent, PlayerUnsheatheEvent}

import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerPickupArrowEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.inventory.ItemStack

import java.util.UUID
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/** The listener for handling entity drops when the player goes into combat mode.
 */
object DropsListener extends Listener:

  private val drops = mutable.Map.empty[UUID, ArrayBuffer[ItemStack]]

  @EventHandler
  private def onSheathe(event: PlayerSheatheEvent): Unit =
    val items = drops.remove(event.getPlayer.getUniqueId).getOrElse(ArrayBuffer.empty)
    val leftover = ArrayBuffer.empty[ItemStack]

    // Return all drops picked up.
    items.foreach(item =>
      val map = event.getPlayer.getInventory.addItem(item)
      map.values().forEach(leftover.addOne),
    )

  @EventHandler
  private def onUnsheathe(event: PlayerUnsheatheEvent): Unit =
    event.getPlayer.tl("unsheathed")
    if drops.contains(event.getPlayer.getUniqueId) then drops(event.getPlayer.getUniqueId) = ArrayBuffer.empty[ItemStack]

  @EventHandler
  private def onPickupArrow(event: PlayerPickupArrowEvent): Unit =
    val player = event.getPlayer
    if player.user.exists(_.bladeUnsheathed) then handlePickup(event, player)

  private def handlePickup(event: EntityPickupItemEvent | PlayerPickupArrowEvent, player: Player) =
    val items = drops.getOrElseUpdate(player.getUniqueId, ArrayBuffer.empty)
    event.setCancelled(true)
    val item = event match
      case event: PlayerPickupArrowEvent => event.getItem
      case event: EntityPickupItemEvent => event.getItem
    items += item.getItemStack
    item.remove()

  @EventHandler
  private def onPickup(event: EntityPickupItemEvent): Unit = event.getEntity match
    case player: Player if player.user.exists(!_.bladeUnsheathed) => handlePickup(event, player)
    case _ => ()

end DropsListener
