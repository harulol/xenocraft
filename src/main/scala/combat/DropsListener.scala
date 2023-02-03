package combat

import org.bukkit.event.Listener
import scala.collection.mutable
import org.bukkit.inventory.ItemStack
import java.util.UUID
import org.bukkit.event.EventHandler
import scala.collection.mutable.ArrayBuffer
import dev.hawu.plugins.xenocraft.events.PlayerUnsheatheEvent
import dev.hawu.plugins.xenocraft.events.PlayerSheatheEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.entity.Player
import dev.hawu.plugins.xenocraft.UserMap.user
import org.bukkit.event.player.PlayerPickupArrowEvent

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
  private def onUnsheathe(event: PlayerUnsheatheEvent): Unit = if drops.contains(event.getPlayer().getUniqueId()) then
    drops(event.getPlayer().getUniqueId()) = ArrayBuffer.empty[ItemStack]

  @EventHandler
  private def onPickupArrow(event: PlayerPickupArrowEvent): Unit =
    val player = event.getPlayer()
    if player.user.exists(!_.bladeUnsheathed) then return

    val items = drops.getOrElseUpdate(player.getUniqueId, ArrayBuffer.empty)
    event.setCancelled(true)
    items += event.getItem.getItemStack
    event.getItem.remove()

  @EventHandler
  private def onPickup(event: EntityPickupItemEvent): Unit = if event.getEntity().isInstanceOf[Player] then
    val player = event.getEntity().asInstanceOf[Player]
    if player.user.exists(!_.bladeUnsheathed) then return

    val items = drops.getOrElseUpdate(player.getUniqueId, ArrayBuffer.empty)
    event.setCancelled(true)
    items += event.getItem.getItemStack
    event.getItem.remove()

end DropsListener
