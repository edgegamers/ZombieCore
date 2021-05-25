package xyz.msws.zombie.data;

import org.bukkit.configuration.file.YamlConfiguration;
import xyz.msws.zombie.api.ZCore;

import java.io.File;

public class YMLZConfig extends ZombieConfig {
    private File file;
    private YamlConfiguration config;

    public YMLZConfig(ZCore plugin, File file) {
        super(plugin);
        this.file = file;
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }
}
