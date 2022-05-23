package xyz.msws.zombie.modules.crafting;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigCollection;
import xyz.msws.zombie.data.YMLZConfig;
import xyz.msws.zombie.utils.MSG;
import xyz.msws.zombie.utils.Serializer;

public class YMLCraftConfig extends CraftConfig {
    private final YamlConfiguration config;

    public YMLCraftConfig(ZCore plugin, YMLZConfig config) {
        super(plugin, config);
        this.config = config.getConfig();
    }

    @Override
    public void load() {
        ConfigurationSection features = config.getConfigurationSection("Features");
        if (features == null) throw new NullPointerException("No features specified");

        ConfigurationSection crafting = features.getConfigurationSection("Crafting");
        if (crafting == null) {
            MSG.log("No crafting config was specified");
            return;
        }

        block = new ConfigCollection<>(Serializer.getEnumSet(crafting.getStringList("Block"), Material.class), Material.class);
        this.result = crafting.getString("RestrictItem", "graystainedglasspane");
    }

    @Override
    public void save() {

    }

}
