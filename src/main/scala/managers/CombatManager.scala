package dev.hawu.plugins.xenocraft
package managers

import UserMap.user
import data.{Attributable, EnemyEntity}
import listener.CombatListener
import utils.Hologram

import dev.hawu.plugins.api.events.Events
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.plugin.java.JavaPlugin

import scala.util.Try

/** The singleton object dedicated to managing the battlefield and spawning holograms in the right place.
 */
object CombatManager extends Initializable:

  override def setUp(pl: JavaPlugin): Unit = Events.registerEvents(pl, CombatListener)

  /** Deals some damage to an attributable.
   */
  def damage(attributable: Attributable, value: Double, color: String = null): Unit =
    val damage = value min 9999999 max 0
    attributable.setHp(attributable.hp - damage)

    attributable match
      case entity: EnemyEntity => EnemyManager.syncBossbar(entity.entity)
      case _ => ()

    if color != null then Try(Bukkit.getEntity(attributable.uuid).asInstanceOf[LivingEntity]).toOption
      .map(_.getEyeLocation).foreach(loc => Hologram.spawnAround(loc, 40, s"$color${damage.round}"))
