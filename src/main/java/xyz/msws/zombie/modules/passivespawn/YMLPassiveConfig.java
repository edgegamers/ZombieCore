package xyz.msws.zombie.modules.passivespawn;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigCollection;
import xyz.msws.zombie.data.YMLZConfig;
import xyz.msws.zombie.utils.MSG;
import xyz.msws.zombie.utils.Serializer;

public class YMLPassiveConfig extends PassiveConfig {
    private final YamlConfiguration config;

    public YMLPassiveConfig(ZCore plugin, YMLZConfig config) {
        super(plugin, config);
        this.config = config.getYml();
    }

    @Override
    public void load() {
        ConfigurationSection features = config.getConfigurationSection("Features");
        if (features == null)
            throw new NullPointerException("No features specified");

        ConfigurationSection spawns = features.getConfigurationSection("BlockSpawns");
        if (spawns == null) {
            MSG.log("No passive mob spawning config specified");
            return;
        }

        block = new ConfigCollection<>(Serializer.getEnumSet(spawns.getStringList("Entities"), EntityType.class), EntityType.class);
        reasons = new ConfigCollection<>(Serializer.getEnumSet(spawns.getStringList("BlockReasons"), CreatureSpawnEvent.SpawnReason.class), CreatureSpawnEvent.SpawnReason.class);
        method = Serializer.getEnum(spawns.getString("Method"), BlockMethod.class);
    }

    @Override
    public void save() {

    }
}
