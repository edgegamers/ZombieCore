package xyz.msws.zombie.modules;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class EventModule extends Module implements Listener {
    public EventModule(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
