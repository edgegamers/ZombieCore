package xyz.msws.zombie.modules;

import org.bukkit.plugin.Plugin;

public abstract class Module {
    protected final Plugin plugin;

    public Module(Plugin plugin) {
        this.plugin = plugin;
    }

    public abstract void enable();

    public abstract void disable();
}
