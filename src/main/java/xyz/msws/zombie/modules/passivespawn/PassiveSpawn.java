package xyz.msws.zombie.modules.passivespawn;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.EventModule;

public class PassiveSpawn extends EventModule {

    private PassiveConfig config;

    public PassiveSpawn(ZCore plugin) {
        super(plugin);
        this.config = plugin.getZConfig().getConfig(PassiveConfig.class);
    }

    @EventHandler
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

    @Override
    public void disable() {
        CreatureSpawnEvent.getHandlerList().unregister(this);
    }
}
