package xyz.msws.zombie.features;

import org.bukkit.plugin.Plugin;

public abstract class Module {
    protected Plugin plugin;

    public Module(Plugin plugin) {
        this.plugin = plugin;
    }

    public abstract void enable();

    public abstract void disable();
}
