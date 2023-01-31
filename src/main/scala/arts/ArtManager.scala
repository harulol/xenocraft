package dev.hawu.plugins.xenocraft
package arts

import dev.hawu.plugins.xenocraft.data.{ArtRechargeType, ArtType, ClassType}
import org.bukkit.Material

import scala.collection.mutable

/** Manager for binding art types to arts.
  */
object ArtManager:

  private val map = mutable.Map.empty[ArtType, Art]

  /** Initializes the art manager, binding all arts to its corresponding classes.
    */
  def initialize(): Unit =
    import ArtType.*
    ClassType.SWORDFIGHTER.arts ++= Seq(GROUND_BEAT, AIR_SLASH, EDGE_THRUST, SHADOW_EYE, SWORD_STRIKE, OVERCLOCK_BUSTER)

  /** Retrieves an iterable of all arts.
    *
    * @param includesKeves
    *   whether to include kevesi arts
    * @param includesAgnus
    *   whether to include agnian arts
    * @param includesTalent
    *   whether to include talent arts
    * @return
    */
  def getAllArts(
    includesKeves: Boolean = true,
    includesAgnus: Boolean = true,
    includesTalent: Boolean = true,
  ): Iterable[ArtType] =
    var values = ArtType.values
    if !includesKeves then values = values.filterNot(_.isKevesi)
    if !includesAgnus then values = values.filterNot(_.isAgnian)
    if !includesTalent then values = values.filterNot(_.isTalent)
    values

  /** Binds an art to the art manager.
    *
    * @param art
    *   the art to bind
    */
  def bind(art: Art): Unit = map += art.artType -> art

  /** Unbinds an art from the art manager.
    *
    * @param art
    *   the art to unbind
    */
  def unbind(art: Art): Unit = map -= art.artType

  /** Unbinds an art type from the art manager.
    *
    * @param artType
    *   the art type to unbind
    */
  def unbind(artType: ArtType): Unit = map -= artType

  /** Binds all arts provided to the class type.
    *
    * @param cls
    *   the class type
    * @param arts
    *   the arts to bind
    */
  def bind(cls: ClassType, arts: ArtType*): Unit = cls.arts ++= arts

  /** Retrieves an optional of the art bound to this type.
    *
    * @param artType
    *   the art type
    * @return
    *   the art option
    */
  def get(artType: ArtType): Option[Art] = map.get(artType)

  /** Retrieves the icon of this art, calculated based on its category and its effects.
    *
    * @param art
    *   the art
    * @return
    *   the material
    */
  def getIcon(art: ArtType): Material =
    import dev.hawu.plugins.xenocraft.data.ArtCategory
    art match
      case ArtType.INFINITY_BLADE  => return Material.DIAMOND_SWORD
      case ArtType.UNLIMITED_SWORD => return Material.NETHERITE_SWORD
      case ArtType.DOMINION_FLOWER => return Material.WITHER_ROSE
      case _                       => ()
    art.category match
      case ArtCategory.HEALING  => Material.GOLDEN_APPLE
      case ArtCategory.BUFF     => Material.POTION
      case ArtCategory.CLEANSE  => Material.SPLASH_POTION
      case ArtCategory.DEFENSE  => Material.SHIELD
      case ArtCategory.ETHER    => Material.IRON_HORSE_ARMOR
      case ArtCategory.FIELD    => Material.CHEST_MINECART
      case ArtCategory.STANCE   => Material.ELYTRA
      case ArtCategory.PHYSICAL => Material.IRON_SWORD
      case ArtCategory.TAUNT    => Material.BLAZE_ROD
      case ArtCategory.DEBUFF   => Material.TIPPED_ARROW
  end getIcon

end ArtManager
