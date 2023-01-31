package dev.hawu.plugins.xenocraft
package gui

import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.collections.tuples.Pair
import dev.hawu.plugins.api.gui.pagination.GuiPaginator
import dev.hawu.plugins.api.gui.templates.CloseInventoryComponent
import dev.hawu.plugins.api.gui.{GuiComponent, GuiModel}
import dev.hawu.plugins.api.i18n.LanguageModule
import dev.hawu.plugins.api.items.{BukkitMaterial, ItemStackBuilder}
import dev.hawu.plugins.api.{Strings, Tasks}
import dev.hawu.plugins.xenocraft.I18n.*
import dev.hawu.plugins.xenocraft.UserMap.user
import dev.hawu.plugins.xenocraft.data.{Character, ClassType, User}
import dev.hawu.plugins.xenocraft.utils.Formulas
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.inventory.{ItemFlag, ItemStack}
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

import java.security.SecureRandom
import java.util
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

/** The singleton object dedicated for stats related GUI.
  */
object StatsGui:

  private val random = SecureRandom()
  private var module: Option[LanguageModule] = None

  /** Initializes the module for the stats GUI.
    *
    * @param pl
    *   the plugin to initialize for
    */
  def initialize(pl: JavaPlugin): Unit = module = Some(LanguageModule(pl, "stats-ui"))

  /** Reloads the stats GUI translation module.
    *
    * @param force
    *   whether to overwrite all changes
    */
  def reload(force: Boolean = false): Unit = module.foreach(_.saveResources(force))

  /** The main menu of the stats GUI.
    *
    * @param player
    *   The player to open the GUI for.
    */
  def mainMenu(player: Player): Unit =
    given p: Player = player

    val extendedUser = UserAdapter.getAdapter.getUser(player)
    val locale = extendedUser.getLocale

    val model = GuiModel(54, module.get.translate(locale, "stats-ui-title"))
    val user = player.user.get

    // The Stats displaying component.
    model.mount(
      13,
      new GuiComponent[Unit]() {
        override def render(): ItemStack = getItem("overall", Material.PLAYER_HEAD)(
          "hp" -> user.maxHp.intValue,
          "atk" -> user.attack.intValue,
          "healing" -> user.healing.intValue,
          "dexterity" -> user.dexterity.intValue,
          "agility" -> user.agility.intValue,
          "crit-rate" -> (user.critRate * 100).intValue,
          "block-rate" -> (user.blockRate * 100).intValue,
          "phys-def" -> (user.physicalDef * 100).intValue,
          "ether-def" -> (user.etherDef * 100).intValue,
        ).durability(3).transformed[SkullMeta](_.setOwningPlayer(player)).build()
      },
    )

    // Soulhacker class selecting component
    if user.cls.exists(_.isSoulhacker) then
      model.mount(
        8,
        new GuiComponent[Int](0) {
          var task: Option[BukkitTask] = None

          def randomMaterial: Material =
            val values = BukkitMaterial.getDisplayableMaterials.asScala
            val num = random.nextInt(values.length)
            values(num)

          override def componentDidMount(): Unit =
            task.foreach(_.cancel())
            task = Some(Tasks.run { () => setState(_ + 1) }.delay(0).period(5).run())

          override def componentWillUnmount(): Unit =
            task.foreach(_.cancel())
            task = None

          override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            chooseSoulhackerSoul(player)

          override def render(): ItemStack =
            getItem("soul-select", randomMaterial)("soul-name" -> user.cls.map(_.soulName(locale)).get)
              .flags(ItemFlag.values().toSeq: _*).build()
        },
      )
    end if

    // The Character displaying component.
    model.mount(
      21,
      new GuiComponent[Unit]() {
        override def handleClick(event: InventoryClickEvent): Unit =
          event.setCancelled(true)
          if event.isRightClick then
            user.char = None
            mainMenu(player)
          else chooseCharacter(player)

        override def render(): ItemStack = getItem("character-select", Material.IRON_SWORD)(
          "char" -> user.char.map(_.name(locale)).getOrElse("none".tl(locale)),
        ).flags(ItemFlag.HIDE_ATTRIBUTES).build()
      },
    )

    // Weapon choosing component if the user can choose a weapon.
    if user.canChooseWeapon then
      model.mount(
        22,
        new GuiComponent[Unit]() {
          override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            chooseWeapon(player)

          override def render(): ItemStack = getItem("weapon-select", Material.FISHING_ROD)(
            "weapon-name" -> user.weapon.map(_.displayName).getOrElse("none".tl(locale)),
          ).flags(ItemFlag.HIDE_ATTRIBUTES).build()
        },
      )

    // The Class displaying component.
    model.mount(
      23,
      new GuiComponent[Unit]() {
        val realDisplayName: String =
          if user.cls.exists(_.isSoulhacker) then user.cls.get.soulhackerName(locale)
          else user.cls.map(_.displayName(locale)).getOrElse("none".tl(locale))

        override def handleClick(event: InventoryClickEvent): Unit =
          event.setCancelled(true)
          if event.isRightClick then
            user.cls = None
            mainMenu(player)
          else chooseClass(player)

        override def render(): ItemStack =
          getItem("class-select", Material.IRON_CHESTPLATE)("display-name" -> realDisplayName)
            .flags(ItemFlag.HIDE_ATTRIBUTES).build()
      },
    )

    model.open(player)
  end mainMenu

  /** Opens a GUI for the player to choose a character.
    *
    * @param player
    *   the player
    */
  def chooseCharacter(player: Player): Unit =
    given p: Player = player

    val locale = UserAdapter.getAdapter.getUser(player).getLocale
    val user = player.user.get

    GuiPaginator.newBuilder[Character]().setCollection(Character.values.toList.asJava).setModelSupplier { () =>
      val model = GuiModel(54, module.get.translate(locale, "characters-ui"))
      model.mount(49, CloseInventoryComponent())
      model
    }.setPredicate((c, filter) => c.name(locale).toLowerCase.contains(filter.toLowerCase))
      .setFilterEvent(_ => p.tl("filter-input")).setBackAction(_ => mainMenu(player)).setItemGenerator((c, _) =>
        new GuiComponent[Unit]() {
          private val description = Strings.chop(c.description(locale), 32)

          override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            user.char = Some(c)
            mainMenu(player)

          override def render(): ItemStack = getItem("character", c.icon)(
            "hp" -> c.baseHp.intValue,
            "atk" -> c.baseAttack.intValue,
            "healing" -> c.baseHealingPower.intValue,
            "dexterity" -> c.baseDexterity.intValue,
            "agility" -> c.baseAgility.intValue,
            "name" -> c.name(locale),
            "description" -> description,
            "selection" -> (if user.char.contains(c) then "selected".tl(locale) else "not-selected".tl(locale)),
          ).flags(ItemFlag.HIDE_ATTRIBUTES).build()
        },
      ).build(player)

  end chooseCharacter

  /** Opens a GUI for the player to choose a class.
    *
    * @param player
    *   the player
    */
  def chooseClass(player: Player): Unit =
    given p: Player = player

    val user = player.user.get
    val locale = UserAdapter.getAdapter.getUser(player).getLocale

    GuiPaginator.newBuilder[ClassType]().setCollection(ClassType.values.filter(_.shouldDisplay).toList.asJava)
      .setModelSupplier { () =>
        val model = GuiModel(54, module.get.translate(locale, "classes-ui"))
        model.mount(49, CloseInventoryComponent())
        model
      }.setPredicate((cls, filter) => cls.toString.toLowerCase.contains(filter.toLowerCase))
      .setFilterEvent(_ => player.tl("filter-input")).setBackAction(_ => mainMenu(player)).setItemGenerator((cls, _) =>
        new GuiComponent[Unit]() {
          override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            user.cls = Some(cls)
            user.weapon = Some(cls.weaponType)
            mainMenu(player)

          override def render(): ItemStack =
            getItem("class", cls.weaponType.material, durability = cls.weaponType.durability)(
              "weapon-type" -> s"&a${cls.weaponType.toString.split("_").map(_.toLowerCase.capitalize).mkString(" ")}",
              "wielder-name" -> cls.classRole.colorize(cls.wielderName(locale)),
              "wielder-title" -> cls.classRole.colorize(cls.wielderTitle(locale)),
              "class" -> cls.displayName(locale),
              "selection" -> (if user.cls.contains(cls) then "selected".tl(locale) else "not-selected".tl(locale)),
            ).flags(ItemFlag.HIDE_ATTRIBUTES).build()
        },
      ).build(player)

  end chooseClass

  /** Chooses a soul for the soulhacker class.
    *
    * @param player
    *   the player
    */
  def chooseSoulhackerSoul(player: Player): Unit =
    given p: Player = player

    val user = player.user.get
    val locale = UserAdapter.getAdapter.getUser(player).getLocale

    GuiPaginator.newBuilder[ClassType]().setCollection(ClassType.values.filter(_.isSoulhacker).toList.asJava)
      .setModelSupplier { () =>
        val model = GuiModel(54, module.get.translate(locale, "souls-ui"))
        model.mount(49, CloseInventoryComponent())
        model
      }.setBackAction(_ => mainMenu(player))
      .setPredicate((cls, filter) => cls.toString.toLowerCase.contains(filter.toLowerCase)).setItemGenerator((cls, _) =>
        new GuiComponent[Unit]() {
          override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            user.cls = Some(cls)
            mainMenu(player)

          override def render(): ItemStack = getItem("soul", Material.CHEST_MINECART)(
            "soul" -> cls.soulName(locale),
            "selection" -> (if user.cls.contains(cls) then "selected".tl(locale) else "not-selected".tl(locale)),
          ).build()
        },
      ).build(player)

  end chooseSoulhackerSoul

  /** Opens a menu for the player to pick a weapon.
    *
    * @param player
    *   the player
    */
  def chooseWeapon(player: Player): Unit =
    given p: Player = player

    val locale = UserAdapter.getAdapter.getUser(p).getLocale
    val model = GuiModel(27, module.get.translate(locale, "weapons-ui"))
    val user = player.user.get

    model.mount(22, CloseInventoryComponent())
    model.mount(
      21,
      new GuiComponent[Unit]() {
        override def handleClick(event: InventoryClickEvent): Unit =
          event.setCancelled(true)
          mainMenu(player)

        override def render(): ItemStack = getItem("back-button", Material.ARROW)().build()
      },
    )

    model.mount(
      11,
      new GuiComponent[Unit]() {
        override def handleClick(event: InventoryClickEvent): Unit =
          event.setCancelled(true)
          user.weapon = Some(user.cls.get.weaponType)
          mainMenu(player)

        override def render(): ItemStack = getItem("normal-weapon", user.cls.get.weaponType.material)(
          "soul" -> user.cls.get.weaponType.displayName,
          "selection" ->
            (if user.weapon.contains(user.cls.get.weaponType) then "selected".tl(locale) else "not-selected".tl(locale)),
        ).flags(ItemFlag.HIDE_ATTRIBUTES).build()
      },
    )
    model.mount(
      15,
      new GuiComponent[Unit]() {
        override def handleClick(event: InventoryClickEvent): Unit =
          event.setCancelled(true)
          user.weapon = Some(user.cls.get.upgradedWeaponType.get)
          mainMenu(player)

        override def render(): ItemStack = getItem("upgraded-weapon", user.cls.get.weaponType.material)(
          "name" -> user.cls.flatMap(_.upgradedWeaponType).get.displayName,
          "selection" ->
            (if user.weapon.contains(user.cls.get.upgradedWeaponType.get) then "selected".tl(locale)
             else "not-selected".tl(locale)),
        ).flags(ItemFlag.HIDE_ATTRIBUTES).build()
      },
    )

    model.open(player)
  end chooseWeapon

  private def getItem(key: String, material: Material, amount: Int = 1, durability: Int = 1)(args: (String, Any)*)(using
    player: Player,
  ): ItemStackBuilder =
    val extendedUser = UserAdapter.getAdapter.getUser(player)
    val item = module.map(_.translateItem(extendedUser.getLocale, ItemStack(material, amount), key, args.asLibrary: _*))
      .get
    ItemStackBuilder.from(item)

end StatsGui
