package dev.hawu.plugins.xenocraft

import data.User

import dev.hawu.plugins.api.misc.ConsoleLogger
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.{Bukkit, OfflinePlayer}

import java.io.File
import java.util.UUID
import scala.collection.mutable

/** The data holders for players.
  */
object UserMap extends Initializable with Listener:

  private val map = mutable.Map.empty[UUID, User]

  /** Initializes the user map.
    */
  override def setUp(pl: JavaPlugin): Unit =
    val folder = File(pl.getDataFolder, "users")
    val files = folder.listFiles()
    if files != null then
      files.foreach { file =>
        try {
          val config = YamlConfiguration.loadConfiguration(file)
          val user = config.get("data").asInstanceOf[User]
          map.put(user.uuid, user)
        } catch
          case e: Exception =>
            e.printStackTrace()
            ConsoleLogger.severef(pl, s"Couldn't properly load ${file.getName}: ${e.getMessage}")
      }

    Bukkit.getOfflinePlayers.map(_.getUniqueId).filterNot(map.contains).foreach { u => map += u -> User(u) }
    pl.getServer.getPluginManager.registerEvents(this, pl)

  /** Saves all users.
    */
  override def tearDown(pl: JavaPlugin): Unit =
    val folder = File(pl.getDataFolder, "users")
    if !folder.exists() then folder.mkdirs()
    map.foreach((uuid, user) =>
      try {
        val file = File(folder, s"$uuid.yml")
        if !file.exists() then file.createNewFile()

        val config = YamlConfiguration.loadConfiguration(file)
        config.set("data", user)
        config.save(file)
      } catch
        case e: Exception =>
          e.printStackTrace()
          ConsoleLogger.severef(pl, s"Couldn't properly save $uuid.yml: ${e.getMessage}"),
    )

  @EventHandler
  private def onJoin(event: PlayerJoinEvent): Unit =
    if !map.contains(event.getPlayer.getUniqueId) then map += event.getPlayer.getUniqueId -> User(event.getPlayer.getUniqueId)

  extension (player: OfflinePlayer) {

    /** Retrieves the user data of the player.
      *
      * @return
      *   the user data
      */
    def user: Option[User] = map.get(player.getUniqueId)

  }

end UserMap
