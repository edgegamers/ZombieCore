package xyz.msws.zombie.modules.noenchant;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.EventModule;

import java.util.Map;

public class NoEnchantSpawn extends EventModule {
    private final EnchantConfig config;

    public NoEnchantSpawn(ZCore plugin) {
        super(plugin);
        this.config = plugin.getZConfig().getConfig(EnchantConfig.class);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (!config.restrictType(entity.getType())) return;

        EntityEquipment eq = entity.getEquipment();
        if (eq == null) return;

        for (EquipmentSlot slot : config.getSlots()) {
            ItemStack item = eq.getItem(slot);
            if (item.getType().isAir()) continue;
            if (config.restrictMaterial(item.getType())) {
                eq.setItem(slot, new ItemStack(Material.AIR));
                continue;
            }
            Map<Enchantment, Integer> enchants = item.getEnchantments();
            for (Enchantment e : enchants.keySet())
                if (config.restrictEnchant(e)) item.removeEnchantment(e);

            eq.setItem(slot, item);
        }
    }

    @Override
    public void disable() {
        CreatureSpawnEvent.getHandlerList().unregister(this);
    }
}
