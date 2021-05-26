package xyz.msws.zombie.modules.daylight;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.EventModule;

public class DaylightSpawn extends EventModule {
    private final DaylightConfig config;

    public DaylightSpawn(ZCore plugin) {
        super(plugin);
        config = plugin.getZConfig().getConfig(DaylightConfig.class);
    }

    @EventHandler(priority = EventPriority.LOW) // Towny Compatability
    public void onSpawn(CreatureSpawnEvent event) {
        if (config.getMobWeights().containsKey(event.getEntityType()))
            return;
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL)
            return;
        if (!config.doCorrupt())
            return;

        EntityType type = config.getRandomType();
        Location origin = event.getLocation().clone().add(config.getRandomOffset());
        int toSpawn = config.getRandomAmount();
        for (int i = 0; i < toSpawn; i++) {
            Location loc = origin.clone().add(config.getRandomOffset(-2, 2));
            if (loc.getWorld() == null)
                break;
            loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 1);
            loc.getWorld().spawnEntity(loc, type);
        }
    }


    @Override
    public void disable() {
        EntitySpawnEvent.getHandlerList().unregister(this);
    }
}
