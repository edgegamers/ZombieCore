package xyz.msws.zombie.data;

import org.bukkit.entity.EntityType;

import java.util.Collection;
import java.util.EnumSet;

public abstract class ZombieConfig {
    protected EnumSet<EntityType> blockBreeding;

    public abstract void load();

    public abstract void save();

    public boolean blockBreeding(EntityType type) {
        if (blockBreeding.contains(type))
            return true;
        return blockBreeding.contains(null);
    }

    public void addBreedType(EntityType type) {
        blockBreeding.add(type);
    }

    public void removeBreedType(EntityType type) {
        blockBreeding.remove(type);
    }

    public void setBreedList(Collection<EntityType> types) {
        blockBreeding = EnumSet.copyOf(types);
    }
}
