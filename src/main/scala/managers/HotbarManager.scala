package dev.hawu.plugins.xenocraft
package managers

import UserMap.user
import data.{ArtRechargeType, ArtType, User}
import gui.{ArtsGUI, ClassesGUI}
import listener.HotbarListener
import managers.AggroManager

import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.events.Events
import dev.hawu.plugins.api.i18n.LanguageModule
import dev.hawu.plugins.api.items.ItemStackBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.{ItemFlag, ItemStack}
import org.bukkit.plugin.java.JavaPlugin

import java.text.DecimalFormat

/** Handles the hotbar for the player.
 */
object HotbarManager extends Initializable:

  private val formatter = DecimalFormat("#,###.#")
  formatter.setMinimumFractionDigits(1)

  override def setUp(pl: JavaPlugin) = Events.registerEvents(pl, HotbarListener)

  /** Applies the arts palette hotbar to the [[player]].
   */
  def applyHotbar(player: Player): Unit =
    val user = player.user.get
    applyWeapon(player)
    applyDivider(player)
    (2 to 4).zip(user.masterArts).foreach((index, art) => applyArt(player, art, index))
    (5 to 7).zip(user.arts).foreach((index, art) => applyArt(player, art, index))
    applyArt(player, user.talentArt.orNull, 8)

  /** Puts the weapon on the first slot of the hotbar for the [[player]].
   */
  def applyWeapon(player: Player): Unit =
    val user = player.user.get
    if !user.bladeUnsheathed || !AggroManager.isBeingTargeted(player) then return ()

    given LanguageModule = ClassesGUI.getModule

    given Player = player

    val locale = UserAdapter.getAdapter.getUser(player).getLocale
    val weaponItem = ItemStackBuilder
      .from(I18n.translateItem(user.weapon.get.material -> 1, "weapon", "name" -> user.weapon.get.displayName(locale)))
      .transform(_.setUnbreakable(true)).flags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS).build()
    player.getInventory.setItem(0, weaponItem)

  /** Puts the hotbar divider on the second slot of the hotbar for [[player]].
   */
  def applyDivider(player: Player): Unit =
    val user = player.user.get
    if !user.bladeUnsheathed || !AggroManager.isBeingTargeted(player) then return ()
    player.getInventory.setItem(1, ItemStackBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build())

  /** Applies the art icon if its recharge is up, or a cooldown item if it is still on cooldown.
   */
  def applyArt(player: Player, art: ArtType, slot: Int): Unit =
    val user = player.user.get
    if !user.bladeUnsheathed || !AggroManager.isBeingTargeted(player) then return ()

    if art == null then player.getInventory.setItem(slot, getEmptySpace)
    else if user.isOnCooldown(art) then player.getInventory.setItem(slot, getCooldownItem(player, art))
    else player.getInventory.setItem(slot, getArtDisplay(player, art))

  private def getArtDisplay(player: Player, art: ArtType) =
    val locale = UserAdapter.getAdapter.getUser(player).getLocale
    ItemStackBuilder.from(ArtsGUI.retrieveArtDisplay(art, locale, click = false)(using player)).transform(_.setUnbreakable(true))
      .flags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS).build()

  private def getEmptySpace = ItemStackBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()

  private def getCooldownItem(player: Player, art: ArtType) =
    val locale = UserAdapter.getAdapter.getUser(player).getLocale
    val user = player.user.get

    // Only Kevesi arts need a decimal place to denote seconds.
    val cooldown = if art.isKevesi then formatter.format(art.cooldown) else art.cooldown.round
    val guage = formatter.format(if art.isKevesi then user.getCooldown(art) / 20 else user.getCooldown(art))
    val placeholder = if art.isTalent then ("talent", "action") else if art.isKevesi then ("keves", "time") else ("agnus", "auto")

    I18n.translateItem(
      Material.GHAST_TEAR -> 1,
      s"on-cooldown-${placeholder(0)}",
      "name" -> art.name(locale),
      placeholder(1) -> guage,
      "cooldown" -> cooldown,
      "bar" -> getProgressBar((if art.isKevesi then user.getCooldown(art) / 20 else user.getCooldown(art)) / art.cooldown),
    )(using ArtsGUI.getModule, player)

  private def getProgressBar(percentage: Double): String =
    val value = (percentage * 100).round.intValue / 5
    s"&7[&a&m${" " * value}&f&m${" " * (20 - value)}&7] &a&l${(percentage * 100).round}%"
