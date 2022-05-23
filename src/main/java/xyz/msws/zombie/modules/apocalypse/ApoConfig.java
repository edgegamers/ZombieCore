package xyz.msws.zombie.modules.apocalypse;

import lombok.Getter;
import lombok.experimental.Accessors;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigCollection;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.HashSet;

public abstract class ApoConfig extends ModuleConfig<ApoModule> {

    @Getter
    protected final ConfigCollection<String> maps = new ConfigCollection<>(new HashSet<>(), String.class);

    @Getter
    @Accessors(fluent = true)
    protected boolean startLoads;

    public ApoConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
    }

    @Override
    public String getName() {
        return "apocalypse";
    }
}
