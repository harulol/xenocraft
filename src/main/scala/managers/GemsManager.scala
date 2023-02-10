package dev.hawu.plugins.xenocraft
package managers

import data.{GemType, User}
import events.gems.{GemBindEvent, GemEffectApplyEvent, GemEffectUnapplyEvent, GemUnbindEvent}
import gui.GemsGUI.{gemLevelSelectionMenu, given_Option_LanguageModule, openGems}

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/** Represents a manager for applying and unapplying gems.
  */
object GemsManager extends Initializable:

  override def setUp(pl: JavaPlugin) = ()

  /** Binds a gem by moving the selected gem to the provided slot if that kind of gem is already equipped. Otherwise, put the gem in the
    * slot, replacing whatever was in it.
    *
    * This also applies the effects of the gems.
    *
    * @param user
    *   the user whose gems selection is being affected
    * @param gem
    *   the gem type
    * @param level
    *   the level of the gem
    * @param slot
    *   the slot index of the gem
    */
  def bindGem(user: User, gem: GemType, level: Int, slot: Int): Unit =
    val event = GemBindEvent(user, slot, gem, level)
    Bukkit.getPluginManager.callEvent(event)
    if event.isCancelled then return ()

    val generalIndex = isGemEquipped(user, event.gem)
    if generalIndex >= 0 && generalIndex != event.slot then
      // Unapply the effects first.
      Option(user.gems(generalIndex)).foreach((g, lvl) => unapplyGem(user, g, lvl))
      user.gems(generalIndex) = user.gems(slot) // Just moving, no need to apply effects.
      user.gems(slot) = event.gem -> event.level
      applyGem(user, event.gem, event.level)
    else
      // Unapply the effects first, then bind gem, then apply the new effects.
      Option(user.gems(slot)).foreach((g, lvl) => unapplyGem(user, g, lvl))
      user.gems(slot) = event.gem -> event.level
      applyGem(user, event.gem, event.level)

  /** Apply a gem's effects. This may cause duplicate effects if you don't [[unapplyGem]] first.
    *
    * This only applies the effects, it doesn't actually bind the gem to the gems array.
    *
    * @param gem
    *   the gem
    * @param level
    *   the level
    */
  def applyGem(user: User, gem: GemType, level: Int): Unit =
    val event = GemEffectApplyEvent(user)
    Bukkit.getPluginManager.callEvent(event)
    if event.isCancelled then return ()

    gem match
      case GemType.TAILWIND         => user.flatAgility += gem.value1At(level)
      case GemType.STEEL_PROTECTION => user.noncombatFlatBlock += (gem.value1At(level) / 100.0)
      case GemType.BRIMMING_SPIRIT  => user.artAggroGeneration += gem.value1At(level)
      case GemType.LIFEBEARER       => user.flatHealing += gem.value1At(level)
      case GemType.SOOTHING_BREATH =>
        user.allyHpRestore += gem.value1At(level)
        user.flatHealing += gem.value2At(level)
      case GemType.LIFESAVING_EXPERTISE =>
        user.allyReviveSpeed += gem.value1At(level)
        user.flatHealing += gem.value2At(level)
      case GemType.SWELLING_BLESSING   => user.buffPower += gem.value1At(level)
      case GemType.REFINED_BLESSING    => user.buffDurationBonus += gem.value1At(level)
      case GemType.STEELCLEAVER        => user.flatAttack += gem.value1At(level)
      case GemType.ACCURATE_GRACE      => user.flatDexterity += gem.value1At(level)
      case GemType.ANALYZE_WEAKNESS    => user.critDamage += gem.value1At(level)
      case GemType.SWELLING_SCOURGE    => user.debuffPower += gem.value1At(level)
      case GemType.REFINED_INCANTATION => user.debuffDurationBonus += gem.value1At(level)
      case GemType.IRON_CLAD           => user.flatHp += gem.value1At(level)
      case GemType.STEADY_STRIKER      => user.rechargeSpeed += gem.value1At(level)
      case GemType.DOUBLESTRIKE        => user.doubleHits += gem.value1At(level)
      case GemType.DISPERSE_BLOODLUST  => user.artAggroGeneration -= gem.value1At(level)
      case _                           => ()

  /** Checks if the provided gem is already equipped.
    *
    * @param gem
    *   the gem to check
    * @return
    *   the index of the gem if it is equipped, -1 if not
    */
  def isGemEquipped(user: User, gem: GemType, level: Int = 0): Int =
    if level >= 1 then user.gems.zipWithIndex.filter(_._1 != null).filter(_._1 == (gem, level)).map(_._2).headOption.getOrElse(-1)
    else user.gems.zipWithIndex.filter(_._1 != null).filter(_._1._1 == gem).map(_._2).headOption.getOrElse(-1)

  /** Logic to unapply a gem, removing the gem's special buffs. This only removes the effects, it doesn't actually unbind the gem from the
    * gems array.
    *
    * @param gem
    *   the gem to unapply
    * @param level
    *   the level of the gem
    */
  def unapplyGem(user: User, gem: GemType, level: Int): Unit =
    val event = GemEffectUnapplyEvent(user)
    Bukkit.getPluginManager.callEvent(event)
    if event.isCancelled then return ()

    gem match
      case GemType.TAILWIND         => user.flatAgility -= gem.value1At(level)
      case GemType.STEEL_PROTECTION => user.noncombatFlatBlock -= (gem.value1At(level) / 100.0)
      case GemType.BRIMMING_SPIRIT  => user.artAggroGeneration -= gem.value1At(level)
      case GemType.LIFEBEARER       => user.flatHealing -= gem.value1At(level)
      case GemType.SOOTHING_BREATH =>
        user.allyHpRestore -= gem.value1At(level)
        user.flatHealing -= gem.value2At(level)
      case GemType.LIFESAVING_EXPERTISE =>
        user.allyReviveSpeed -= gem.value1At(level)
        user.flatHealing -= gem.value2At(level)
      case GemType.SWELLING_BLESSING   => user.buffPower -= gem.value1At(level)
      case GemType.REFINED_BLESSING    => user.buffDurationBonus -= gem.value1At(level)
      case GemType.STEELCLEAVER        => user.flatAttack -= gem.value1At(level)
      case GemType.ACCURATE_GRACE      => user.flatDexterity -= gem.value1At(level)
      case GemType.ANALYZE_WEAKNESS    => user.critDamage -= gem.value1At(level)
      case GemType.SWELLING_SCOURGE    => user.debuffPower -= gem.value1At(level)
      case GemType.REFINED_INCANTATION => user.debuffDurationBonus -= gem.value1At(level)
      case GemType.IRON_CLAD           => user.flatHp -= gem.value1At(level)
      case GemType.STEADY_STRIKER      => user.rechargeSpeed -= gem.value1At(level)
      case GemType.DOUBLESTRIKE        => user.doubleHits -= gem.value1At(level)
      case GemType.DISPERSE_BLOODLUST  => user.artAggroGeneration += gem.value1At(level)
      case _                           => ()

  /** Unbinds all gems and removes all effects.
    */
  def unbindAll(user: User): Unit = (0 to 2).map(user -> _).foreach(unbind)

  /** Unbinds a gem from a slot.
    */
  def unbind(user: User, slot: Int): Unit =
    val event = GemUnbindEvent(user, slot)
    Bukkit.getPluginManager.callEvent(event)
    if event.isCancelled then return ()

    Option(user.gems(slot)).foreach((g, lvl) => unapplyGem(user, g, lvl))
    user.gems(slot) = null
