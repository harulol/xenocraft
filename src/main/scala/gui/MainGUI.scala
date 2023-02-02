package dev.hawu.plugins.xenocraft
package gui

import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.gui.brushes.{BrushRegistry, LayoutBrush}
import dev.hawu.plugins.api.gui.templates.{CloseInventoryComponent, StaticComponent}
import dev.hawu.plugins.api.gui.{GuiComponent, GuiModel}
import dev.hawu.plugins.api.i18n.{LanguageModule, Locale}
import dev.hawu.plugins.api.items.ItemStackBuilder
import dev.hawu.plugins.xenocraft.I18n.*
import dev.hawu.plugins.xenocraft.UserMap.user
import org.bukkit.entity.Player
import org.bukkit.event.inventory.{InventoryClickEvent, InventoryType}
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.inventory.{ItemFlag, ItemStack}
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.{Material, Sound}

import java.util
import scala.jdk.CollectionConverters.*

/** The singleton object for dealing with the very main character building UIs, as all other UIs are accessible from
  * this UI.
  */
object MainGUI extends ModuleHolder("main-ui"):

  private val navigationMap = Map(
    '1' -> ("character-menu-item" -> Material.PLAYER_HEAD),
    '2' -> ("class-menu-item" -> Material.PAPER),
    '3' -> ("art-menu-item" -> Material.IRON_SWORD),
    '4' -> ("gem-menu-item" -> Material.EMERALD),
    '5' -> ("skill-menu-item" -> Material.NETHER_STAR),
    '6' -> ("accessory-menu-item" -> Material.TOTEM_OF_UNDYING),
  )

  private val functionMap = Map(
    '1' -> CharactersGUI.openCharacters,
    '2' -> ClassesGUI.openClasses,
    '3' -> ArtsGUI.openArts,
    '4' -> GemsGUI.openGems,
    '5' -> SkillsGUI.openSkills,
    '6' -> ((p: Player) => openMain(p)),
  )

  given Option[LanguageModule] = module

  /** Mounts the navigation buttons on the menu on the side.
    *
    * @param model
    *   the model
    * @param player
    *   the player
    * @param current
    *   the current selected item
    * @param border
    *   the border as decorations
    */
  def applyNavigationBar(model: GuiModel, current: Char, border: Material)(using player: Player): Unit =
    val layout = BrushRegistry.layout().setModel(model)
      .setLayout("1-******X", "2-*-----*", "3-*-----*", "4-*-----*", "5-*-----*", "6-*******").build()

    layout.apply('*', () => ItemStackBuilder.of(border).name("&e").toStaticComponent)
    layout.apply(
      'X',
      () =>
        new GuiComponent[Unit]() {
          override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            openMain(player)

          override def render(): ItemStack = I18n.translateItem(Material.COMPASS -> 1, "main-menu-item")
        },
    )

    // Set the navigation buttons and apply glow if needed.
    applyNavigationButtons(current, layout)

    val index = current.toString.toInt - 1
    model.mount(index * 9 + 1, ItemStackBuilder.of(border).name("&e").toStaticComponent)
  end applyNavigationBar

  /** Opens the main GUI's... main menu to the player.
    *
    * @param player
    *   the player
    */
  def openMain(player: Player): Unit =
    given Player = player

    val user = player.user.get
    val locale = UserAdapter.getAdapter.getUser(player).getLocale

    val model = I18n.translateModel(45, "main-ui-title")
    val layout = BrushRegistry.layout().setModel(model)
      .setLayout("123456***", "*-------*", "*---P---*", "*-------*", "****X****").build()

    layout.apply('*', () => ItemStackBuilder.of(Material.BLACK_STAINED_GLASS_PANE).toStaticComponent)
    layout.apply('X', () => CloseInventoryComponent())
    applyNavigationButtons(' ', layout)

    // The profile button, just there to show you what you have chosen.
    val className =
      if user.cls.exists(_.isSoulhacker) then user.cls.map(_.soulhackerName(locale)).get
      else user.cls.map(_.displayName(locale)).getOrElse("none".tl(locale))
    val profileItem = ItemStackBuilder.from(I18n.translateItem(
      Material.PLAYER_HEAD -> 1,
      "profile-item",
      "name" -> player.getName,
      "hp" -> user.maxHp.intValue,
      "atk" -> user.attack.intValue,
      "healing" -> user.healing.intValue,
      "dexterity" -> user.dexterity.intValue,
      "agility" -> user.agility.intValue,
      "crit-rate" -> (user.critRate * 100).intValue,
      "block-rate" -> (user.blockRate * 100).intValue,
      "physical-def" -> (user.physicalDef * 100).intValue,
      "ether-def" -> (user.etherDef * 100).intValue,
      "class" -> className,
      "weapon" -> user.weapon.map(_.displayName(locale)).getOrElse("none".tl(locale)),
      "char" -> user.char.map(_.name(locale)).getOrElse("none".tl(locale)),
    )).transformed[SkullMeta](_.setOwningPlayer(player)).toStaticComponent
    layout.apply('P', () => profileItem)

    model.open(player)
  end openMain

  /** Apply navigation buttons to a layout brush from the chars 1 -> 6.
    *
    * @param current
    *   the current selected item
    * @param layout
    *   the layout brush
    * @param player
    *   the player to open to
    */
  def applyNavigationButtons(current: Char, layout: LayoutBrush)(using player: Player): Unit =
    for i <- 1 to 6 do
      val char = i.toString.head
      val (key, mat) = navigationMap(char)
      val item = ItemStackBuilder.from(I18n.translateItem(mat -> 1, key))

      layout.apply(
        char,
        () =>
          new GuiComponent[Unit]() {
            override def handleClick(event: InventoryClickEvent): Unit =
              event.setCancelled(true)
              player.playSound(player.getLocation, Sound.BLOCK_LEVER_CLICK, 1, 1)
              functionMap(char)(player)

            override def render(): ItemStack =
              item.flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS)
              if current == char then item.glow().build() else item.build()
          },
      )
    end for

  end applyNavigationButtons

  /** Retrieves the slots needed for pagination within an enclosed space.
    *
    * @return
    *   the slots needed
    */
  def getPaginationSlots: util.List[Integer] = (0 to 54).filter(i => i % 9 >= 3 && i % 9 <= 7).filterNot(_ < 9)
    .filterNot(_ > 44).map(Integer.valueOf).asJava

end MainGUI
