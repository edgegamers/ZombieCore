package xyz.msws.zombie.features;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityBreedEvent;
import xyz.msws.zombie.api.ZCore;

public class AnimalBreeding extends EventModule {
    private final ZCore plugin;

    public AnimalBreeding(ZCore plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void disable() {
        EntityBreedEvent.getHandlerList().unregister(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBreed(EntityBreedEvent event) {
        if (plugin.getZConfig().blockBreeding(event.getEntityType()))
            return;
        event.setCancelled(true);
        event.setExperience(0);
    }
}
