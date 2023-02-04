package dev.hawu.plugins.xenocraft
package combat

import org.bukkit.event.Listener
import scala.collection.mutable
import java.util.UUID
import dev.hawu.plugins.xenocraft.data.ArtType
import org.bukkit.scheduler.BukkitTask
import org.bukkit.event.EventHandler
import dev.hawu.plugins.xenocraft.events.PlayerSheatheEvent
import dev.hawu.plugins.xenocraft.events.PlayerUnsheatheEvent
import dev.hawu.plugins.api.Tasks
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import dev.hawu.plugins.xenocraft.UserMap.user
import dev.hawu.plugins.xenocraft.data.User
import org.bukkit.event.player.PlayerQuitEvent
import dev.hawu.plugins.api.items.ItemStackBuilder
import org.bukkit.inventory.ItemFlag
import org.bukkit.Material
import dev.hawu.plugins.xenocraft.gui.ArtsGUI
import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.xenocraft.gui.ClassesGUI

/** Represents a listener that handles arts and damaging for battles.
  *
  * Unlike [[BattlefieldListener]], this is more towards during a battle than just fields-related stuff.
  */
object HotbarManager extends Listener:

  private val cooldowns = mutable.Map.empty[UUID, mutable.Map[ArtType, Long]]
  private val tasks = mutable.Map.empty[UUID, BukkitTask]

  @EventHandler
  private def onSheathe(event: PlayerSheatheEvent): Unit = tasks.remove(event.getPlayer.getUniqueId).foreach(_.cancel)

  private def handleHotbar(player: Player, runnable: BukkitRunnable): Unit =
    if !BattlefieldListener.isInBattle(player) then
      runnable.cancel()
      return

    val user = player.user.get
    setHotbar(user, player)

  private def tryPutting(player: Player, index: Int, art: ArtType): Unit =
    val emptySpace = ItemStackBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()
    val cooldown = cooldowns.getOrElseUpdate(player.getUniqueId, mutable.Map.empty).getOrElse(art, -1L)
    val locale = UserAdapter.getAdapter().getUser(player).getLocale()

    if art == null then player.getInventory().setItem(index, emptySpace)
    else if cooldown > System.currentTimeMillis() then
      val cooldownItem = I18n
        .translateItem(art.icon -> 1, "on-cooldown", "name" -> art.name(locale))(using ArtsGUI.getModule, player)
      player.getInventory().setItem(index, cooldownItem)
    else
      val artItem = ArtsGUI.retrieveArtDisplay(art, locale, click = false)(using player)
      val artDisplay = ItemStackBuilder.from(artItem).transform(_.setUnbreakable(true))
        .flags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS).build()
      player.getInventory().setItem(index, artDisplay)

  private def putWeapons(player: Player, user: User): Unit =
    val locale = UserAdapter.getAdapter().getUser(player).getLocale()

    val weapon = I18n.translateItem(
      user.weapon.get.material -> 1,
      "weapon",
      "name" -> user.weapon.get.displayName(locale),
    )(using ClassesGUI.getModule, player)
    val weaponItem = ItemStackBuilder.from(weapon).transform(_.setUnbreakable(true))
      .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE).build()

    player.getInventory().setItem(0, weaponItem)
    player.getInventory().setItem(1, ItemStackBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build())

  /** Sets the hotbar for the player.
    *
    * @param user
    *   the user
    * @param player
    *   the player
    */
  def setHotbar(user: User, player: Player): Unit =
    putWeapons(player, user)
    (2 to 4).zip(user.masterArts).foreach((index, art) => tryPutting(player, index, art))
    (5 to 7).zip(user.arts).foreach((index, art) => tryPutting(player, index, art))
    tryPutting(player, 8, user.talentArt.orNull)

  @EventHandler
  private def onQuit(event: PlayerQuitEvent): Unit = tasks.remove(event.getPlayer.getUniqueId).foreach(_.cancel)

  @EventHandler
  private def onUnsheathe(event: PlayerUnsheatheEvent): Unit =
    tasks.remove(event.getPlayer.getUniqueId).foreach(_.cancel)
    tasks(event.getPlayer().getUniqueId()) = Tasks.run(handleHotbar(event.getPlayer, _)).plugin(Xenocraft.getInstance)
      .delay(0).period(1).run()

end HotbarManager
