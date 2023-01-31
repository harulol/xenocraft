package dev.hawu.plugins.xenocraft
package combat

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.xenocraft.combat.Hologram
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.{AsyncPlayerChatEvent, PlayerQuitEvent}
import org.bukkit.event.{EventHandler, EventPriority, Listener}
import org.bukkit.plugin.java.JavaPlugin

import java.util.UUID
import scala.collection.mutable

/** Listener dedicated to turn chat messages into floating holograms above their head.
  */
object ChatHologramListener extends Listener:

  private val map = mutable.Map.empty[UUID, Hologram]

  /** Initializes the listener with the plugin.
    *
    * @param pl
    *   the plugin
    */
  def initialize(pl: JavaPlugin): Unit = pl.getServer.getScheduler.runTaskTimer(pl, _ => map.foreach(teleport), 0, 1)

  private def teleport(entry: (UUID, Hologram)): Unit = Tasks.run { _ =>
    val player = Bukkit.getPlayer(entry._1)
    if player != null && entry._2.isSpawned then entry._2.move(player.getLocation.add(0.0, 3.0, 0.0))
  }.run()

  /** Simulates that a chat event has been fired in case of any cancellations for other reasons by other plugins.
    *
    * This method should be called if you cancel the AsyncPlayerChatEvent to send your own message in your system, but
    * still would like the hologram to be displayed.
    *
    * @param player
    *   the player to spawn the hologram for
    * @param message
    *   the message to spawn
    */
  def fireEventChat(player: Player, message: String): Unit = Tasks.run(_ => spawnHologram(player, message)).run()

  private def spawnHologram(player: Player, message: String): Unit =
    map.remove(player.getUniqueId).foreach(_.despawn())

    // Each word on average is 5 letters, but I put 4 for slow readers.
    // More words, more time on the hologram.
    // Since 1 word takes roughly 0.5s to read for slow readers, which means 10 ticks per word.
    // Add another 3s (60 ticks) offset for others to react to it first.
    val lines = mutable.ArrayBuffer(message)
    val averageWords = message.split(" ").mkString("").grouped(4).size
    val hologram = Hologram(player.getLocation.add(0.0, 3.0, 0.0), lines, 60 + 10 * averageWords)
    hologram.spawn()

    map.put(player.getUniqueId, hologram)

  @EventHandler
  private def onPlayerQuit(event: PlayerQuitEvent): Unit = map.remove(event.getPlayer.getUniqueId).foreach(_.despawn())

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private def onAsyncChat(event: AsyncPlayerChatEvent): Unit =
    if !event.isAsynchronous then spawnHologram(event.getPlayer, event.getMessage)
    else Tasks.run(_ => spawnHologram(event.getPlayer, event.getMessage)).run()

end ChatHologramListener
