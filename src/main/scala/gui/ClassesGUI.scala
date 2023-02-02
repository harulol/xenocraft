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
import dev.hawu.plugins.xenocraft.data.User

/** The singleton object dedicated to working with the GUI to select classes.
  */
object ClassesGUI extends ModuleHolder("classes-ui"):

  given Option[LanguageModule] = module

  /** Opens the classes selecting menus for the player
    *
    * @param player
    *   the player
    */
  def openClasses(player: Player): Unit =
    given Player = player

    val user = player.user.get
    val locale = UserAdapter.getAdapter.getUser(player).getLocale

    GuiPaginator.newBuilder[ClassType]().setModelSupplier(() => {
      val model = I18n.translateModel(54, "classes-ui-title")
      MainGUI.applyNavigationBar(model, '2', Material.PINK_STAINED_GLASS_PANE)
      if user.canChooseWeapon then model.mount(2, WeaponSelectComponent(user, player))
      model
    }).setCollection(ClassType.values.filter(_.shouldDisplay).toList.asJava).setAllowedSlots(MainGUI.getPaginationSlots)
      .setPredicate(null).setPreviousButtonSlots(Set(47).map(Integer.valueOf).asJava)
      .setNextButtonSlots(Set(53).map(Integer.valueOf).asJava).setItemGenerator((cls, _) =>
        new GuiComponent[Unit]() {
          override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)

            // Something just to save the last chosen soul, so when you select soulhacker again,
            // it goes straight to that soul instead of Power Soul.
            if cls.isSoulhacker then
              if user.lastSoulhackerSoul.isDefined then user.applyClass(user.lastSoulhackerSoul)
              else
                user.applyClass(Some(cls))
                user.lastSoulhackerSoul = Some(cls)
            else user.applyClass(Some(cls))
            MainGUI.openMain(player)

          override def render(): ItemStack = ItemStackBuilder.from(I18n.translateItem(
            cls.weaponType.material -> 1,
            "class",
            "name" -> cls.displayName(locale),
            "wielder" -> cls.classRole.colorize(cls.wielderName(locale)),
            "title" -> cls.classRole.colorize(cls.wielderTitle(locale)),
            "selection" -> (if user.cls.contains(cls) then "selected".tl(locale) else "not-selected".tl(locale)),
          )).flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS).build()
        },
      ).build(player)
  end openClasses

  /** Opens the weapon selection menu for the player.
    *
    * @param player
    *   the player
    */
  def openWeaponSelect(player: Player): Unit =
    given Player = player

    val model = I18n.translateModel(54, "weapon-ui-title")
    MainGUI.applyNavigationBar(model, '2', Material.PINK_STAINED_GLASS_PANE)

    model.mount(31, WeaponComponent(player.user.get, player, false))
    model.mount(33, WeaponComponent(player.user.get, player, true))

    model.open(player)

  /** The implementation of a weapon component for selecting the weapon
    *
    * @param user
    *   the user
    * @param player
    *   the player
    * @param upgraded
    *   whether this is upgraded
    */
  class WeaponComponent(private val user: User, private var player: Player, private val upgraded: Boolean)
    extends GuiComponent[Unit]():

    private val weapon = if !upgraded then user.cls.get.weaponType else user.cls.get.upgradedWeaponType.get
    private val locale = UserAdapter.getAdapter().getUser(user.uuid).getLocale()

    override def componentWillUnmount(): Unit = player = null

    override def handleClick(event: InventoryClickEvent): Unit =
      event.setCancelled(true)
      user.weapon = Some(weapon)
      MainGUI.openMain(player)

    override def render(): ItemStack = I18n.translateItem(
      weapon.material -> 1,
      if upgraded then "upgraded-weapon" else "normal-weapon",
      "name" -> weapon.displayName(locale),
      "selection" -> (if user.weapon.contains(weapon) then "selected".tl(locale) else "not-selected".tl(locale)),
    )(using module, player)

  end WeaponComponent

  /** The implementation of a gui component for players to select a weapon.
    *
    * @param user
    *   the user related
    */
  class WeaponSelectComponent(private val user: User, private var player: Player) extends GuiComponent[Unit]():
    private val locale = UserAdapter.getAdapter().getUser(player).getLocale()

    override def componentWillUnmount(): Unit = player = null

    override def handleClick(event: InventoryClickEvent): Unit =
      event.setCancelled(true)
      openWeaponSelect(player)

    override def render(): ItemStack = ItemStackBuilder.from(
      I18n.translateItem(
        Material.GHAST_TEAR -> 1,
        "weapon-select",
        "weapon" -> user.weapon.map(_.displayName(locale)).getOrElse("none".tl(locale)),
      )(using module, player),
    ).flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS).build()

  end WeaponSelectComponent

end ClassesGUI
