package dev.hawu.plugins.xenocraft
package managers

import UserMap.user
import data.ClassRole
import listener.AggroListener
import managers.AggroManager.drawAggroLines

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.api.events.Events
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.{Entity, LivingEntity, Mob, Player}
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.{Bukkit, Color, Location, Particle}

import java.util.UUID
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

/** A singleton object dedicated to handling aggro values.
  */
object AggroManager extends Initializable:

  private val aggroMap = mutable.Map.empty[UUID, UUID]

  override def setUp(pl: JavaPlugin): Unit =
    Events.registerEvents(pl, AggroListener)
    Tasks.run(_ =>
      filterAll()
      drawAggroLines(),
    ).delay(0).period(5).plugin(pl).run()

  /** Filters out all bad data from the aggro map.
    */
  def filterAll(): Unit = for (entityUUID, playerUUID) <- aggroMap do
    val entity = Bukkit.getEntity(entityUUID)
    val player = Bukkit.getPlayer(playerUUID)
    if player == null || !player.isOnline || entity == null || entity.isDead then aggroMap.remove(entityUUID)
    else if !entity.isInstanceOf[Mob] then aggroMap.remove(entityUUID)
    clear(entity.asInstanceOf[Mob], player)

  /** Clears a mob from a player and removes them from its bossbar.
    */
  def clear(mob: Mob, p: Player): Unit = if mob.getTarget == null then
    aggroMap.remove(mob.getUniqueId)
    EnemyManager.getBossbar(mob).foreach(_.removePlayer(p))

  /** Attempts to draw aggro lines for every entity in the aggro map.
    */
  def drawAggroLines(): Unit = for (entity, player) <- aggroMap.map[Entity, Player]((e, p) => Bukkit.getEntity(e) -> Bukkit.getPlayer(p)) do
    drawAggroLine(entity.asInstanceOf[LivingEntity], player)

  /** Draws the aggro target line from the [[entity]] to the [[player]].
    */
  def drawAggroLine(entity: LivingEntity, player: Player): Unit =
    val user = player.user.get
    val dustOptions = if user.cls.exists(_.classRole == ClassRole.DEFENDER) then DustOptions(Color.AQUA, 1) else DustOptions(Color.RED, 1)
    drawLine(entity.getEyeLocation.add(0.0, 1.0, 0.0), player.getEyeLocation.add(0.0, 1.0, 0.0), dustOptions)

  private def drawLine(from: Location, to: Location, dustOptions: DustOptions): Unit =
    val distance = to.distance(from)
    val count = distance.longValue * 3
    val step = distance / count
    val direction = to.clone().subtract(from).toVector.normalize().multiply(step)

    var angle = 0.0
    var trigStep = math.Pi / count
    val current = from.clone()

    while current.distanceSquared(to) >= step * step do
      val elevation = math.sin(angle) * 5
      val loc = current.clone().add(0.0, elevation, 0.0)

      current.getWorld.spawnParticle(Particle.REDSTONE, loc, 1, 0.0, 0.0, 0.0, 0.0, dustOptions)
      current.add(direction)
      angle += trigStep
  end drawLine

  override def tearDown(pl: JavaPlugin) = aggroMap.clear()

  /** Binds an entity-player aggro pair.
    */
  def map(entity: Mob, player: Player): Unit = aggroMap += entity.getUniqueId -> player.getUniqueId

  /** Clears a mob out of the map, regardless of the target and returns the optional value of the target UUID.
    */
  def clear(entity: Mob): Option[UUID] = aggroMap.remove(entity.getUniqueId)

  /** Clears all aggro binding for a player.
    */
  def clearAll(player: Player): Unit = aggroMap.filterInPlace((_, p) => p == player.getUniqueId)

  /** Returns true if the player is currently being targeted by any mob.
    */
  def isBeingTargeted(player: Player): Boolean = aggroMap.values.toList.contains(player.getUniqueId)
