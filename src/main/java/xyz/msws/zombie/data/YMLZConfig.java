package xyz.msws.zombie.data;

import org.bukkit.configuration.file.YamlConfiguration;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.ModuleConfig;
import xyz.msws.zombie.modules.apocalypse.YMLApoConfig;
import xyz.msws.zombie.modules.book.TXTBookConfig;
import xyz.msws.zombie.modules.breeding.YMLBreedingConfig;
import xyz.msws.zombie.modules.crafting.YMLCraftConfig;
import xyz.msws.zombie.modules.daylight.YMLDaylightConfig;
import xyz.msws.zombie.modules.fishing.YMLFishConfig;
import xyz.msws.zombie.modules.noenchant.YMLEnchantConfig;
import xyz.msws.zombie.modules.passivespawn.YMLPassiveConfig;

import java.io.File;
import java.io.IOException;

/**
 * A YML Implementation of {@link ZombieConfig}
 */
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
        reload();

        configs.add(new YMLBreedingConfig(plugin, this));
        configs.add(new YMLDaylightConfig(plugin, this));
        configs.add(new YMLFishConfig(plugin, this));
        configs.add(new YMLEnchantConfig(plugin, this));
        configs.add(new YMLPassiveConfig(plugin, this));
        configs.add(new YMLApoConfig(plugin, this));
        configs.add(new YMLCraftConfig(plugin, this));
        configs.add(new TXTBookConfig(plugin, this));

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

    @Override
    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void reset() {
        plugin.saveResource("config.yml", true);
        reload();
    }

    public YamlConfiguration getYml() {
        return config;
    }

    public File getFile() {
        return file;
    }
}
