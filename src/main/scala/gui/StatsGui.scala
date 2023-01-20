package dev.hawu.plugins.xenocraft
package gui

import dev.hawu.plugins.api.Strings
import dev.hawu.plugins.api.gui.pagination.GuiPaginator
import dev.hawu.plugins.api.gui.{GuiComponent, GuiModel}
import dev.hawu.plugins.api.items.ItemStackBuilder
import dev.hawu.plugins.xenocraft.UserMap.{save, user}
import dev.hawu.plugins.xenocraft.data.{Character, ClassType, User}
import dev.hawu.plugins.xenocraft.utils.Formulas
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.inventory.{ItemFlag, ItemStack}

import java.util
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

/**
 * The singleton object dedicated for stats
 * related GUI.
 */
object StatsGui:

  /**
   * The main menu of the stats GUI.
   *
   * @param player The player to open the GUI for.
   */
  def mainMenu(player: Player): Unit =
    val model = GuiModel(54, "Your Stats")
    val user = player.user.get

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
          s"&fCritical Rate: &a${Formulas.calculateDisplayCritRate(user).intValue}",
          s"&fBlock Rate: &a${Formulas.calculateDisplayBlockRate(user).intValue}",
          s"&fPhys. Def: &a${Formulas.calculateDisplayPhysDefense(user).intValue}",
          s"&fEther Def: &a${Formulas.calculateDisplayEtherDefense(user).intValue}",
        )
        .transformed[SkullMeta](_.setOwningPlayer(player))
        .build()
    })

    model.mount(21, new GuiComponent[Unit]() {
      override def handleClick(event: InventoryClickEvent): Unit =
        event.setCancelled(true)
        if event.isRightClick then
          user.char = None
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

    model.mount(23, new GuiComponent[Unit]() {
      override def handleClick(event: InventoryClickEvent): Unit =
        event.setCancelled(true)
        if event.isRightClick then
          user.cls = None
        else
          chooseClass(player)

      override def render(): ItemStack = ItemStackBuilder.of(Material.IRON_CHESTPLATE)
        .flags(ItemFlag.HIDE_ATTRIBUTES)
        .name("&e&lYour Class")
        .lore(
          "",
          s"&fClass: &a${user.cls.map(_.toString.toLowerCase.capitalize).getOrElse("&cNone")}",
          "",
          "&6Click &eto change",
          "&6Right click &eto deselect",
        )
        .build()
    })

    model.open(player)

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
          .transform { meta =>
            val lore = mutable.ArrayBuffer.empty[String]
            lore += ""
            lore ++= description
            lore += ""

            lore += s"&7Base HP: &a${c.baseHp}"
            lore += s"&7Base Attack: &a${c.baseAttack}"
            lore += s"&7Base Healing: &a${c.baseHealingPower}"
            lore += s"&7Base Dexterity: &a${c.baseDexterity}"
            lore += s"&7Base Agility: &a${c.baseAgility}"

            lore += ""
            if user.char.contains(c) then
              lore += "&a&lSELECTED"
            else lore += "&6Click &eto select"

            meta.setLore(lore.map(Strings.color).asJava)
          }
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
      .setCollection(ClassType.values.toList.asJava)
      .setPredicate((cls, filter) => cls.toString.toLowerCase.contains(filter.toLowerCase))
      .setBackAction(_ => mainMenu(player))
      .setItemGenerator((cls, _) => new GuiComponent[Unit]() {
        override def handleClick(event: InventoryClickEvent): Unit =
          event.setCancelled(true)
          user.cls = Some(cls)
          mainMenu(player)

        override def render(): ItemStack = ItemStackBuilder.of(cls.upgradedWeaponType.getOrElse(cls.weaponType).material)
          .flags(ItemFlag.HIDE_ATTRIBUTES)
          .name(s"&e&l${cls.toString.split("_").map(_.toLowerCase.capitalize).mkString(" ")}")
          .lore(
            "",
            s"&7Weapon: &a${cls.weaponType.toString.split("_").map(_.toLowerCase.capitalize).mkString(" ")}",
            s"&7Upgraded Weapon: &a${cls.upgradedWeaponType.map(_.toString.split("_").map(_.toLowerCase.capitalize).mkString(" ")).getOrElse("None")}",
            "",
            if user.cls.contains(cls) then "&a&lSELECTED" else "&6Click &eto select",
          )
          .build()
      })
      .build(player)
