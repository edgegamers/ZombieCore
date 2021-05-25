package xyz.msws.zombie.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YMLZConfig extends ZombieConfig {
    private File file;
    private YamlConfiguration config;

    public YMLZConfig(File file) {
        this.file = file;
    }

    @Override
    public void load() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void save() {
        ConfigurationSection features = config.createSection("Features");
        List<String> types = new ArrayList<>();
        blockBreeding.forEach(s -> types.add(s == null ? "null" : s.toString()));
        features.set("DisableBreeding", types);

        config.set("Features", features);
        
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
