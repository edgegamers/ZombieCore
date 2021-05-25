package xyz.msws.zombie.data;

import org.bukkit.entity.EntityType;

import java.util.List;

public abstract class ZombieConfig {
    private List<EntityType> blockBreeding;

    public abstract void load();

    public boolean blockBreeding(EntityType type) {
        if (blockBreeding.contains(type))
            return true;
        return blockBreeding.contains(null);
    }
}
