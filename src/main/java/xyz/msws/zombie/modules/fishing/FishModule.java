package xyz.msws.zombie.modules.fishing;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
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

    private final Map<UUID, Long> times = new WeakHashMap<>();

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (event.getPlayer().hasPermission("zombiecore.bypass.fishing"))
            return;
        if (event.getState() == PlayerFishEvent.State.FISHING) {
            if (config.getMinTime() == -1)
                return;
            event.getHook().setMinWaitTime((int) config.getMinTime() / 1000);
        }
        if (event.getState() == PlayerFishEvent.State.FISHING) {
            times.put(uuid, System.currentTimeMillis());
            return;
        }
        if (event.getState() == PlayerFishEvent.State.REEL_IN) {
            Entity ent = event.getCaught();
            if (!(ent instanceof Item item))
                return;
            ItemStack stack = item.getItemStack();
            if (config.blockEnchants()) {
                for (Enchantment ench : stack.getEnchantments().keySet())
                    stack.removeEnchantment(ench);
                item.setItemStack(stack);
            }
            if (!config.restrict(item.getItemStack().getType()))
                return;
            item.setItemStack(new ItemStack(Material.COD, 1));
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
