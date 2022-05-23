package xyz.msws.zombie.modules.crafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.items.ItemFactory;
import xyz.msws.zombie.modules.EventModule;

public class CraftBlocker extends EventModule {

    private final CraftConfig config;
    private final ZCore plugin;
    private ItemStack restrict;

    public CraftBlocker(ZCore plugin) {
        super(plugin);
        this.plugin = plugin;
        this.config = plugin.getZConfig().getConfig(CraftConfig.class);
    }

    @Override
    public void enable() {
        super.enable();
        ItemFactory factory = plugin.getItemBuilder();
        restrict = factory.build(config.getResult(), Bukkit.getConsoleSender());
        if (restrict == null) restrict = new ItemStack(Material.AIR);
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;
        if (!config.blockRecipe(event.getRecipe().getResult().getType())) return;
        event.getInventory().setResult(restrict);
    }

    @EventHandler
    public void onCraft(InventoryClickEvent event) {
        if (event.getSlotType() != InventoryType.SlotType.RESULT) return;
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (!item.equals(restrict)) return;
        event.setCancelled(true);
    }

    @Override
    public void disable() {
        PrepareItemCraftEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
    }
}
