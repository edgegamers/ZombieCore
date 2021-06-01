package xyz.msws.zombie.modules.named;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import xyz.msws.zombie.modules.EventModule;

public class NamedSpawn extends EventModule {
    public NamedSpawn(Plugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getCustomName() == null)
            return;
        entity.setMetadata("ignoreZombie", new FixedMetadataValue(plugin, true));
    }

    @Override
    public void disable() {
        CreatureSpawnEvent.getHandlerList().unregister(this);
    }
}
