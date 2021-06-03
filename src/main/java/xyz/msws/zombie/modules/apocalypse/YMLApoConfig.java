package xyz.msws.zombie.modules.apocalypse;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.YMLZConfig;
import xyz.msws.zombie.utils.MSG;

public class YMLApoConfig extends ApoConfig {

    private YamlConfiguration config;

    public YMLApoConfig(ZCore plugin, YMLZConfig config) {
        super(plugin, config);
        this.config = config.getYml();
    }

    @Override
    public void load() {
        ConfigurationSection apo = config.getConfigurationSection("Features.Apocalypse");
        if (apo == null) {
            MSG.log("No apocalypse startup settings have been configured");
            return;
        }
        for (String s : apo.getStringList("AutoStart")) {
            if (s.equalsIgnoreCase("ALL")) {
                for (World w : Bukkit.getWorlds())
                    maps.add(w.getName());
                continue;
            }
            maps.add(s);
        }

        startLoads = apo.getBoolean("StartLoads", true);
    }

    @Override
    public void save() {

    }
}
