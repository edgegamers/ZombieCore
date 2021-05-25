package xyz.msws.zombie.modules;

import org.bukkit.entity.Breedable;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
        if (plugin.getZConfig().getConfig(BreedingConfig.class).blockBreeding(event.getEntityType()))
            return;
        event.setCancelled(true);
        event.setExperience(0);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFeed(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Breedable))
            return;
        if (!plugin.getZConfig().getConfig(BreedingConfig.class).blockBreeding(entity.getType()))
            return;
        event.setCancelled(true);
    }
}
