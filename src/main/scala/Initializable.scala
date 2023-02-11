package dev.hawu.plugins.xenocraft

import org.bukkit.plugin.java.JavaPlugin

/** Represents an object that can be initialized.
  */
trait Initializable:

  /** Initializes said object.
    */
  def setUp(pl: JavaPlugin): Unit = ()

  /** Cleans up the object.
    */
  def tearDown(pl: JavaPlugin): Unit = ()
