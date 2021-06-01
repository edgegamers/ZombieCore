package xyz.msws.zombie.modules.passivespawn;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.EventModule;
import xyz.msws.zombie.utils.MSG;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class PassiveSpawn extends EventModule {

    private PassiveConfig config;
    private Map<EntityType, Integer> typeStats = new HashMap<>();
    private Map<CreatureSpawnEvent.SpawnReason, Integer> reasonStats = new EnumMap<>(CreatureSpawnEvent.SpawnReason.class);


    public PassiveSpawn(ZCore plugin) {
        super(plugin);
        this.config = plugin.getZConfig().getConfig(PassiveConfig.class);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++)
                    MSG.announce(" ");
                MSG.announce("&6&lAllowed Types:");
                for (Map.Entry<EntityType, Integer> entry : typeStats.entrySet()) {
                    MSG.announce("&e" + MSG.camelCase(entry.getKey().toString()) + "&7: &f" + entry.getValue());
                }
                MSG.announce("&2&lAllowed Reasons:");
                for (Map.Entry<CreatureSpawnEvent.SpawnReason, Integer> entry : reasonStats.entrySet()) {
                    MSG.announce("&a" + MSG.camelCase(entry.getKey().toString()) + "&7: &f" + entry.getValue());
                }
            }
        }.runTaskTimer(plugin, 0, 60);
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (!config.blockType(event.getEntityType())) {
            increment(event.getEntityType());
            return;
        }
        if (!config.blockReason(event.getSpawnReason())) {
            increment(event.getSpawnReason());
            return;
        }
        switch (config.getMethod()) {
            case CANCEL -> event.setCancelled(true);
            case HP -> event.getEntity().setHealth(0);
            case REMOVE -> event.getEntity().remove();
            case TP -> event.getEntity().teleport(event.getLocation().clone().subtract(0, 1000, 0));
        }
    }

    public int increment(EntityType type) {
        typeStats.put(type, typeStats.getOrDefault(type, 0) + 1);
        return typeStats.get(type);
    }

    public int increment(CreatureSpawnEvent.SpawnReason type) {
        reasonStats.put(type, reasonStats.getOrDefault(type, 0) + 1);
        return reasonStats.get(type);
    }

    @Override
    public void disable() {
        CreatureSpawnEvent.getHandlerList().unregister(this);
    }
}
