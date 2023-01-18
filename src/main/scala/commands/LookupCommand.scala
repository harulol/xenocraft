package dev.hawu.plugins.xenocraft
package commands

import dev.hawu.plugins.api.commands.*
import dev.hawu.plugins.xenocraft.arts.{ArtRechargeType, EnumArt}
import dev.hawu.plugins.xenocraft.classes.EnumClass
import dev.hawu.plugins.xenocraft.combat.StatusEffect
import dev.hawu.plugins.xenocraft.skills.EnumSkill
import org.jetbrains.annotations.TestOnly

import java.util
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Try}

/**
 * A testing command called /lookup to check
 * if stuff has loaded properly;.
 */
@TestOnly
class LookupCommand extends CommandRegistrable:

  @Command("lookup")
  def lookupCmd(source: CommandSource, args: CommandArgument): Unit =
    source.sendMessage("&c/lookup <art|status|skill|class> [stuff]")

  @TabExecute("lookup")
  def lookupTab(source: CommandSource, args: CommandArgument): util.List[String] =
    List("art", "status", "skill", "class").asJava

  @Command("lookup art")
  def lookupArtCmd(source: CommandSource, args: CommandArgument): Unit =
    if args.size() < 1 then
      source.sendMessage("&c/lookup art <name>")
    else
      val lookupTerm = args.getUnderlyingList.asScala.mkString("_").toUpperCase
      val result: Try[EnumArt] = Try(EnumArt.valueOf(lookupTerm))
      result match
        case Success(value) =>
          val rechargeUnit = value.rechargeType match
            case ArtRechargeType.TIME_RECHARGE => "s"
            case ArtRechargeType.AUTO_ATTACK_RECHARGE => " hits"
            case ArtRechargeType.ROLE_ACTION_RECHARGE => " actions"

          source.sendMessage(s"&6&lArt ${value.displayName}:")
          source.sendMessage(s"&6* &e&lArt Type: &a${value.artType.displayName}")
          source.sendMessage(s"&6* &e&lArea of Effect: &a${value.aoeType.displayName}")
          source.sendMessage(s"&6* &e&lPower Multiplier: &c${value.powerMultiplier}%")
          source.sendMessage(s"&6* &e&lRecharge: &a${if value.rechargeGauge.isWhole then value.rechargeGauge.toInt else value.rechargeGauge}$rechargeUnit")

          if value.buff.isDefined then
            val colorCode = if value.buff.get.isBuff then "&b" else "&c"
            source.sendMessage(s"&6* &e&lBuff/Debuff: $colorCode${value.buff.get.displayName}")

          if value.reaction.isDefined then
            source.sendMessage(s"&6* &e&lReaction: &a${value.reaction.get.toString.toLowerCase.capitalize}")

          if value.effect.isDefined then
            source.sendMessage(s"&7( ${value.effect.get} )")
        case Failure(_) =>
          source.sendMessage("&cNo art found with that name.")

  @TabExecute("lookup art")
  def lookupArtTab(source: CommandSource, args: CommandArgument): util.List[String] =
    EnumArt.values.map(_.toString).toList.asJava

  @Command("lookup status")
  def lookupStatusCmd(source: CommandSource, args: CommandArgument): Unit =
    if args.size() < 1 then
      source.sendMessage("&c/lookup status <name>")
    else
      val lookupTerm = args.getUnderlyingList.asScala.mkString("_").toUpperCase
      val result: Try[StatusEffect] = Try(StatusEffect.valueOf(lookupTerm))
      result match
        case Success(value) =>
          val effectType = value.physical match
            case Some(true) => "&7Physical"
            case Some(false) => "&bEther"
            case None => "&cUntyped"

          source.sendMessage(s"&6&lEffect ${value.displayName}:")
          source.sendMessage(s"&6* &e&lBuff: ${if value.isBuff then "&bBuff" else "&cDebuff"}")
          source.sendMessage(s"&6* &e&lEffect Type: $effectType")
          source.sendMessage(s"&6* &e&lRandomly Applicable: ${if value.randomlyApplicable then "&aYes" else "&cNo"}")
        case Failure(_) =>
          source.sendMessage("&cNo status effect found with that name.")

  @TabExecute("lookup status")
  def lookupStatusTab(source: CommandSource, args: CommandArgument): util.List[String] =
    StatusEffect.values.map(_.toString).toList.asJava

  @Command("lookup skill")
  def lookupSkillCmd(source: CommandSource, args: CommandArgument): Unit =
    if args.size() < 1 then
      source.sendMessage("&c/lookup skill <name>")
    else
      val result: Try[EnumSkill] = Try(EnumSkill.valueOf(args.getUnderlyingList.asScala.mkString("_").toUpperCase))
      result match
        case Success(value) =>
          source.sendMessage(s"&6&lSkill ${value.displayName}:")
          source.sendMessage(s"&6* &e&lClass: &a${value.cls.name}")
          source.sendMessage(s"&6* &e&lMaster Skill: ${if value.isMaster then "&aYes" else "&cNo"}")
          source.sendMessage(s"&7( ${value.description} )")
        case Failure(_) =>
          source.sendMessage("&cNo skill found with that name.")

  @TabExecute("lookup skill")
  def lookupSkillTab(source: CommandSource, args: CommandArgument): util.List[String] =
    EnumSkill.values.map(_.toString).toList.asJava

  @Command("lookup class")
  def lookupClassCmd(source: CommandSource, args: CommandArgument): Unit =
    if args.size() < 1 then
      source.sendMessage("&c/lookup class <name>")
    else
      val result: Try[EnumClass] = Try(EnumClass.valueOf(args.getUnderlyingList.asScala.mkString("_").toUpperCase))
      result match
        case Success(value) =>
          source.sendMessage(s"&6&lClass ${value.name}:")
          source.sendMessage(s"&6* &e&lNation: &a${value.nation.toString}")
          source.sendMessage(s"&6* &e&lWielder name: ${value.role.colorize(value.wielderName)}")
          source.sendMessage(s"&6* &e&lWielder title: ${value.role.colorize(value.wielderTitle)}")
          source.sendMessage(s"&6* &e&lWeapon name: &7${value.weaponName}")
        case Failure(_) =>
          source.sendMessage("&cNo class found with that name.")

  @TabExecute("lookup class")
  def lookupClassTab(source: CommandSource, args: CommandArgument): util.List[String] =
    EnumClass.values.map(_.toString).toList.asJava
