package xyz.msws.zombie.api;

import org.bukkit.plugin.Plugin;
import xyz.msws.zombie.commands.ZombieCoreCommand;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.data.items.ItemFactory;
import xyz.msws.zombie.modules.ModuleManager;

/**
 * Represents a ZombieCore Plugin
 */
public interface ZCore extends Plugin {
    /**
     * Gets the module manager that the plugin uses
     *
     * @return ModuleManager or null if unassigned
     */
    ModuleManager getModuleManager();

    /**
     * Gets the plugin's config, implementations may differ
     *
     * @return The {@link ZombieConfig}
     */
    ZombieConfig getZConfig();

    /**
     * Gets the plugin's {@link ItemFactory}
     *
     * @return The plugin's {@link ItemFactory}
     */
    ItemFactory getItemBuilder();

    /**
     * Gets the plugin's core command
     *
     * @return The plugin's {@link ZombieCoreCommand}
     */
    ZombieCoreCommand getCoreCommand();
}
