package dev.hawu.plugins.xenocraft
package managers

import listener.ChatHologramListener
import utils.Hologram

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.api.events.Events
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

import java.util.UUID
import scala.collection.mutable

/** Represents the manager for spawning holograms needed for chat messages.
 */
object ChatHologramManager extends Initializable:

  private val chatHolograms = mutable.Map.empty[UUID, Hologram]
  private var task: Option[BukkitTask] = None

  override def setUp(pl: JavaPlugin) =
    task = Some(Tasks.run(_ => chatHolograms.keys.foreach(teleport)).period(1).delay(0).plugin(pl).run())
    Events.registerEvents(pl, ChatHologramListener)

  /** Teleports the [[player]]'s current chat holograms.
   */
  def teleport(uuid: UUID): Unit =
    val player = Bukkit.getPlayer(uuid)

    // Despawn if somehow invalid?
    if player == null || !player.isOnline || !chatHolograms.contains(uuid) then
      chatHolograms.remove(uuid).foreach(_.despawn())
      return ()

    val location = player.getLocation.add(0.0, 3.0, 0.0)
    chatHolograms.get(player.getUniqueId).foreach(_.move(location))

  override def tearDown(pl: JavaPlugin) =
    task.foreach(_.cancel())
    task = None
    chatHolograms.values.foreach(_.despawn())
    chatHolograms.clear()

  /** Simulates that a chat event has been fired in case of any cancellations for other reasons by other plugins.
   *
   * This method should be called if you cancel the AsyncPlayerChatEvent to send your own message in your system, but still would like the
   * hologram to be displayed.
   *
   * @param player
   * the player to spawn the hologram for
   * @param message
   * the message to spawn
   */
  def fireEventChat(player: Player, message: String): Unit = Tasks.run(_ => spawnHologram(player, message)).run()

  /** Spawns a hologram above the [[player]] with the [[message]] provided. This is used for making floating chat messages.
   */
  def spawnHologram(player: Player, message: String): Unit =
    chatHolograms.remove(player.getUniqueId).foreach(_.despawn())

    // Each word on average is 5 letters, but I put 4 for slow readers.
    // More words, more time on the hologram.
    // Since 1 word takes roughly 0.5s to read for slow readers, which means 10 ticks per word.
    // Add another 3s (60 ticks) offset for others to react to it first.
    val lines = mutable.ArrayBuffer(message)
    val averageWords = message.split(" ").mkString("").grouped(4).size
    val hologram = Hologram(player.getLocation.add(0.0, 3.0, 0.0), lines, 60 + 10 * averageWords)
    hologram.spawn()

    chatHolograms.put(player.getUniqueId, hologram)

  /** Clears the hologram for the [[player]].
   */
  def clearHologram(player: Player): Unit = chatHolograms.remove(player.getUniqueId).foreach(_.despawn())
