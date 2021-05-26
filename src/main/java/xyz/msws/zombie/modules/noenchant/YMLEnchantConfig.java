package xyz.msws.zombie.modules.noenchant;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.YMLZConfig;
import xyz.msws.zombie.utils.MSG;
import xyz.msws.zombie.utils.Serializer;

import java.util.Arrays;

public class YMLEnchantConfig extends EnchantConfig {
    private YamlConfiguration config;

    public YMLEnchantConfig(ZCore plugin, YMLZConfig config) {
        super(plugin, config);
        this.config = config.getYml();
    }

    @Override
    public void load() {
        ConfigurationSection noEnchants = config.getConfigurationSection("Features.NoEnchants");
        if (noEnchants == null) {
            MSG.log("No enchantment restrictions have been configured");
            return;
        }

        types = Serializer.getEnumSet(noEnchants.getStringList("Entities"), EntityType.class);
        slots = Serializer.getEnumSet(noEnchants.getStringList("Slots"), EquipmentSlot.class);
        materials = Serializer.getEnumSet(noEnchants.getStringList("Items"), Material.class);

        for (String s : noEnchants.getStringList("Enchants")) {
            if (s.equalsIgnoreCase("all")) {
                enchants.addAll(Arrays.asList(Enchantment.values()));
                break;
            }
            try {
                Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(s));
                enchants.add(ench);
            } catch (IllegalArgumentException e) {
                MSG.log("Unknown enchantment: %s", s);
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
