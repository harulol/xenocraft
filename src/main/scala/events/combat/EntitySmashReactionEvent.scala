package dev.hawu.plugins.xenocraft
package events.combat

import UserMap.user
import data.{ArtType, Attributable}
import events.combat.EntitySmashReactionEvent.handlers
import managers.EnemyManager

import org.bukkit.entity.{LivingEntity, Mob, Player}
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityEvent

/** Calls when a smash reaction is afflicted on the entity via some arts.
  */
class EntitySmashReactionEvent(entity: LivingEntity, val attacker: Attributable, val arts: Iterable[ArtType]) extends EntityEvent(entity):

  if arts.size < 1 || arts.size > 2 then throw new IllegalArgumentException("The amount of arts must be 1 or 2.")

  val attributable: Attributable = entity match
    case player: Player => player.user.get
    case mob: Mob       => EnemyManager.getEnemy(mob).get

  val artsDamage: Double = arts.flatMap(attacker.artsDamage.get).sum[Double]

  // Since game runs at 30fps, but MC runs at 20tps, convert the reaction frames to 30fps.
  var smashDamageBonus: Double = 0.03 * (attributable.reactionFrames / 20.0 * 30)

  /** Returns the final damage of this smash event coerced legally.
    */
  def finalDamage: Double = totalDamage min 9999999 max 0

  /** Returns the true total damage of this smash event without coercing in the legal range.
    */
  def totalDamage: Double = artsDamage * (1.5 + smashDamageBonus)

  /** Returns the final shock damage of this smash event coerced legally.
    */
  def finalShockDamage: Double = shockDamage min 9999999 max 0

  /** Returns the true shock damage of this smash event without coercing in the legal range. This shock damage should be dealt to enemies in
    * range of the smash, but not to the smash-ee.
    */
  def shockDamage: Double = totalDamage * 0.25

  override def getHandlers: HandlerList = handlers

/** Handler list for [[EntitySmashReactionEvent]].
  */
object EntitySmashReactionEvent:

  private val handlers = HandlerList()

  def getHandlerList: HandlerList = handlers
