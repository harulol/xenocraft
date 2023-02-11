package dev.hawu.plugins.xenocraft
package events.arts

import UserMap.user
import data.ArtType
import events.UserEvent
import events.arts.PlayerUseFusionArtEvent.handlers

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.{Cancellable, HandlerList}

/** An event that is called before the user executes the fusion art.
  *
  * It is guaranteed that the [[classArt]] and [[fusionArt]] are always non-null.
  */
class PlayerUseFusionArtEvent(who: Player, val classArt: ArtType, val fusionArt: ArtType)
  extends PlayerEvent(who) with UserEvent(who.user.get) with Cancellable:

  private var cancelled = false

  override def isCancelled: Boolean = cancelled

  override def setCancelled(cancel: Boolean): Unit = cancelled = cancel

  override def getHandlers = handlers

/** Companion object for [[PlayerUseFusionArtEvent]].
  */
object PlayerUseFusionArtEvent:

  private val handlers = HandlerList()

  def getHandlerList = handlers
