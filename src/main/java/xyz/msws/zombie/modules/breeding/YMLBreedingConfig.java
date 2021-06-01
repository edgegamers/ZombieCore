package xyz.msws.zombie.modules.breeding;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigCollection;
import xyz.msws.zombie.data.YMLZConfig;
import xyz.msws.zombie.utils.MSG;
import xyz.msws.zombie.utils.Serializer;

import java.util.ArrayList;
import java.util.List;

public class YMLBreedingConfig extends BreedingConfig {
    private final YamlConfiguration config;

    public YMLBreedingConfig(ZCore plugin, YMLZConfig config) {
        super(plugin, config);
        this.config = config.getYml();
    }

    @Override
    public void load() {
        ConfigurationSection features = config.getConfigurationSection("Features");
        if (features == null)
            throw new NullPointerException("No features specified");

        ConfigurationSection breeding = features.getConfigurationSection("Breeding");
        if (breeding == null) {
            MSG.log("No breeding config was specified");
            return;
        }

        blockBreeding = new ConfigCollection<>(Serializer.getEnumSet(features.getStringList("Breeding.Entities"), EntityType.class), EntityType.class);
        clicks = breeding.getBoolean("BlockClicks", false);
        breed = breeding.getBoolean("BlockBreeding", true);
        resetBreeding = breeding.getBoolean("ResetBreeding", true);
        hopeless = breeding.getInt("EggThreshold", 64);
        love = breeding.getBoolean("BlockLove", true);
    }

    @Override
    public void save() {
        ConfigurationSection features = config.createSection("Features");
        List<String> types = new ArrayList<>();
        blockBreeding.forEach(s -> types.add(s.toString()));
        features.set("Breeding.Entities", types);
        features.set("BlockClicks", clicks);
        features.set("BlockBreeding", breed);
        features.set("ResetBreeding", true);
        features.set("EggThreshold", hopeless);
        config.set("Features", features);
    }
}
