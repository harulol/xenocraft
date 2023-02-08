package dev.hawu.plugins.xenocraft
package combat

import dev.hawu.plugins.api.{Strings, Tasks}
import org.bukkit.Location
import org.bukkit.entity.ArmorStand

import java.util.concurrent.ThreadLocalRandom
import scala.collection.mutable

/** Represents a floating text from an invisible armor stand, creating a holographic display.
  */
class Hologram(
  private var location: Location,
  private val lines: mutable.ArrayBuffer[String] = mutable.ArrayBuffer.empty[String],
  private val selfDestruct: Long = -1,
):

  private val stands = mutable.ArrayBuffer.empty[ArmorStand]

  /** Nudges the hologram by a random amount.
    */
  def nudgeLocation(): Unit = location = location.add(randomGaussian, randomGaussian, randomGaussian)

  private def randomGaussian: Double =
    if ThreadLocalRandom.current().nextBoolean() then ThreadLocalRandom.current().nextGaussian() / 2
    else -ThreadLocalRandom.current().nextGaussian() / 2

  /** Adds one line to the hologram and immediately spawns it.
    *
    * @param line
    *   the line to add
    */
  def add(line: String): Unit =
    lines += line
    stands += makeArmorStand(location.clone().subtract(0.0, 0.3 * stands.size, 0.0), line)

  if selfDestruct >= 0 then
    Tasks.run(() => {
      despawn()
      lines.clear()
    }).delay(selfDestruct).run()

  /** Attempts to spawn all holograms.
    */
  def spawn(): Unit =
    despawn()
    val current = location.clone()
    for line <- lines do
      stands += makeArmorStand(current, line)
      current.subtract(0.0, 0.3, 0.0)

  private def makeArmorStand(location: Location, name: String): ArmorStand =
    val stand: ArmorStand = location.getWorld.spawn[ArmorStand](
      location,
      classOf[ArmorStand],
      stand => {
        stand.setVisible(false)
        stand.setMarker(true)
        stand.setCustomNameVisible(true)
        stand.setCanPickupItems(false)
        stand.setCollidable(false)
        stand.setInvulnerable(true)
        stand.setGravity(false)
        stand.setCustomName(Strings.color(name))
      },
    )
    stand

  /** Despawns all armor stands and clears all references.
    */
  def despawn(): Unit =
    stands.foreach(_.remove())
    stands.clear()

  /** Checks if the hologram is already spawned.
    *
    * @return
    *   whether it is spawned
    */
  def isSpawned: Boolean = stands.nonEmpty

  /** Updates one line of the hologram and immediately updates it.
    *
    * @param index
    *   the index of the line
    * @param line
    *   the line to update
    */
  def update(index: Int, line: String): Unit =
    lines(index) = line
    stands(index).setCustomName(Strings.color(line))

  /** Removes one line of the hologram and immediately updates it.
    *
    * @param index
    *   the index to remove
    */
  def remove(index: Int): Unit =
    lines.remove(index)
    stands.remove(index).remove()
    stands.slice(index, stands.size).foreach(as => as.teleport(as.getLocation.add(0.0, 0.3, 0.0)))

  /** Moves all holograms to a new location.
    *
    * @param location
    *   the new location
    */
  def move(location: Location): Unit =
    this.location = location
    val current = location
    this.stands.foreach(e => {
      e.teleport(current)
      current.subtract(0.0, 0.3, 0.0)
    })

end Hologram
