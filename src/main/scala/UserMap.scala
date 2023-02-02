package dev.hawu.plugins.xenocraft

import dev.hawu.plugins.api.misc.ConsoleLogger
import dev.hawu.plugins.xenocraft.data.User
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
object UserMap extends Listener:

  private val map = mutable.Map.empty[UUID, User]

  /** Initializes the user map.
    *
    * @param pl
    *   the plugin
    */
  def initialize(pl: JavaPlugin): Unit =
    val folder = File(pl.getDataFolder, "users")
    val files = folder.listFiles()
    if files != null then
      files.foreach { file =>
        try {
          val config = YamlConfiguration.loadConfiguration(file)
          val user = config.get("data").asInstanceOf[User]
          map.put(user.uuid, user)
          user.applyClass(user.cls)
        } catch
          case e: Exception =>
            e.printStackTrace()
            ConsoleLogger.severef(pl, "Couldn't properly load %s: %s", file.getName, e.getMessage)
      }

    Bukkit.getOfflinePlayers.filter(p => !map.contains(p.getUniqueId)).foreach { p =>
      map.put(p.getUniqueId, User(p.getUniqueId))
    }

  end initialize

  /** Saves all users.
    *
    * @param pl
    *   the plugin
    */
  def save(pl: JavaPlugin): Unit =
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
          ConsoleLogger.severef(pl, "Couldn't properly save %s.yml: %s", uuid.toString, e.getMessage),
    )

  end save

  @EventHandler
  private def onJoin(event: PlayerJoinEvent): Unit = if !map.contains(event.getPlayer.getUniqueId) then
    map.put(event.getPlayer.getUniqueId, User(event.getPlayer.getUniqueId))

  extension (player: OfflinePlayer) {

    /** Retrieves the user data of the player.
      *
      * @return
      *   the user data
      */
    def user: Option[User] = map.get(player.getUniqueId)

  }

end UserMap
