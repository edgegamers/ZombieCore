package xyz.msws.zombie.modules.apocalypse;

import com.ericdebouwer.zombieapocalypse.api.ApocalypseAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.EventModule;

public class ApoModule extends EventModule {

    private final ApoConfig config;

    public ApoModule(ZCore plugin) {
        super(plugin);
        this.config = plugin.getZConfig().getConfig(ApoConfig.class);
        if (!Bukkit.getPluginManager().isPluginEnabled("ZombieApocalypse"))
            throw new IllegalStateException("ZombieApocalypse is not enabled");
    }

    @Override
    public void enable() {
        for (String name : config.getMaps()) {
            ApocalypseAPI.getInstance().startApocalypse(name);
        }
    }

    @EventHandler
    public void onLoad(WorldLoadEvent event) {
        if (!config.doStartLoads())
            return;
        ApocalypseAPI.getInstance().startApocalypse(event.getWorld().getName());
    }

    @Override
    public void disable() {
        WorldLoadEvent.getHandlerList().unregister(this);
    }
}
