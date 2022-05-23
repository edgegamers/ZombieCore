package xyz.msws.zombie.modules.apocalypse;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.YMLZConfig;
import xyz.msws.zombie.utils.MSG;

import java.util.List;

public class YMLApoConfig extends ApoConfig {

    private final YamlConfiguration config;

    public YMLApoConfig(ZCore plugin, YMLZConfig config) {
        super(plugin, config);
        this.config = config.getConfig();
    }

    @Override
    public void load() {
        ConfigurationSection apo = config.getConfigurationSection("Features.Apocalypse");
        if (apo == null) {
            MSG.log("No apocalypse startup settings have been configured");
            return;
        }
        List<String> worlds = apo.getStringList("AutoStart");
        boolean white = !worlds.contains("ALL");
        if (white) for (World w : Bukkit.getWorlds())
            maps.add(w.getName());
        for (String s : apo.getStringList("AutoStart")) {
            if (white) {
                maps.add(s);
            } else maps.remove(s);
        }

        startLoads = apo.getBoolean("StartLoads", true);
    }

    @Override
    public void save() {
        ConfigurationSection features = config.createSection("Features");

    }
}
