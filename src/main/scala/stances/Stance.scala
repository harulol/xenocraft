package dev.hawu.plugins.xenocraft
package stances

import UserMap.user
import data.User

import org.bukkit.entity.Player
import org.bukkit.event.{HandlerList, Listener}

import java.util.UUID
import scala.collection.mutable

/** Represents a temporary listener that acts as a stance for the player.
  *
  * A stance is effectively just a temporary skill, I guess.
  */
abstract class Stance extends Listener:

  protected val applied = mutable.Set.empty[UUID]

  final def safeApply(user: User): Unit = if isNotApplied(user) then
    apply(user)
    applied += user.uuid

  final def isNotApplied(user: User): Boolean = !applied.contains(user.uuid)

  final def safeUnapply(user: User): Unit = if isApplied(user) then
    unapply(user)
    applied -= user.uuid

  final def isApplied(user: User): Boolean = applied.contains(user.uuid)

  /** Apply the stance to the [[user]].
    */
  def apply(user: User): Unit

  /** Unapply the [[user]] from the stance.
    */
  def unapply(user: User): Unit
