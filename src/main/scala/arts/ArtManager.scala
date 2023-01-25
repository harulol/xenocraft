package dev.hawu.plugins.xenocraft
package arts

import dev.hawu.plugins.xenocraft.data.{ArtType, ClassType}

import scala.collection.mutable

/**
 * Manager for binding art types to arts.
 */
object ArtManager:

  private val map = mutable.Map.empty[ArtType, Art]

  /**
   * Binds an art to the art manager.
   *
   * @param art the art to bind
   */
  def bind(art: Art): Unit =
    map += art.artType -> art

  /**
   * Unbinds an art from the art manager.
   *
   * @param art the art to unbind
   */
  def unbind(art: Art): Unit =
    map -= art.artType

  /**
   * Unbinds an art type from the art manager.
   *
   * @param artType the art type to unbind
   */
  def unbind(artType: ArtType): Unit =
    map -= artType

  /**
   * Binds all arts provided to the class type.
   *
   * @param cls  the class type
   * @param arts the arts to bind
   */
  def bind(cls: ClassType, arts: ArtType*): Unit =
    cls.arts ++= arts

  /**
   * Retrieves an optional of the art bound to this type.
   *
   * @param artType the art type
   * @return the art option
   */
  def get(artType: ArtType): Option[Art] = map.get(artType)
