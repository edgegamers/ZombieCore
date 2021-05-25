package xyz.msws.zombie.modules;

import org.bukkit.entity.EntityType;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ZombieConfig;

import java.util.EnumSet;

public abstract class BreedingConfig extends ModuleConfig<AnimalBreeding> {

    protected EnumSet<EntityType> blockBreeding = EnumSet.noneOf(EntityType.class);

    public BreedingConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
    }

    public boolean blockBreeding(EntityType type) {
        return blockBreeding.contains(type);
    }


}
