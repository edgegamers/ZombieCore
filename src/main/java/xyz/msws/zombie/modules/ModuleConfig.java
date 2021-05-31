package xyz.msws.zombie.modules;

import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ZombieConfig;

import java.util.Map;

/**
 * Represents a config for a Module, similar to ParticleData
 *
 * @param <T> ModuleType that is supported
 */
public abstract class ModuleConfig<T extends Module> {
    protected ZombieConfig config;
    protected ZCore plugin;

    public ModuleConfig(ZCore plugin, ZombieConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    /**
     * Parses a config's data
     */
    public abstract void load();

    /**
     * Saves the current values to the config
     */
    public abstract void save();

    public abstract String getName();
}
