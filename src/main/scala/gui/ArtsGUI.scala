package dev.hawu.plugins.xenocraft
package gui

import dev.hawu.plugins.api.Strings
import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.collections.tuples.Pair
import dev.hawu.plugins.api.gui.brushes.BrushRegistry
import dev.hawu.plugins.api.gui.pagination.GuiPaginator
import dev.hawu.plugins.api.gui.templates.StaticComponent
import dev.hawu.plugins.api.gui.{GuiComponent, GuiModel}
import dev.hawu.plugins.api.i18n.{LanguageModule, Locale}
import dev.hawu.plugins.api.items.ItemStackBuilder
import dev.hawu.plugins.xenocraft.I18n.tl
import dev.hawu.plugins.xenocraft.UserMap.user
import dev.hawu.plugins.xenocraft.arts.ArtManager
import dev.hawu.plugins.xenocraft.data.{ArtRechargeType, ArtType, ClassNation, User}
import dev.hawu.plugins.xenocraft.gui.ArtsGUI.{ArtComponent, TalentArtComponent}
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.{ItemFlag, ItemStack}
import org.bukkit.plugin.java.JavaPlugin

import java.text.DecimalFormat
import scala.jdk.CollectionConverters.*

/** The singleton object dedicated to arts GUIs.
  */
