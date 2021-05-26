package xyz.msws.zombie.modules.breeding;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.YMLZConfig;
import xyz.msws.zombie.utils.MSG;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YMLAnimalConfig extends BreedingConfig {
    private File file;
    private YamlConfiguration config;

    public YMLAnimalConfig(ZCore plugin, YMLZConfig config) {
        super(plugin, config);
    }

    @Override
    public void load() {
        if (!file.exists())
            plugin.saveResource("config.yml", true);
        this.config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection features = config.getConfigurationSection("Features");
        if (features == null)
            throw new NullPointerException("No features specified");

        ConfigurationSection breeding = features.getConfigurationSection("Breeding");
        if (breeding == null) {
            MSG.log("No breeding config was specified");
            return;
        }

        List<String> breeds = features.getStringList("Breeding.Entities");
        if (breeds.isEmpty())
            MSG.log("No limitation to breeding was specified in the config.");
        for (String breed : breeds) {
            if (breed.isEmpty())
                continue;
            if (breed.equalsIgnoreCase("all")) {
                blockBreeding.addAll(Arrays.asList(EntityType.values()));
                break;
            }
            try {
                EntityType type = EntityType.valueOf(breed.toUpperCase());
                blockBreeding.add(type);
            } catch (IllegalArgumentException e) {
                MSG.log("Invalid entity type specified in breeding list: %s", breed);
            }
        }

        this.clicks = breeding.getBoolean("BlockClicks", false);
        this.breed = breeding.getBoolean("BlockBreeding", true);
        this.resetBreeding = breeding.getBoolean("ResetBreeding", true);
    }

    @Override
    public void save() {
        ConfigurationSection features = config.createSection("Features");
        List<String> types = new ArrayList<>();
        blockBreeding.forEach(s -> types.add(s.toString()));
        features.set("DisableBreeding", types);

        config.set("Features", features);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
