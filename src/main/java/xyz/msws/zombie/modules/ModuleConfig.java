package xyz.msws.zombie.modules;

import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ZombieConfig;

public abstract class ModuleConfig<T extends Module> {
    protected ZombieConfig config;
    protected ZCore plugin;

    public ModuleConfig(ZCore plugin, ZombieConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public abstract void load();

    public abstract void save();
}