object ArtsGUI extends ModuleHolder("arts-ui"):

  given Option[LanguageModule] = module

  /** Opens the menu for the player to select and bind arts.
    *
    * @param player
    *   the player
    * @param slot
    *   the slot to bind to
    * @param master
    *   whether this is to select master arts
    */
  def openArtsSelection(player: Player, slot: Int, master: Boolean): Unit =
    given Player = player

    val user = player.user.get
    val locale = UserAdapter.getAdapter.getUser(player).getLocale

    var includesKeves = true
    var includesAgnus = true

    user.cls match
      case Some(cls) => cls.nation match
          case ClassNation.KEVES => includesKeves = !master
          case ClassNation.AGNUS => includesAgnus = !master
      case _ => ()

    GuiPaginator.newBuilder[ArtType]().setCollection(
      ArtManager.getAllArts(includesKeves = includesKeves, includesAgnus = includesAgnus, includesTalent = false)
        .filter(user.canUseArtAs(_, if master then "master" else "class")).toList.asJava,
    ).setAllowedSlots(MainGUI.getPaginationSlots).setFilterTemplate(null).setBackAction(_ => openArts(player))
      .setBackSlots(Set(2).map(Integer.valueOf).asJava).setPreviousButtonSlots(Set(47).map(Integer.valueOf).asJava)
      .setNextButtonSlots(Set(53).map(Integer.valueOf).asJava).setItemGenerator((art, _) =>
        new GuiComponent[Unit]() {
          override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            user.equipArt(art, slot, master)
            openArts(player)

          override def render(): ItemStack = retrieveArtDisplay(art, locale, true)
        },
      ).setModelSupplier(() => retrieveModel(if master then "choose-master-art-title" else "choose-art-title"))
      .build(player)
  end openArtsSelection

  /** Opens the menu for the player to select talent arts.
    *
    * @param player
    *   the player
    */
  def openTalentArtsSelection(player: Player): Unit =
    given Player = player

    val user = player.user.get
    val locale = UserAdapter.getAdapter.getUser(player).getLocale

    GuiPaginator.newBuilder[ArtType]()
      .setCollection(ArtManager.getAllArts(false, false).filter(user.canUseArtAs(_, "talent")).toList.asJava)
      .setAllowedSlots(MainGUI.getPaginationSlots).setBackAction(_ => openArts(player))
      .setBackSlots(Set(2).map(Integer.valueOf).asJava).setPreviousButtonSlots(Set(47).map(Integer.valueOf).asJava)
      .setNextButtonSlots(Set(53).map(Integer.valueOf).asJava)
      .setModelSupplier(() => retrieveModel("choose-talent-art-title")).setPredicate(null).setItemGenerator((art, _) =>
        new GuiComponent[Unit]() {
          override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            user.talentArt = Some(art)
            openArts(player)

          override def render(): ItemStack = retrieveArtDisplay(art, locale, true)
        },
      ).build(player)
  end openTalentArtsSelection

  /** Opens the arts menu GUI.
    *
    * @param player
    *   the player
    */
  def openArts(player: Player): Unit =
    given Player = player

    val model = retrieveModel("arts-ui-title")

    // The master arts
    (21 to 39 by 9).zipWithIndex.foreach((num, index) => model.mount(num, ArtComponent(player, index, true)))

    // The class arts
    (23 to 41 by 9).zipWithIndex.foreach((num, index) => model.mount(num, ArtComponent(player, index)))

    // The talent art
    model.mount(34, TalentArtComponent(player))

    // The tooltip text
    model.mount(12, StaticComponent(I18n.translateItem(Material.MAP -> 1, "master-arts-tooltip")))
    model.mount(14, StaticComponent(I18n.translateItem(Material.MAP -> 1, "arts-tooltip")))
    model.mount(16, StaticComponent(I18n.translateItem(Material.MAP -> 1, "talent-art-tooltip")))

    model.open(player)
  end openArts

  private def retrieveModel(key: String)(using Player): GuiModel =
    val model = I18n.translateModel(54, key)
    MainGUI.applyNavigationBar(model, '3', Material.LIGHT_BLUE_STAINED_GLASS_PANE)
    model

  private def retrieveArtDisplay(art: ArtType, locale: Locale, detailed: Boolean = false)(using
    player: Player,
  ): ItemStack =
    val user = player.user.get
    val item =
      if !detailed then
        I18n.translateItem(
          art.icon -> 1,
          "art",
          "reaction" -> art.reaction.map(_.name(locale)).getOrElse("&7---"),
          "name" -> art.name(locale),
          "description" -> Strings.chop(art.description(locale), 32),
        )
      else
        val cooldownText = art.cooldownType match
          case ArtRechargeType.TIME =>
            val decimalFormat = DecimalFormat("#,###.#")
            val cooldown = decimalFormat.format(art.cooldown)
            module.get.translate(locale, "cooldown-time", Pair.of("time", cooldown))
          case ArtRechargeType.AUTO_ATTACK => module.get
              .translate(locale, "cooldown-auto-attack", Pair.of("count", art.cooldown.intValue))
          case ArtRechargeType.ROLE_ACTION => module.get
              .translate(locale, "cooldown-role-action", Pair.of("count", art.cooldown.intValue))

        I18n.translateItem(
          art.icon -> 1,
          "choose-art",
          "reaction" -> art.reaction.map(_.name(locale)).getOrElse("&7---"),
          "name" -> art.name(locale),
          "type" -> art.category.name(locale),
          "target" -> art.target.name(locale),
          "power" -> s"${(art.powerMultiplier * 100).round}%",
          "cooldown" -> cooldownText,
          "recharge" -> art.cooldownType.name(locale),
          "description" -> Strings.chop(art.description(locale), 32),
          "selection" -> (if user.isArtSelected(art) then "selected".tl(locale) else "not-selected".tl(locale)),
        )
    end item

    ItemStackBuilder.from(item).flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS).build()
  end retrieveArtDisplay

  /** The implementation of a GUI component that allows selecting arts.
    */
  class ArtComponent(private var player: Player, private val slot: Int, private val master: Boolean = false)
    extends GuiComponent[Unit]():

    private val locale = UserAdapter.getAdapter.getUser(player).getLocale
    private var user = player.user.get
    private var artOption = Option(if master then user.masterArts(slot) else user.arts(slot))

    override def componentWillUnmount(): Unit =
      player = null
      user = null
      artOption = None

    override def handleClick(event: InventoryClickEvent): Unit =
      event.setCancelled(true)
      openArtsSelection(player, slot, master)

    override def render(): ItemStack =
      if artOption.isEmpty then
        I18n.translateItem(Material.BLACK_STAINED_GLASS_PANE -> 1, "empty-art")(using module, player)
      else retrieveArtDisplay(artOption.get, locale)(using player)
    end render

  end ArtComponent

  /** The implementation of GUI Component to handle selecting talent arts.
    */
  class TalentArtComponent(private var player: Player) extends GuiComponent[Unit]:

    private val locale = UserAdapter.getAdapter.getUser(player).getLocale
    private var user = player.user.get

    override def componentWillUnmount(): Unit =
      player = null
      user = null

    override def handleClick(event: InventoryClickEvent): Unit =
      event.setCancelled(true)
      openTalentArtsSelection(player)

    override def render(): ItemStack =
      if user.talentArt.isEmpty then
        I18n.translateItem(Material.RED_STAINED_GLASS_PANE -> 1, "empty-talent-art")(using module, player)
      else retrieveArtDisplay(user.talentArt.get, locale)(using player)
    end render

  end TalentArtComponent

end ArtsGUI
