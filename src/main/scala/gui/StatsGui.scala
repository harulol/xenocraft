package dev.hawu.plugins.xenocraft
package gui

import dev.hawu.plugins.api.gui.pagination.GuiPaginator
import dev.hawu.plugins.api.gui.templates.CloseInventoryComponent
import dev.hawu.plugins.api.gui.{GuiComponent, GuiModel}
import dev.hawu.plugins.api.items.{BukkitMaterial, ItemStackBuilder}
import dev.hawu.plugins.api.{Strings, Tasks}
import dev.hawu.plugins.xenocraft.UserMap.{save, user}
import dev.hawu.plugins.xenocraft.data.{Character, ClassType, User}
import dev.hawu.plugins.xenocraft.utils.Formulas
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.inventory.{ItemFlag, ItemStack}
import org.bukkit.scheduler.BukkitTask

import java.security.SecureRandom
import java.util
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

/**
 * The singleton object dedicated for stats
 * related GUI.
 */
object StatsGui:

  private val random = SecureRandom()

  /**
   * The main menu of the stats GUI.
   *
   * @param player The player to open the GUI for.
   */
  def mainMenu(player: Player): Unit =
    val model = GuiModel(54, "Your Stats")
    val user = player.user.get

    // The Stats displaying component.
    model.mount(13, new GuiComponent[Unit]() {
      override def render(): ItemStack = ItemStackBuilder.of(Material.PLAYER_HEAD)
        .name("&e&lYour Stats")
        .lore(
          "",
          s"&fHP: &a${Formulas.calculateHp(user).intValue}",
          s"&fAttack: &a${Formulas.calculateAttack(user).intValue}",
          s"&fHealing Power: &a${Formulas.calculateHealingPower(user).intValue}",
          s"&fDexterity: &a${Formulas.calculateDexterity(user).intValue}",
          s"&fAgility: &a${Formulas.calculateAgility(user).intValue}",
          s"&fCritical Rate: &a${(Formulas.calculateDisplayCritRate(user) * 100).intValue}%",
          s"&fBlock Rate: &a${(Formulas.calculateDisplayBlockRate(user) * 100).intValue}%",
          s"&fPhys. Def: &a${(Formulas.calculateDisplayPhysDefense(user) * 100).intValue}%",
          s"&fEther Def: &a${(Formulas.calculateDisplayEtherDefense(user) * 100).intValue}%",
        )
        .transformed[SkullMeta](_.setOwningPlayer(player))
        .build()
    })

    // Soulhacker class selecting component
    if user.cls.exists(_.isSoulhacker) then model.mount(8, new GuiComponent[Int](0) {
      var task: Option[BukkitTask] = None

      def randomMaterial: Material =
        val values = BukkitMaterial.getDisplayableMaterials.asScala
        val num = random.nextInt(values.length)
        values(num)

      override def componentDidMount(): Unit =
        task.foreach(_.cancel())
        task = Some(Tasks.scheduleTimerAsync(0, 5, _ => setState(_ + 1)))

      override def componentWillUnmount(): Unit =
        task.foreach(_.cancel())
        task = None

      override def handleClick(event: InventoryClickEvent): Unit =
        event.setCancelled(true)
        chooseSoulhackerSoul(player)

      override def render(): ItemStack = ItemStackBuilder.of(randomMaterial)
        .name("&e&lSoul Skill")
        .flags(ItemFlag.values().toSeq: _*)
        .lore(
          "",
          "&7The class and type of",
          "&7the Soulhacker class.",
          "",
          s"&fSelected: &a${user.cls.map(_.soulName).get}",
          "",
          "&6Click &eto change",
        )
        .build()
    })

    // The Character displaying component.
    model.mount(21, new GuiComponent[Unit]() {
      override def handleClick(event: InventoryClickEvent): Unit =
        event.setCancelled(true)
        if event.isRightClick then
          user.char = None
          mainMenu(player)
        else
          chooseCharacter(player)

      override def render(): ItemStack = ItemStackBuilder.of(Material.IRON_SWORD)
        .flags(ItemFlag.HIDE_ATTRIBUTES)
        .name("&e&lYour Character Preset")
        .lore(
          "",
          s"&fCharacter: &a${user.char.map(_.toString.toLowerCase.capitalize).getOrElse("&cNone")}",
          "",
          "&6Click &eto change",
          "&6Right click &eto deselect",
        )
        .build()
    })

    if user.canChooseWeapon then model.mount(22, new GuiComponent[Unit]() {
      override def handleClick(event: InventoryClickEvent): Unit =
        event.setCancelled(true)
        chooseWeapon(player)

      override def render(): ItemStack = ItemStackBuilder.of(Material.FISHING_ROD)
        .name("&e&lWeapon")
        .lore(
          "",
          "&7You're probably using a class",
          "&7and character that allows switching",
          "&7weapons!",
          "",
          s"&fCurrent: &a${user.weapon.map(_.displayName).getOrElse("&cNone")}",
          "",
          "&6Click &eto change",
        )
        .build()
    })

    // The Class displaying component.
    model.mount(23, new GuiComponent[Unit]() {
      val realDisplayName = if user.cls.get.isSoulhacker then
        s"Soulhacker (${user.cls.get.soulName})"
      else user.cls.get.displayName

      override def handleClick(event: InventoryClickEvent): Unit =
        event.setCancelled(true)
        if event.isRightClick then
          user.cls = None
          mainMenu(player)
        else
          chooseClass(player)

      override def render(): ItemStack = ItemStackBuilder.of(Material.IRON_CHESTPLATE)
        .flags(ItemFlag.HIDE_ATTRIBUTES)
        .name("&e&lYour Class")
        .lore(
          "",
          s"&fClass: &a$realDisplayName",
          "",
          "&6Click &eto change",
          "&6Right click &eto deselect",
        )
        .build()
    })

    model.open(player)
  end mainMenu

  /**
   * Opens a GUI for the player to choose a character.
   *
   * @param player the player
   */
  def chooseCharacter(player: Player): Unit =
    val user = player.user.get
    GuiPaginator.newBuilder[Character]()
      .setCollection(Character.values.toList.asJava)
      .setPredicate((c, filter) => c.toString.toLowerCase.contains(filter.toLowerCase))
      .setBackAction(_ => mainMenu(player))
      .setItemGenerator((c, _) => new GuiComponent[Unit]() {
        private val description = Strings.chop(c.description, 32).asScala.map("&7" + _)

        override def handleClick(event: InventoryClickEvent): Unit =
          event.setCancelled(true)
          user.char = Some(c)
          mainMenu(player)

        override def render(): ItemStack = ItemStackBuilder.of(c.icon)
          .name(s"&e&l${c.toString.toLowerCase.capitalize}")
          .lore("")
          .appendLore(description.asJava)
          .appendLore(
            "",
            s"&7Base HP: &a${c.baseHp.intValue}",
            s"&7Base Attack: &a${c.baseAttack.intValue}",
            s"&7Base Healing: &a${c.baseHealingPower.intValue}",
            s"&7Base Dexterity: &a${c.baseDexterity.intValue}",
            s"&7Base Agility: &a${c.baseAgility.intValue}",
            "",
            if user.char.contains(c) then "&a&lSELECTED" else "&6Click &eto select",
          )
          .flags(ItemFlag.HIDE_ATTRIBUTES)
          .build()
      })
      .build(player)

  /**
   * Opens a GUI for the player to choose a class.
   *
   * @param player the player
   */
  def chooseClass(player: Player): Unit =
    val user = player.user.get
    GuiPaginator.newBuilder[ClassType]()
      .setCollection(ClassType.values.filter(_.shouldDisplay).toList.asJava)
      .setPredicate((cls, filter) => cls.toString.toLowerCase.contains(filter.toLowerCase))
      .setBackAction(_ => mainMenu(player))
      .setItemGenerator((cls, _) => new GuiComponent[Unit]() {
        override def handleClick(event: InventoryClickEvent): Unit =
          event.setCancelled(true)
          user.cls = Some(cls)
          user.weapon = Some(cls.weaponType)
          mainMenu(player)

        override def render(): ItemStack = ItemStackBuilder.of(cls.weaponType.material)
          .flags(ItemFlag.HIDE_ATTRIBUTES)
          .name(s"&e&l${cls.displayName}")
          .lore(
            "",
            s"&7Weapon: &a${cls.weaponType.toString.split("_").map(_.toLowerCase.capitalize).mkString(" ")}",
            s"&7Wielder: ${cls.classRole.colorize(cls.wielderName)}",
            s"&7Wielder Title: ${cls.classRole.colorize(cls.wielderTitle)}",
            "",
            if user.cls.contains(cls) then "&a&lSELECTED" else "&6Click &eto select",
          )
          .build()
      })
      .build(player)

  /**
   * Chooses a soul for the soulhacker class.
   *
   * @param player the player
   */
  def chooseSoulhackerSoul(player: Player): Unit =
    val user = player.user.get
    GuiPaginator.newBuilder[ClassType]()
      .setCollection(ClassType.values.filter(_.isSoulhacker).toList.asJava)
      .setBackAction(_ => mainMenu(player))
      .setPredicate((cls, filter) => cls.toString.toLowerCase.contains(filter.toLowerCase))
      .setItemGenerator((cls, _) => new GuiComponent[Unit]() {
        override def handleClick(event: InventoryClickEvent): Unit =
          event.setCancelled(true)
          user.cls = Some(cls)
          mainMenu(player)

        override def render(): ItemStack = ItemStackBuilder.of(Material.CHEST)
          .name(s"&e&l${cls.soulName}")
          .lore(
            "",
            if user.cls.contains(cls) then "&a&lSELECTED" else "&6Click &eto select",
          )
          .build()
      })
      .build(player)

  /**
   * Opens a menu for the player to pick a weapon.
   *
   * @param player the player
   */
  def chooseWeapon(player: Player): Unit =
    val model = GuiModel(27, "Choose a weapon")
    val user = player.user.get
    model.mount(22, CloseInventoryComponent())
    model.mount(21, new GuiComponent[Unit]() {
      override def handleClick(event: InventoryClickEvent): Unit =
        event.setCancelled(true)
        mainMenu(player)

      override def render(): ItemStack = ItemStackBuilder.of(Material.ARROW)
        .name("&a&lBack")
        .lore("&7Go back to main menu")
        .build()
    })

    model.mount(11, new GuiComponent[Unit]() {
      override def handleClick(event: InventoryClickEvent): Unit =
        event.setCancelled(true)
        user.weapon = Some(user.cls.get.weaponType)
        mainMenu(player)

      override def render(): ItemStack = ItemStackBuilder.of(user.cls.map(_.weaponType.material).get)
        .name(s"&e&l${user.cls.map(_.weaponType.displayName).get}")
        .lore(
          "&8Normal",
          "",
          if user.weapon.contains(user.cls.map(_.weaponType).get) then "&a&lSELECTED" else "&6Click &eto select"
        )
        .build()
    })
    model.mount(15, new GuiComponent[Unit]() {
      override def handleClick(event: InventoryClickEvent): Unit =
        event.setCancelled(true)
        user.weapon = Some(user.cls.get.upgradedWeaponType.get)
        mainMenu(player)

      override def render(): ItemStack = ItemStackBuilder.of(user.cls.map(_.upgradedWeaponType.get.material).get)
        .name(s"&e&l${user.cls.map(_.upgradedWeaponType.get.displayName).get}")
        .lore(
          "&8Upgraded",
          "",
          if user.weapon.contains(user.cls.map(_.upgradedWeaponType).get) then "&a&lSELECTED" else "&6Click &eto select"
        )
        .build()
    })

    model.open(player)
  end chooseWeapon
