package xyz.msws.zombie.modules.breeding;

import org.bukkit.entity.Breedable;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.EventModule;

public class AnimalBreeding extends EventModule {
    private final BreedingConfig config;

    public AnimalBreeding(ZCore plugin) {
        super(plugin);
        config = plugin.getZConfig().getConfig(BreedingConfig.class);
        if (config == null) {
            disable();
            throw new NullPointerException("No BreedingConfig found");
        }
    }

    @Override
    public void disable() {
        EntityBreedEvent.getHandlerList().unregister(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBreed(EntityBreedEvent event) {
        if (!config.blockBreeding())
            return;
        if (!config.blockBreeding(event.getEntityType()))
            return;
        event.setCancelled(true);
        event.setExperience(0);

        if (!config.resetBreeding())
            return;

        if (!(event.getMother() instanceof Breedable) || !(event.getFather() instanceof Breedable))
            return;
        Breedable a = (Breedable) event.getMother();
        Breedable b = (Breedable) event.getFather();

        a.setBreed(false);
        b.setBreed(false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFeed(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!config.blockClicks())
            return;
        if (!(entity instanceof Breedable))
            return;
        if (!config.blockBreeding(entity.getType()))
            return;
        event.setCancelled(true);
    }
}
