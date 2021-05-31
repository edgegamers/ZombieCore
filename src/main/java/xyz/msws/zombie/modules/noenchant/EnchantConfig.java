package xyz.msws.zombie.modules.noenchant;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.EnumSet;
import java.util.HashSet;

public abstract class EnchantConfig extends ModuleConfig<NoEnchantSpawn> {

    protected EnumSet<EntityType> types = EnumSet.noneOf(EntityType.class);
    protected EnumSet<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);
    protected EnumSet<Material> materials = EnumSet.noneOf(Material.class);
    protected HashSet<Enchantment> enchants = new HashSet<>();

    public EnchantConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
    }

    public EnumSet<EquipmentSlot> getSlots() {
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
