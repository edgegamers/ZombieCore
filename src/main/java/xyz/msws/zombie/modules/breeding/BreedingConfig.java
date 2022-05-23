package xyz.msws.zombie.modules.breeding;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.entity.EntityType;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigCollection;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.EnumSet;

public abstract class BreedingConfig extends ModuleConfig<AnimalBreeding> {

    protected ConfigCollection<EntityType> blockTypes = new ConfigCollection<>(EnumSet.noneOf(EntityType.class), EntityType.class);
    @Getter
    @Accessors(fluent = true)
    protected boolean blockClicks = true, blockBreeding = false, blockLove = true, resetBreeding = true;
    @Getter
    protected int hopeless = 64;

    public BreedingConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
    }

    public boolean allowBreeding(EntityType type) {
        return !blockTypes.contains(type);
    }

    @Override
    public String getName() {
        return "breeding";
    }
}
