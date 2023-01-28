package dev.hawu.plugins.xenocraft
package gui

import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.gui.GuiComponent
import dev.hawu.plugins.api.gui.brushes.BrushRegistry
import dev.hawu.plugins.api.gui.pagination.GuiPaginator
import dev.hawu.plugins.api.i18n.LanguageModule
import dev.hawu.plugins.api.items.ItemStackBuilder
import dev.hawu.plugins.xenocraft.I18n.tl
import dev.hawu.plugins.xenocraft.UserMap.user
import dev.hawu.plugins.xenocraft.data.ClassType
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.{ItemFlag, ItemStack}

import scala.jdk.CollectionConverters.*

/**
 * The singleton object dedicated to working with
 * the GUI to select classes.
 */
object ClassesGUI extends ModuleHolder("classes-ui"):

  given Option[LanguageModule] = module

  /**
   * Opens the classes selecting menus for the player
   *
   * @param player the player
   */
  def openClasses(player: Player): Unit =
    given Player = player

    val user = player.user.get
    val locale = UserAdapter.getAdapter.getUser(player).getLocale

    GuiPaginator.newBuilder[ClassType]()
      .setModelSupplier(() => {
        val model = I18n.translateModel(54, "classes-ui-title")
        MainGUI.applyNavigationBar(model, '2', Material.PINK_STAINED_GLASS_PANE)
        model
      })
      .setCollection(ClassType.values.filter(_.shouldDisplay).toList.asJava)
      .setAllowedSlots(MainGUI.getPaginationSlots)
      .setPredicate(null)
      .setPreviousButtonSlots(Set(47).map(Integer.valueOf).asJava)
      .setNextButtonSlots(Set(53).map(Integer.valueOf).asJava)
      .setItemGenerator((cls, _) => new GuiComponent[Unit]() {
        override def handleClick(event: InventoryClickEvent): Unit =
          event.setCancelled(true)
          user.cls = Some(cls)
          MainGUI.openMain(player)

        override def render(): ItemStack = ItemStackBuilder.from(I18n.translateItem(cls.weaponType.material -> 1, "class",
          "name" -> cls.displayName(locale),
          "wielder" -> cls.classRole.colorize(cls.wielderName(locale)),
          "title" -> cls.classRole.colorize(cls.wielderTitle(locale)),
          "selection" -> (if user.cls.contains(cls) then "selected".tl(locale) else "not-selected".tl(locale)),
        )).flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS).build()
      })
      .build(player)
  end openClasses

