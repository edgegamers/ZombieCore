package xyz.msws.zombie.data;

import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a {@link ZombieConfig}
 */
public abstract class ZombieConfig {
    protected ZCore plugin;

    protected List<ModuleConfig<?>> configs = new ArrayList<>();

    public ZombieConfig(ZCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads the specified data
     */
    public abstract void load();

    /**
     * Saves the current values to the config
     */
    public abstract void save();

    /**
     * Reloads the current values
     */
    public abstract void reload();

    /**
     * Resets the current values to default
     */
    public abstract void reset();

    /**
     * Returns a {@link ModuleConfig} for the given type
     *
     * @param type Module Config Class to get
     * @param <T>  Module Config Class
     * @return The requested config class, or null if none exists
     */
    public <T extends ModuleConfig<?>> T getConfig(Class<T> type) {
        for (ModuleConfig<?> config : configs) {
            if (type.isAssignableFrom(config.getClass()))
                return (T) config;
        }
        return null;
    }
}
