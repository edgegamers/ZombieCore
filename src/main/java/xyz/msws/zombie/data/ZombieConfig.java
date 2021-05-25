package xyz.msws.zombie.data;

import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.ArrayList;
import java.util.List;

public abstract class ZombieConfig {
    protected ZCore plugin;

    protected List<ModuleConfig<?>> configs = new ArrayList<>();
    
    public ZombieConfig(ZCore plugin) {
        this.plugin = plugin;
    }

    public abstract void load();

    public abstract void save();

    public <T extends ModuleConfig<?>> T getConfig(Class<T> type) {
        for (ModuleConfig<?> config : configs) {
            if (type.isAssignableFrom(config.getClass()))
                return (T) config;
        }
        return null;
    }
}
