package dev.hawu.plugins.xenocraft
package gui

import UserMap.user
import data.{ClassNation, ClassType, SkillType, User}
import I18n.tl

import dev.hawu.plugins.xenocraft.ModuleHolder
import dev.hawu.plugins.api.i18n.{LanguageModule, Locale}
import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.Strings
import dev.hawu.plugins.api.items.ItemStackBuilder
import dev.hawu.plugins.api.gui.GuiComponent
import dev.hawu.plugins.api.gui.templates.StaticComponent
import dev.hawu.plugins.api.gui.pagination.GuiPaginator
import org.bukkit.entity.Player
import org.bukkit.inventory.{ItemFlag, ItemStack}
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent

import scala.jdk.CollectionConverters.*

/** The singleton for dealing with skills.
 */
object SkillsGUI extends ModuleHolder("skills-ui"):

  given Option[LanguageModule] = module

  /** Opens up a pagination to choose master skills.
    *
    * @param player
    *   the player to open to
    */
  def openMasterSkills(player: Player, slot: Int): Unit =
    given Player = player

    val user = player.user.get
    var skills = SkillType.values

    if user.cls.exists(_.isSoulhacker) then skills = skills.filter(s => s.isSoulhacker && s != SkillType.SOUL_HACK)
    else if user.cls.isDefined then skills = skills.filter(_.isMaster).filter(_.cls.get != user.cls.get)
    else skills = Array.ofDim(0)

    GuiPaginator.newBuilder[SkillType]().setCollection(skills.toList.asJava).setModelSupplier(() => {
      val model = I18n.translateModel(54, "master-skill-title")
      MainGUI.applyNavigationBar(model, '5', Material.RED_STAINED_GLASS_PANE)
      model
    }).setAllowedSlots(MainGUI.getPaginationSlots).setItemGenerator((skill, _) =>
      new GuiComponent[Unit] {
        override def handleClick(event: InventoryClickEvent): Unit =
          event.setCancelled(true)
          user.equipMasterSkill(skill, slot)
          openSkills(player)

        override def render(): ItemStack = getSkillDisplay(skill, true)
      },
    ).setPredicate(null).setPreviousButtonSlots(Set(47).map(Integer.valueOf).asJava).setNextButtonSlots(Set(53).map(Integer.valueOf).asJava)
      .setBackSlots(Set(2).map(Integer.valueOf).asJava).setBackAction(_ => openSkills(player)).build(player)

  end openMasterSkills

  /** Opens the menu to select skills for the player.
    *
    * @param player
    *   the player
    */
  def openSkills(player: Player): Unit =
    given Player = player

    val user = player.user.get

    val model = I18n.translateModel(54, "skills-title")
    MainGUI.applyNavigationBar(model, '5', Material.RED_STAINED_GLASS_PANE)
    if user.cls.exists(_.isSoulhacker) then model.mount(2, new SoulhackerSoulSelectComponent(user, player))

    // Tooltip stacks.
    model.mount(21, StaticComponent(I18n.translateItem(Material.MAP -> 1, "skill-tooltip")))
    model.mount(30, StaticComponent(I18n.translateItem(Material.MAP -> 1, "master-skill-tooltip")))

    // Retrieve class skills as this should not be customizable.
    val classSkills = Array.ofDim[SkillType](4)
    if user.cls.exists(_.isSoulhacker) then classSkills(0) = SkillType.SOUL_HACK
    else SkillType.values.filter(_.cls.exists(_ == user.cls.orNull)).copyToArray(classSkills)
    (22 to 25).foreach(i => model.mount(i, StaticComponent(getSkillDisplay(classSkills(i - 22)))))

    // Master skills are however, customizable.
    user.masterSkills.zipWithIndex.foreach((skill, index) => model.mount(index + 32, new MasterSkillComponent(user, player, index)))

    model.open(player)

  end openSkills

  private def getSkillDisplay(skill: SkillType, master: Boolean = false)(using player: Player): ItemStack =
    val locale = UserAdapter.getAdapter.getUser(player).getLocale
    if skill == null then
      I18n.translateItem(Material.BLACK_STAINED_GLASS_PANE -> 1, if master then "empty-master-skill" else "empty-class-skill")
    else
      ItemStackBuilder.from(I18n.translateItem(
        skill.material -> 1,
        if master then "master-skill" else "skill",
        "name" -> skill.name(locale),
        "description" -> Strings.chop(skill.description(locale), 32),
      )).flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS).build()

  /** The component for handling clicks on master skills
   */
  class MasterSkillComponent(private var user: User, private var player: Player, private val slot: Int) extends GuiComponent[Unit]():

    private val skill = user.masterSkills(slot)

    override def componentWillUnmount(): Unit =
      user = null
      player = null

    override def handleClick(event: InventoryClickEvent): Unit =
      event.setCancelled(true)
      openMasterSkills(player, slot)

    override def render(): ItemStack = getSkillDisplay(skill, true)(using player)

  /** Opens the menu to select soulhacker's souls for the player
    *
    * @param player
    *   the player
    */
  def openSoulSelection(player: Player): Unit =
    given Player = player

    GuiPaginator.newBuilder[ClassType]().setCollection(ClassType.values.filter(_.isSoulhacker).toList.asJava)
      .setAllowedSlots(MainGUI.getPaginationSlots).setModelSupplier(() => {
        val model = I18n.translateModel(54, "soul-select-title")
        MainGUI.applyNavigationBar(model, '5', Material.RED_STAINED_GLASS_PANE)
        model
      }).setPredicate(null).setItemGenerator((soul, _) => new SoulhackerSoulComponent(player.user.get, player, soul))
      .setBackAction(_ => openSkills(player)).setBackSlots(Set(2).map(Integer.valueOf).asJava).build(player)

  /** The component for selecting a soul for soulhacker builds.
    *
    * @param user
    *   the user
    * @param player
    *   the player
    * @param soul
    *   the soul
    */
  class SoulhackerSoulComponent(private var user: User, private var player: Player, private val soul: ClassType)
    extends GuiComponent[Unit]():

    private val locale = UserAdapter.getAdapter.getUser(player).getLocale
    private val soulName = soul.soulName(locale)
    private val description = module.get.translate(locale, s"${soul.toString.split("_")(1).toLowerCase()}-soul-desc")

    override def componentWillUnmount(): Unit =
      player = null
      user = null

    override def handleClick(event: InventoryClickEvent): Unit =
      event.setCancelled(true)
      user.cls = Some(soul)
      user.lastSoulhackerSoul = Some(soul)
      openSkills(player)

    override def render(): ItemStack = I18n.translateItem(
      Material.CHEST_MINECART -> 1,
      "soul-select",
      "soul" -> soulName,
      "description" -> Strings.chop(description, 32),
      "selection" -> (if user.cls.contains(soul) then "selected".tl(locale) else "not-selected".tl(locale)),
    )(using module, player)

  end SoulhackerSoulComponent

  /** The component for selecting a soul for soulhacker builds.
    *
    * @param user
    *   the user
    * @param player
    *   the player
    */
  class SoulhackerSoulSelectComponent(private var user: User, private var player: Player) extends GuiComponent[Unit]():

    private val soul = user.cls.get
    private val locale = UserAdapter.getAdapter.getUser(player).getLocale

    override def componentWillUnmount(): Unit =
      user = null
      player = null

    override def handleClick(event: InventoryClickEvent): Unit =
      event.setCancelled(true)
      openSoulSelection(player)

    override def render(): ItemStack = I18n
      .translateItem(Material.MINECART -> 1, "soulhacker-soul", "soul" -> soul.soulName(locale))(using module, player)

  end SoulhackerSoulSelectComponent

end SkillsGUI
