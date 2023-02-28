package dev.hawu.plugins.xenocraft

import Xenocraft.instance
import modules.{EnumCharacter, I18n, Module}

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.Bukkit

/** The base component of the entire plugin [[Xenocraft]].
  */
class Xenocraft extends JavaPlugin:

  private val modules: Seq[Module] = Seq(EnumCharacter, I18n)

  override def onEnable(): Unit =
    instance = Some(this)
    modules.filterNot(_.setup(this)).map(_.getClass.getSimpleName)
      .foreach(mod => "module-setup-failed".tl("module" -> mod))

  override def onDisable(): Unit =
    modules.reverse.foreach(_.tearDown(this))
    instance = None

/** Companion object for [[Xenocraft]].
  */
object Xenocraft:

  private var instance: Option[Xenocraft] = None

  /** Returns the [[Xenocraft]] instance, the base plugin.
    */
  def getInstance: Xenocraft = instance.get
