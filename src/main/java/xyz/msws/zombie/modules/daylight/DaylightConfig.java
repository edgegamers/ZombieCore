package xyz.msws.zombie.modules.daylight;

import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class DaylightConfig extends ModuleConfig<DaylightSpawn> {

    protected EnumMap<EntityType, Double> mobWeights = new EnumMap<>(EntityType.class);
    protected double corruptChance, minRange, maxRange;
    protected Map<Integer, Double> mobAmounts = new HashMap<>();
    protected Random random;
    protected int chunkMobs;

    public DaylightConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
        random = new Random();
    }

    public double getMobWeight(EntityType type) {
        return mobWeights.getOrDefault(type, 0.0);
    }

    public void setMobWeight(EntityType type, double rate) {
        mobWeights.put(type, rate);
    }

    public EnumMap<EntityType, Double> getMobWeights() {
        return mobWeights;
    }

    public double getCorruptionChance() {
        return corruptChance;
    }

    public double getMinRange() {
        return minRange;
    }

    public double getMaxRange() {
        return maxRange;
    }

    public EntityType getRandomType() {
        return getRandom(mobWeights);
    }

    public Vector getRandomOffset() {
        return getRandomOffset(minRange, maxRange);
    }

    public Vector getRandomOffset(double min, double max) {
        return new Vector(getRandom(min, max), 0, getRandom(min, max));
    }

    public double getRandom(double min, double max) {
        return random.nextDouble() * (max - min) + min;
    }

    private <K> K getRandom(Map<K, Double> map) {
        double total = 0;
        for (Double weight : map.values())
            total += weight;

        double v = random.nextDouble() * total;
        for (Map.Entry<K, Double> entry : map.entrySet()) {
            if (v < entry.getValue())
                return entry.getKey();
            v -= entry.getValue();
        }
        throw new IllegalArgumentException(String.format("Invalid map data: could not get random type (%.2f, %.2f)", v, total));
    }

    public int getRandomAmount() {
        return getRandom(mobAmounts);
    }

    public boolean doCorrupt() {
        return random.nextDouble() < corruptChance;
    }

    public int getChunkMobs() {
        return chunkMobs;
    }

    @Override
    public String getName() {
        return "spawning";
    }
}
