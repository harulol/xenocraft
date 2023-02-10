package dev.hawu.plugins.xenocraft
package gui

import I18n.{tl, translateItem}
import UserMap.user
import data.GemType
import gui.GemsGUI.GemComponent
import managers.GemsManager

import dev.hawu.plugins.api.Strings
import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.gui.GuiComponent
import dev.hawu.plugins.api.gui.brushes.BrushRegistry
import dev.hawu.plugins.api.gui.pagination.GuiPaginator
import dev.hawu.plugins.api.i18n.LanguageModule
import dev.hawu.plugins.api.items.ItemStackBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

import scala.jdk.CollectionConverters.*

/** The singleton object dedicated to working with the UI to select gems.
  */
object GemsGUI extends ModuleHolder("gems-ui"):

  given Option[LanguageModule] = module

  /** Opens the selection menu for gems.
    *
    * @param player
    *   the player
    * @param index
    *   the index to place at
    */
  def gemSelectionMenu(player: Player, index: Int): Unit =
    given Player = player

    val user = player.user.get
    val locale = UserAdapter.getAdapter.getUser(player).getLocale

    GuiPaginator.newBuilder[GemType]().setCollection(GemType.values.toList.asJava).setAllowedSlots(MainGUI.getPaginationSlots)
      .setModelSupplier(() => {
        val model = I18n.translateModel(54, "gems-ui-title")
        MainGUI.applyNavigationBar(model, '4', Material.ORANGE_STAINED_GLASS_PANE)
        model
      }).setPredicate(null).setItemGenerator((gem, _) =>
        new GuiComponent[Unit]() {
          override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            gemLevelSelectionMenu(player, index, gem)

          override def render(): ItemStack = I18n.translateItem(
            gem.category.icon -> 1,
            "gem-type",
            "name" -> gem.category.colorize(gem.name(locale), true),
            "description" -> Strings.chop(gem.description(locale), 32),
            "selection" -> (if GemsManager.isGemEquipped(user, gem) >= 0 then "selected".tl(locale) else "not-selected".tl(locale)),
          )
        },
      ).build(player)
  end gemSelectionMenu

  /** Opens a more detailed screen to choose a level for the gem slot.
    *
    * @param player
    *   the player
    * @param index
    *   the gem slot
    * @param gem
    *   the gem type
    */
  def gemLevelSelectionMenu(player: Player, index: Int, gem: GemType): Unit =
    given Player = player

    val user = player.user.get
    val locale = UserAdapter.getAdapter.getUser(player).getLocale

    GuiPaginator.newBuilder[Int]().setCollection((1 to 10).toList.asJava).setAllowedSlots(MainGUI.getPaginationSlots)
      .setModelSupplier(() => {
        val model = I18n.translateModel(54, "gems-ui-title")
        MainGUI.applyNavigationBar(model, '4', Material.ORANGE_STAINED_GLASS_PANE)
        model
      }).setPredicate(null).setItemGenerator((level, _) =>
        new GuiComponent[Unit]() {
          override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            GemsManager.bindGem(user, gem, level, index)
            openGems(player)

          override def render(): ItemStack = I18n.translateItem(
            gem.category.icon -> 1,
            "gem-type",
            "name" -> gem.category.colorize(gem.name(locale, level), true),
            "description" -> Strings.chop(gem.description(locale, level), 32),
            "selection" -> (if GemsManager.isGemEquipped(user, gem, level) >= 0 then "selected".tl(locale) else "not-selected".tl(locale)),
          )
        },
      ).build(player)
  end gemLevelSelectionMenu

  /** The gems selection screen.
    *
    * @param player
    *   the player to open to
    */
  def openGems(player: Player): Unit =
    given Player = player

    val model = I18n.translateModel(54, "gems-ui-title")
    MainGUI.applyNavigationBar(model, '4', Material.ORANGE_STAINED_GLASS_PANE)

    // Gems in slots 22, 32, 24
    model.mount(22, GemComponent(player, 0))
    model.mount(32, GemComponent(player, 1))
    model.mount(24, GemComponent(player, 2))

    model.open(player)
  end openGems

  /** The GUIComponent that handles selecting gems.
    */
  class GemComponent(private var player: Player, private val slot: Int) extends GuiComponent[Unit]():

    private val locale = UserAdapter.getAdapter.getUser(player).getLocale
    private var user = player.user.get
    private var gemTuple = user.gems(slot)

    // just to be sure to un-reference idk
    override def componentWillUnmount(): Unit =
      user = null
      gemTuple = null
      player = null

    override def handleClick(event: InventoryClickEvent): Unit =
      event.setCancelled(true)
      if event.isRightClick then
        user.gems(slot) = null
        openGems(player)
      else gemSelectionMenu(player, slot)

    override def render(): ItemStack = if gemTuple == null then emptyGem else gemItem

    private def emptyGem: ItemStack = I18n.translateItem(Material.BLACK_STAINED_GLASS_PANE -> 1, "no-gem")(using module, player)

    private def gemItem: ItemStack =
      val (gem, level) = gemTuple
      val description = Strings.chop(gem.description(locale, level), 32)
      I18n.translateItem(
        gem.category.icon -> 1,
        "gem",
        "name" -> gem.category.colorize(gem.name(locale, level), true),
        "description" -> description,
      )(using module, player)

  end GemComponent

end GemsGUI
