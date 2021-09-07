package xyz.msws.zombie.modules.breeding;

import org.bukkit.entity.EntityType;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigCollection;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.EnumSet;

public abstract class BreedingConfig extends ModuleConfig<AnimalBreeding> {

    protected ConfigCollection<EntityType> blockBreeding = new ConfigCollection<>(EnumSet.noneOf(EntityType.class), EntityType.class);
    protected boolean clicks = true, breed = false, love = true, resetBreeding = true;
    protected int hopeless = 64;

    public BreedingConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
    }

    public boolean allowBreeding(EntityType type) {
        return !blockBreeding.contains(type);
    }

    public boolean blockClicks() {
        return clicks;
    }

    public boolean blockBreeding() {
        return breed;
    }

    public boolean resetBreeding() {
        return resetBreeding;
    }

    public boolean blockLove() {
        return love;
    }

    public int getHopeless() {
        return hopeless;
    }

    @Override
    public String getName() {
        return "breeding";
    }
}
