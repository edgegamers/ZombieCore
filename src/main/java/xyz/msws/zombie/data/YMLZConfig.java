package xyz.msws.zombie.data;

import org.bukkit.configuration.file.YamlConfiguration;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.ModuleConfig;
import xyz.msws.zombie.modules.breeding.YMLBreedingConfig;

import java.io.File;
import java.io.IOException;

public class YMLZConfig extends ZombieConfig {
    private final File file;
    private YamlConfiguration config;

    public YMLZConfig(ZCore plugin, File file) {
        super(plugin);
        this.file = file;
    }

    @Override
    public void load() {
        if (!file.exists())
            plugin.saveResource("config.yml", true);
        this.config = YamlConfiguration.loadConfiguration(file);

        configs.add(new YMLBreedingConfig(plugin, this));

        configs.forEach(ModuleConfig::load);
    }

    @Override
    public void save() {
        configs.forEach(ModuleConfig::save);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getYml() {
        return config;
    }

    public File getFile() {
        return file;
    }
}
