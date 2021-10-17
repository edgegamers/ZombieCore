package xyz.msws.zombie.modules.passivespawn;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.EventModule;

public class PassiveSpawn extends EventModule {

    private final PassiveConfig config;

    public PassiveSpawn(ZCore plugin) {
        super(plugin);
        this.config = plugin.getZConfig().getConfig(PassiveConfig.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawn(CreatureSpawnEvent event) {
        if (!config.blockType(event.getEntityType()))
            return;
        if (!config.blockReason(event.getSpawnReason()))
            return;
        switch (config.getMethod()) {
            case CANCEL -> event.setCancelled(true);
            case HP -> event.getEntity().setHealth(0);
            case REMOVE -> event.getEntity().remove();
            case TP -> event.getEntity().teleport(event.getLocation().clone().subtract(0, 1000, 0));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawn(ChunkLoadEvent event) {
        for (Entity ent : event.getChunk().getEntities()) {
            if (!config.blockType(ent.getType()))
                continue;
            switch (config.getMethod()) {
                case HP -> {
                    if (ent instanceof LivingEntity) {
                        ((LivingEntity) ent).setHealth(0);
                    } else {
                        ent.remove();
                    }
                }
                case REMOVE, CANCEL -> ent.remove();
                case TP -> ent.teleport(ent.getLocation().clone().subtract(0, 1000, 0));
            }
        }
    }

    @Override
    public void disable() {
        CreatureSpawnEvent.getHandlerList().unregister(this);
        ChunkLoadEvent.getHandlerList().unregister(this);
    }
}
