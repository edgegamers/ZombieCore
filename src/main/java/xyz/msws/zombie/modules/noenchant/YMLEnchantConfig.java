package xyz.msws.zombie.modules.noenchant;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigCollection;
import xyz.msws.zombie.data.YMLZConfig;
import xyz.msws.zombie.utils.MSG;
import xyz.msws.zombie.utils.Serializer;

import java.util.Arrays;
import java.util.List;

public class YMLEnchantConfig extends EnchantConfig {
    private final YamlConfiguration config;

    public YMLEnchantConfig(ZCore plugin, YMLZConfig config) {
        super(plugin, config);
        this.config = config.getConfig();
    }

    @Override
    public void load() {
        ConfigurationSection noEnchants = config.getConfigurationSection("Features.NoEnchants");
        if (noEnchants == null) {
            MSG.log("No enchantment restrictions have been configured");
            return;
        }

        types = new ConfigCollection<>(Serializer.getEnumSet(noEnchants.getStringList("BlockEntities"), EntityType.class), EntityType.class);
        slots = new ConfigCollection<>(Serializer.getEnumSet(noEnchants.getStringList("BlockSlots"), EquipmentSlot.class), EquipmentSlot.class);
        materials = new ConfigCollection<>(Serializer.getEnumSet(noEnchants.getStringList("BlockItems"), Material.class), Material.class);

        List<String> blockEnchants = noEnchants.getStringList("BlockEnchants");
        boolean whitelist = true;
        if (blockEnchants.contains("ALL")) {
            enchants.addAll(Arrays.asList(Enchantment.values()));
            whitelist = false;
            blockEnchants.remove("ALL");
        }

        for (String s : blockEnchants) {
            try {
                Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(s));
                if (whitelist) enchants.add(ench);
                else enchants.remove(ench);
            } catch (IllegalArgumentException e) {
                MSG.warn("Unknown enchantment: %s", s);
            }
        }
    }

    @Override
    public void save() {
        ConfigurationSection section = config.createSection("Features.NoEnchants");
        section.set("Entities", types);
        section.set("Slots", slots);
        section.set("Items", materials);
    }
}
