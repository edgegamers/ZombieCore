package xyz.msws.zombie.api;

import org.bukkit.plugin.Plugin;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.features.ModuleManager;

public interface ZCore extends Plugin {
    ModuleManager getModuleManager();

    ZombieConfig getZConfig();
}
