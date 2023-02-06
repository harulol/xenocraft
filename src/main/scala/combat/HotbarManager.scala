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
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerDropItemEvent
import dev.hawu.plugins.xenocraft.arts.ArtManager
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryClickEvent
import dev.hawu.plugins.xenocraft.events.PlayerDealDamageEvent
import org.bukkit.event.EventPriority
import java.text.DecimalFormat
import org.bukkit.inventory.ItemStack
import dev.hawu.plugins.api.i18n.Locale
import dev.hawu.plugins.xenocraft.events.EnemyDamagePlayerEvent
import dev.hawu.plugins.xenocraft.combat.BattlefieldListener

/** Represents a listener that handles arts and damaging for battles.
  *
  * Unlike [[BattlefieldListener]], this is more towards during a battle than just fields-related stuff.
  */
object HotbarManager extends Listener:

  private val cooldowns = mutable.Map.empty[UUID, mutable.Map[ArtType, Long]]
  private val tasks = mutable.Map.empty[UUID, BukkitTask]

  @EventHandler
  private def onSheathe(event: PlayerSheatheEvent): Unit =
    tasks.remove(event.getPlayer.getUniqueId).foreach(_.cancel)
    event.getPlayer().user.foreach { u =>
      u.resetCooldowns()
      u.stopKevesiCooldown()
    }

  private def handleHotbar(player: Player, runnable: BukkitRunnable): Unit =
    if !BattlefieldListener.isInBattle(player) then
      runnable.cancel()
      return

    val user = player.user.get
    setHotbar(user, player)

  private def getBar(percentage: Double): String =
    val builder = StringBuilder("&7[")
    val value = (percentage * 100).round.intValue / 5

    builder.append("&a&m")
    builder.append(" " * value)
    builder.append("&f&m")
    builder.append(" " * (20 - value))
    builder.append("&7] ")
    builder.append(s"&a&l${(percentage * 100).round}%")
    builder.toString()

  private def getCooldownItem(player: Player, art: ArtType, locale: Locale): ItemStack =
    val decimalFormatter = DecimalFormat("#,###.#")
    val cooldown = if art.isKevesi then decimalFormatter.format(art.cooldown) else art.cooldown.round
    val user = player.user.get
    val guage = decimalFormatter.format(if art.isKevesi then user.getCooldown(art) / 20 else user.getCooldown(art))
    val placeholder =
      if art.isTalent then ("talent", "action") else if art.isKevesi then ("keves", "time") else ("agnus", "auto")

    I18n.translateItem(
      Material.GHAST_TEAR -> 1,
      s"on-cooldown-${placeholder(0)}",
      "name" -> art.name(locale),
      placeholder(1) -> guage,
      "cooldown" -> cooldown,
      "bar" -> getBar((if art.isKevesi then user.getCooldown(art) / 20 else user.getCooldown(art)) / art.cooldown),
    )(using ArtsGUI.getModule, player)

  private def tryPutting(player: Player, index: Int, art: ArtType): Unit =
    if !BattlefieldListener.isInBattle(player) then return

    val emptySpace = ItemStackBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()
    val locale = UserAdapter.getAdapter().getUser(player).getLocale()
    val user = player.user.get

    if art == null then player.getInventory().setItem(index, emptySpace)
    else if user.isOnCooldown(art) then
      val cooldownItem = getCooldownItem(player, art, locale)
      player.getInventory().setItem(index, cooldownItem)
    else
      val artItem = ArtsGUI.retrieveArtDisplay(art, locale, click = false)(using player)
      val artDisplay = ItemStackBuilder.from(artItem).transform(_.setUnbreakable(true))
        .flags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS).build()
      player.getInventory().setItem(index, artDisplay)

  private def putWeapons(player: Player, user: User): Unit =
    if !BattlefieldListener.isInBattle(player) then return
    val locale = UserAdapter.getAdapter().getUser(player).getLocale()

    val weapon = I18n.translateItem(
      user.weapon.get.material -> 1,
      "weapon",
      "name" -> user.weapon.get.displayName(locale),
    )(using ClassesGUI.getModule, player)
    val weaponItem = ItemStackBuilder.from(weapon).transform(_.setUnbreakable(true))
      .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE).build()

    player.getInventory().setItem(0, weaponItem)
    player.getInventory().setItem(1, ItemStackBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build())

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
  private def onQuit(event: PlayerQuitEvent): Unit =
    tasks.remove(event.getPlayer.getUniqueId).foreach(_.cancel)
    val user = event.getPlayer().user.get
    user.resetCooldowns()
    user.stopKevesiCooldown()

  @EventHandler
  private def onSwapHands(event: PlayerSwapHandItemsEvent): Unit =
    if event.getPlayer().user.exists(_.bladeUnsheathed) then event.setCancelled(true)

  private def tryFusionAtSlot(player: Player, user: User, index: Int): Unit =
    if player.isSneaking() && !user.isOnCooldown(user.arts(index)) && !user.isOnCooldown(user.masterArts(index)) then
      tryUsing(user, player, user.arts(index), true)
      tryUsing(user, player, user.masterArts(index), true)
    else tryUsing(user, player, user.arts(index))

  @EventHandler
  private def onHotbarSwap(event: PlayerItemHeldEvent): Unit =
    val user = event.getPlayer().user.get
    if user.bladeUnsheathed then
      event.setCancelled(true)
      event.getNewSlot() match
        case 2 => tryUsing(user, event.getPlayer(), user.masterArts(0))
        case 3 => tryUsing(user, event.getPlayer(), user.masterArts(1))
        case 4 => tryUsing(user, event.getPlayer(), user.masterArts(2))
        case 5 => tryFusionAtSlot(event.getPlayer(), user, 0)
        case 6 => tryFusionAtSlot(event.getPlayer(), user, 1)
        case 7 => tryFusionAtSlot(event.getPlayer(), user, 2)
        case 8 => tryUsing(user, event.getPlayer(), user.talentArt.orNull)
        case _ => ()

  end onHotbarSwap

  private def tryUsing(user: User, player: Player, art: ArtType, fusion: Boolean = false): Unit =
    if art == null || user.isOnCooldown(art) then return
    if user.isInAnimation || ArtManager.get(art).map(_.use(player, user, fusion)).contains(true) then user.use(art)

  @EventHandler
  private def onItemDrop(event: PlayerDropItemEvent): Unit =
    val user = event.getPlayer().user.get
    if user.bladeUnsheathed then event.setCancelled(true)

  @EventHandler
  private def onCommandPreprocess(event: PlayerCommandPreprocessEvent): Unit =
    val user = event.getPlayer().user.get
    if user.bladeUnsheathed then event.setCancelled(true)

  @EventHandler
  private def onInventoryEvent(event: InventoryOpenEvent): Unit = event.getPlayer() match
    case player: Player => if player.user.exists(_.bladeUnsheathed) then event.setCancelled(true)
    case _              => ()

  @EventHandler
  private def onInventoryClick(event: InventoryClickEvent): Unit = event.getWhoClicked() match
    case player: Player => if player.user.exists(_.bladeUnsheathed) then event.setCancelled(true)
    case _              => ()

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  private def onHit(event: PlayerDealDamageEvent): Unit = if event.isHit && !event.shouldOverride then
    val user = event.getPlayer().user.get
    user.masterArts.appendedAll(user.arts).filter(_ != null).filter(_.isAgnian).foreach(user.rechargeArt(_))

  @EventHandler
  private def onUnsheathe(event: PlayerUnsheatheEvent): Unit =
    tasks.remove(event.getPlayer.getUniqueId).foreach(_.cancel)
    tasks(event.getPlayer().getUniqueId()) = Tasks.run(handleHotbar(event.getPlayer, _)).plugin(Xenocraft.getInstance)
      .delay(0).period(1).run()
    event.getPlayer().user.foreach(_.startKevesiCooldown())

end HotbarManager
