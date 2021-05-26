package xyz.msws.zombie.modules.fishing;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.EventModule;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class FishModule extends EventModule {
    private final FishConfig config;

    public FishModule(ZCore plugin) {
        super(plugin);
        this.config = plugin.getZConfig().getConfig(FishConfig.class);
    }

    private Map<UUID, Long> times = new WeakHashMap<>();

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (event.getState() == PlayerFishEvent.State.FISHING) {
            times.put(uuid, System.currentTimeMillis());
            return;
        }
        if (event.getState() != PlayerFishEvent.State.BITE)
            return;
        if (!config.cancel(System.currentTimeMillis() - times.getOrDefault(uuid, 0L)))
            return;
        event.setCancelled(true);
    }

    @Override
    public void disable() {
        PlayerFishEvent.getHandlerList().unregister(this);
    }
}
