package dev.hawu.plugins.xenocraft
package gui

import dev.hawu.plugins.api.adapters.UserAdapter
import dev.hawu.plugins.api.gui.brushes.BrushRegistry
import dev.hawu.plugins.api.i18n.LanguageModule
import dev.hawu.plugins.xenocraft.UserMap.user
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * The singleton object dedicated to arts GUIs.
 */
object ArtsGUI extends ModuleHolder("arts-ui"):

  given Option[LanguageModule] = module

  /**
   * Opens the arts menu GUI.
   *
   * @param player the player
   */
  def openArts(player: Player): Unit =
    given Player = player

    val user = player.user.get
    val locale = UserAdapter.getAdapter.getUser(player).getLocale

    // 1 -> main, 2 -> characters, 3 -> arts, 4 -> classes, 5 -> gems, 6 -> accessories
    val model = I18n.translateModel(54, "stats-ui-title")
    val layout = BrushRegistry.layout().setModel(model)
      .setLayout(
        "1-*******",
        "2**-----*",
        "3-*--P--*",
        "4-*-----*",
        "5-*-----*",
        "6-*******",
      ).build()

    model.open(player)
  end openArts
