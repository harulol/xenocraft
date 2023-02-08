package dev.hawu.plugins.xenocraft
package gui

import UserMap.user
import data.Character
import I18n.tl

import dev.hawu.plugins.api.Strings
import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.gui.GuiComponent
import dev.hawu.plugins.api.gui.pagination.GuiPaginator
import dev.hawu.plugins.api.i18n.LanguageModule
import dev.hawu.plugins.api.items.ItemStackBuilder
import dev.hawu.plugins.xenocraft.ModuleHolder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.{ItemFlag, ItemStack}
import org.bukkit.plugin.java.JavaPlugin

import scala.jdk.CollectionConverters.*

/** The dedicated singleton object for managing GUI for choosing a character preset.
  */
object CharactersGUI extends ModuleHolder("characters-ui"):

  given Option[LanguageModule] = module

  /** Opens the menu to choose a character preset.
    *
    * @param player
    *   the player
    */
  def openCharacters(player: Player): Unit =
    given Player = player

    val user = player.user.get
    val locale = UserAdapter.getAdapter.getUser(player).getLocale

    GuiPaginator.newBuilder[Character]().setModelSupplier(() => {
      val model = I18n.translateModel(54, "characters-ui-title")
      MainGUI.applyNavigationBar(model, '1', Material.GREEN_STAINED_GLASS_PANE)
      model
    }).setAllowedSlots(MainGUI.getPaginationSlots).setBackSlots(Set.empty[Int].map(Integer.valueOf).asJava).setPredicate(null)
      .setCollection(Character.values.toList.asJava).setFilterSlots(Set.empty[Int].map(Integer.valueOf).asJava)
      .setItemGenerator((char, _) =>
        new GuiComponent[Unit]() {
          private val item = I18n.translateItem(
            char.icon -> 1,
            "character",
            "hp" -> char.baseHp.intValue,
            "atk" -> char.baseAttack.intValue,
            "healing" -> char.baseHealingPower.intValue,
            "agility" -> char.baseAgility.intValue,
            "dexterity" -> char.baseDexterity.intValue,
            "name" -> char.name(locale),
            "description" -> Strings.chop(char.description(locale), 32),
            "selection" -> (if user.char.contains(char) then "selected".tl(locale) else "not-selected".tl(locale)),
          )

          override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            user.applyCharacter(char)
            MainGUI.openMain(player)

          override def render(): ItemStack = ItemStackBuilder.from(item).flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS)
            .build()
        },
      ).build(player)
  end openCharacters

end CharactersGUI
