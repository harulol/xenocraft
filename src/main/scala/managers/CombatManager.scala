package dev.hawu.plugins.xenocraft
package managers

import UserMap.user
import data.Attributable
import listener.CombatListener
import utils.Hologram

import dev.hawu.plugins.api.events.Events
import org.bukkit.plugin.java.JavaPlugin

/** The singleton object dedicated to managing the battlefield and spawning holograms in the right place.
 */
object CombatManager extends Initializable:

  override def setUp(pl: JavaPlugin) = Events.registerEvents(pl, CombatListener)

  /** Deals some damage to an attributable.
   */
  def damage(attributable: Attributable, value: Double): Unit =
    val damage = value min 9999999 max 0
    attributable.setHp(attributable.hp - damage)
