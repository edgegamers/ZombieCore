package xyz.msws.zombie.modules.breeding;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityEnterLoveModeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.Lang;
import xyz.msws.zombie.modules.EventModule;
import xyz.msws.zombie.utils.MSG;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class AnimalBreeding extends EventModule {
    private final BreedingConfig config;

    private Map<UUID, Integer> attempts = new HashMap<>();
    private HashSet<UUID> sent = new HashSet<>();

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
        EntityEnterLoveModeEvent.getHandlerList().unregister(this);
        PlayerInteractEntityEvent.getHandlerList().unregister(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBreed(EntityBreedEvent event) {
        if (!config.blockBreeding())
            return;
        if (config.allowBreeding(event.getEntityType()))
            return;
        if (event.getBreeder() != null && event.getBreeder().hasPermission("zombiecore.bypass.breeding"))
            return;
        event.setCancelled(true);
        event.setExperience(0);
        if (!config.resetBreeding())
            return;
        if (!(event.getMother() instanceof Animals) || !(event.getFather() instanceof Animals))
            return;
        Animals a = (Animals) event.getMother();
        Animals b = (Animals) event.getFather();
        a.setBreed(true);
        b.setBreed(true);
        a.setLoveModeTicks(0);
        b.setLoveModeTicks(0);

        if (event.getBreeder() != null && config.getHopeless() != -1)
            increment(event.getBreeder());
    }

    @EventHandler
    public void onEnter(EntityEnterLoveModeEvent event) {
        if (!config.blockLove())
            return;
        if (config.allowBreeding(event.getEntityType()))
            return;
        if (event.getHumanEntity() != null && event.getHumanEntity().hasPermission("zombiecore.bypass.breeding"))
            return;
        event.setCancelled(true);
        event.setTicksInLove(0);
        if (event.getHumanEntity() == null)
            return;
        increment(event.getHumanEntity());
    }

    private void increment(LivingEntity player) {
        attempts.put(player.getUniqueId(), attempts.getOrDefault(player.getUniqueId(), 0) + 1);
        if (attempts.getOrDefault(player.getUniqueId(), 0) < config.getHopeless())
            return;
        if (sent.contains(player.getUniqueId()))
            return;
        attempts.put(player.getUniqueId(), 0);
        sent.add(player.getUniqueId());
        for (Player p : Bukkit.getOnlinePlayers())
            MSG.tell(p, Lang.BREEDING_EGG, player.getName());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFeed(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!config.blockClicks())
            return;
        if (!(entity instanceof Breedable))
            return;
        if (config.allowBreeding(entity.getType()))
            return;
        if (event.getPlayer().hasPermission("zombiecore.bypass.breeding"))
            return;
        event.setCancelled(true);
    }
}
