package xyz.msws.zombie.modules.daylight;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.TimeVariable;
import xyz.msws.zombie.data.YMLZConfig;
import xyz.msws.zombie.utils.MSG;

import java.util.Map;

public class YMLDaylightConfig extends DaylightConfig {
    private final YamlConfiguration config;

    public YMLDaylightConfig(ZCore plugin, YMLZConfig config) {
        super(plugin, config);
        this.config = config.getYml();
    }

    @Override
    public void load() {
        ConfigurationSection features = config.getConfigurationSection("Features");
        if (features == null)
            throw new NullPointerException("No features specified");

        ConfigurationSection spawns = features.getConfigurationSection("DaySpawns");
        if (spawns == null) {
            MSG.log("No dalight mob spawning config specified");
            return;
        }

        ConfigurationSection weights = spawns.getConfigurationSection("MobWeights");
        if (weights == null)
            throw new IllegalArgumentException("MobWeights is either null or improperly configured");

        corruptChance = new TimeVariable<>(spawns.getConfigurationSection("CorruptionChances"), Double.class);
        for (Map.Entry<String, Object> entry : weights.getValues(false).entrySet()) {
            EntityType type;
            try {
                type = EntityType.valueOf(entry.getKey().toUpperCase());
            } catch (IllegalArgumentException e) {
                MSG.log("Invalid entity type: %s", entry.getKey());
                continue;
            }
            if (!(entry.getValue() instanceof Number)) {
                MSG.log("Invalid entity weight for %s: %s", entry.getKey(), entry.getValue().toString());
                continue;
            }
            mobWeights.put(type, ((Number) entry.getValue()).doubleValue());
        }

        this.minRange = spawns.getDouble("RangeOffset.Min", 3);
        this.maxRange = spawns.getDouble("RangeOffset.Max", 10);
        this.chunkMobs = new TimeVariable<>(spawns.getConfigurationSection("MaxChunkMobs"), Integer.class);
        this.minBlockLevel = spawns.getInt("LightLevels.BlockMin", 0);
        this.maxBlocklevel = spawns.getInt("LightLevels.BlockMax", 7);
        this.minSkyLevel = spawns.getInt("LightLevels.SkyMin", 0);
        this.maxSkyLevel = spawns.getInt("LightLevels.SkyMax", 15);

        ConfigurationSection amounts = spawns.getConfigurationSection("MobAmount");
        if (amounts == null)
            throw new IllegalArgumentException("MobAmounts is either null or improperly configured");

        for (Map.Entry<String, Object> entry : amounts.getValues(false).entrySet()) {
            try {
                mobAmounts.put(Integer.parseInt(entry.getKey()), ((Number) entry.getValue()).doubleValue());
            } catch (NumberFormatException | ClassCastException e) {
                MSG.log("Invalid Number / Value (%s: %s)", entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void save() {
        ConfigurationSection spawns = config.createSection("Features.DaySpawns");
        spawns.set("CorruptionChance", corruptChance);
        spawns.set("MobWeights", mobWeights);
        spawns.set("RangeOffset.Min", minRange);
        spawns.set("RangeOffset.Max", maxRange);
        spawns.set("MobAmount", mobAmounts);
    }
}
