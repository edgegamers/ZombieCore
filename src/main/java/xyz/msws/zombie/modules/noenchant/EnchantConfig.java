package xyz.msws.zombie.modules.noenchant;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigCollection;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.EnumSet;
import java.util.HashSet;

public abstract class EnchantConfig extends ModuleConfig<NoEnchantSpawn> {

    protected ConfigCollection<EntityType> types = new ConfigCollection<>(EnumSet.noneOf(EntityType.class), EntityType.class);
    protected ConfigCollection<EquipmentSlot> slots = new ConfigCollection<>(EnumSet.noneOf(EquipmentSlot.class), EquipmentSlot.class);
    protected ConfigCollection<Material> materials = new ConfigCollection<>(EnumSet.noneOf(Material.class), Material.class);
    protected ConfigCollection<Enchantment> enchants = new ConfigCollection<>(new HashSet<>(), Enchantment.class);

    public EnchantConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
    }

    public ConfigCollection<EquipmentSlot> getSlots() {
        return slots;
    }

    public boolean restrictType(EntityType type) {
        return types.contains(type);
    }

    public boolean restrictSlot(EquipmentSlot slot) {
        return slots.contains(slot);
    }

    public boolean restrictMaterial(Material mat) {
        return materials.contains(mat);
    }

    public boolean restrictEnchant(Enchantment enchantment) {
        return enchants.contains(enchantment);
    }

    @Override
    public String getName() {
        return "enchants";
    }
}
