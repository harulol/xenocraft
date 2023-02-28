package dev.hawu.plugins.xenocraft
package modules

import org.bukkit.plugin.java.JavaPlugin

/** Represents a module that can be enabled or disabled.
  */
trait Module:

  /** Sets up this module with [[pl]] injected. Returns true if the setup finished with no issues.
    */
  def setup(pl: JavaPlugin): Boolean = true

  /** Reloads the module, [[pl]] provided and whether this was invoked forcefully. Returns true if
    * the reload finished with no issues.
    */
  def reload(pl: JavaPlugin, force: Boolean): Boolean = true

  /** Tears down the module and prepares for disabling.
    */
  def tearDown(pl: JavaPlugin): Unit = ()
