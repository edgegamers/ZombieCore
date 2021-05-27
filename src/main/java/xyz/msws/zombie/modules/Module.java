package xyz.msws.zombie.modules;

import org.bukkit.plugin.Plugin;

/**
 * A module for easy toggling of modules
 */
public abstract class Module {
    protected final Plugin plugin;

    public Module(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads, initializes, and runs any first-run code
     */
    public abstract void enable();

    /**
     * Disables, unloads, saves, etc. any data related to the Module
     */
    public abstract void disable();
}
