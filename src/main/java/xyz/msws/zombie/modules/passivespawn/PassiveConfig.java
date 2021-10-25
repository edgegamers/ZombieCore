package xyz.msws.zombie.modules.passivespawn;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigCollection;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.EnumSet;

public abstract class PassiveConfig extends ModuleConfig<PassiveSpawn> {

    protected ConfigCollection<EntityType> block = new ConfigCollection<>(EnumSet.noneOf(EntityType.class), EntityType.class);
    protected ConfigCollection<CreatureSpawnEvent.SpawnReason> reasons = new ConfigCollection<>(EnumSet.noneOf(CreatureSpawnEvent.SpawnReason.class), CreatureSpawnEvent.SpawnReason.class);
    protected BlockMethod method;
    protected boolean allowNames;

    public PassiveConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
    }

    @Override
    public String getName() {
        return "passive";
    }

    public boolean blockType(EntityType type) {
        return block.contains(type);
    }

    public BlockMethod getMethod() {
        return method;
    }

    public boolean blockReason(CreatureSpawnEvent.SpawnReason reason) {
        return reasons.contains(reason);
    }
}
