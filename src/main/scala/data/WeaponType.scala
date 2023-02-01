package dev.hawu.plugins.xenocraft
package data

import org.bukkit.Material
import dev.hawu.plugins.api.i18n.Locale
import dev.hawu.plugins.xenocraft.gui.ClassesGUI

/** Represents a type of Blade a class holder can have.
  *
  * For probably good reasons, only the main cast's Blades have upgraded versions, which is why there 2 Blades with
  * different stats that are bound to the same class type.
  *
  * See [[ClassType]].
  */
enum WeaponType(
  val material: Material,
  val durability: Int = 0,
  val weaponAttack: Double,
  val weaponCrit: Double,
  val weaponBlock: Double,
  val weaponStability: Double,
):

  /** Retrieves the localized display name.
    *
    * @param locale
    *   the locale
    * @return
    *   the display name
    */
  def displayName(locale: Locale): String = ClassesGUI.getModule
    .translate(locale, s"${toString().replace('_', '-').toLowerCase()}")

  case VEILED_SWORD
    extends WeaponType(
      material = Material.IRON_SWORD,
      weaponAttack = 575,
      weaponCrit = 0.2,
      weaponBlock = 0.1,
      weaponStability = 0.08,
    )

  case TRUTHSINGER
    extends WeaponType(
      material = Material.DIAMOND_SWORD,
      weaponAttack = 690,
      weaponCrit = 0.24,
      weaponBlock = 0.12,
      weaponStability = 0.08,
    )

  case DUAL_MOONBLADES
    extends WeaponType(
      material = Material.ENDER_PEARL,
      weaponAttack = 524,
      weaponCrit = 0.2,
      weaponBlock = 0.35,
      weaponStability = 0.05,
    )

  case SUNDANCERS
    extends WeaponType(
      material = Material.ENDER_EYE,
      weaponAttack = 629,
      weaponCrit = 0.24,
      weaponBlock = 0.42,
      weaponStability = 0.05,
    )

  case GUNROD
    extends WeaponType(
      material = Material.FISHING_ROD,
      weaponAttack = 442,
      weaponCrit = 0.1,
      weaponBlock = 0.1,
      weaponStability = 0.03,
    )

  case CADUCEUS_GUNROD
    extends WeaponType(
      material = Material.CARROT_ON_A_STICK,
      weaponAttack = 530,
      weaponCrit = 0.12,
      weaponBlock = 0.12,
      weaponStability = 0.03,
    )

  case SPELL_TAGS_MONDO
    extends WeaponType(
      material = Material.PAPER,
      weaponAttack = 458,
      weaponCrit = 0.1,
      weaponBlock = 0.15,
      weaponStability = 0.02,
    )

  case SPELL_TAGS_TOTALITY
    extends WeaponType(
      material = Material.FILLED_MAP,
      weaponAttack = 550,
      weaponCrit = 0.12,
      weaponBlock = 0.18,
      weaponStability = 0.02,
    )

  case SHIELDBLADE
    extends WeaponType(
      material = Material.OBSIDIAN,
      weaponAttack = 534,
      weaponCrit = 0.08,
      weaponBlock = 0.35,
      weaponStability = 0.12,
    )

  case ADAMANT_SHELL
    extends WeaponType(
      material = Material.BEDROCK,
      weaponAttack = 641,
      weaponCrit = 0.1,
      weaponBlock = 0.42,
      weaponStability = 0.12,
    )

  case COMET_MALLET
    extends WeaponType(
      material = Material.IRON_AXE,
      weaponAttack = 620,
      weaponCrit = 0.2,
      weaponBlock = 0.15,
      weaponStability = 0.16,
    )

  case METEOR_SMASHER
    extends WeaponType(
      material = Material.DIAMOND_AXE,
      weaponAttack = 744,
      weaponCrit = 0.24,
      weaponBlock = 0.18,
      weaponStability = 0.16,
    )

  case DUAL_RAPIERS
    extends WeaponType(
      material = Material.IRON_HOE,
      weaponAttack = 585,
      weaponCrit = 0.3,
      weaponBlock = 0.08,
      weaponStability = 0.04,
    )

  case RELIEF_RIFLE
    extends WeaponType(
      material = Material.HOPPER,
      weaponAttack = 448,
      weaponCrit = 0.06,
      weaponBlock = 0.08,
      weaponStability = 0.02,
    )

  case GUTSY_GLADIUS
    extends WeaponType(
      material = Material.SHIELD,
      weaponAttack = 508,
      weaponCrit = 0.08,
      weaponBlock = 0.4,
      weaponStability = 0.06,
    )

  case MARTIAL_LINKSTAFF
    extends WeaponType(
      material = Material.END_ROD,
      weaponAttack = 534,
      weaponCrit = 0.24,
      weaponBlock = 0.12,
      weaponStability = 0.08,
    )

  case VARIABLE_ARMS
    extends WeaponType(
      material = Material.BLAZE_SPAWN_EGG,
      weaponAttack = 544,
      weaponCrit = 0.2,
      weaponBlock = 0.2,
      weaponStability = 0.1,
    )

  case GUN_DRONES
    extends WeaponType(
      material = Material.STONE_HOE,
      weaponAttack = 549,
      weaponCrit = 0.1,
      weaponBlock = 0.1,
      weaponStability = 0.04,
    )

  case DISC_LAUNCHER
    extends WeaponType(
      material = Material.MUSIC_DISC_11,
      weaponAttack = 483,
      weaponCrit = 0.15,
      weaponBlock = 0.06,
      weaponStability = 0.08,
    )

  case LUMINESCENT_BOW
    extends WeaponType(
      material = Material.BOW,
      weaponAttack = 554,
      weaponCrit = 0.3,
      weaponBlock = 0.06,
      weaponStability = 0.02,
    )

  case TWINSABER
    extends WeaponType(
      material = Material.DIAMOND_HOE,
      weaponAttack = 534,
      weaponCrit = 0.24,
      weaponBlock = 0.3,
      weaponStability = 0.08,
    )

  case EXPONENTIAL_EDGE
    extends WeaponType(
      material = Material.STONE_SWORD,
      weaponAttack = 575,
      weaponCrit = 0.35,
      weaponBlock = 0.06,
      weaponStability = 0.04,
    )

  case DEFENSE_MACE
    extends WeaponType(
      material = Material.STONE_AXE,
      weaponAttack = 519,
      weaponCrit = 0.2,
      weaponBlock = 0.45,
      weaponStability = 0.08,
    )

  case WAR_STANDARD
    extends WeaponType(
      material = Material.RED_BANNER,
      weaponAttack = 514,
      weaponCrit = 0.16,
      weaponBlock = 0.16,
      weaponStability = 0.06,
    )

  case MIMIC_KNUCKLES
    extends WeaponType(
      material = Material.PINK_DYE,
      durability = 9,
      weaponAttack = 580,
      weaponCrit = 0.15,
      weaponBlock = 0.25,
      weaponStability = 0.16,
    )

  case TURBO_CESTUS
    extends WeaponType(
      material = Material.INK_SAC,
      weaponAttack = 554,
      weaponCrit = 0.36,
      weaponBlock = 0.16,
      weaponStability = 0.04,
    )

  case SIKEN_IVY
    extends WeaponType(
      material = Material.FEATHER,
      weaponAttack = 432,
      weaponCrit = 0.16,
      weaponBlock = 0.08,
      weaponStability = 0.04,
    )

  case HEATSHIMMER_SPEAR
    extends WeaponType(
      material = Material.FLINT_AND_STEEL,
      weaponAttack = 661,
      weaponCrit = 0.25,
      weaponBlock = 0.1,
      weaponStability = 0.16,
    )

  case DOOM_AXES
    extends WeaponType(
      material = Material.GOLDEN_AXE,
      weaponAttack = 635,
      weaponCrit = 0.3,
      weaponBlock = 0.2,
      weaponStability = 0.1,
    )

  case ANIMA_SWORD
    extends WeaponType(
      material = Material.LILY_PAD,
      weaponAttack = 559,
      weaponCrit = 0.2,
      weaponBlock = 0.3,
      weaponStability = 0.04,
    )

  case ELEMENTAL_STAFF
    extends WeaponType(
      material = Material.DIAMOND_HORSE_ARMOR,
      weaponAttack = 671,
      weaponCrit = 0.3,
      weaponBlock = 0.1,
      weaponStability = 0.04,
    )

  case INOSABERS
    extends WeaponType(
      material = Material.EXPERIENCE_BOTTLE,
      weaponAttack = 519,
      weaponCrit = 0.15,
      weaponBlock = 0.35,
      weaponStability = 0.12,
    )

  case LUCKY_SEVEN
    extends WeaponType(
      material = Material.NETHERITE_SWORD,
      weaponAttack = 686,
      weaponCrit = 0.5,
      weaponBlock = 0.5,
      weaponStability = 0.0,
    )

end WeaponType
