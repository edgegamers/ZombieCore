package xyz.msws.zombie.modules.daylight;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigMap;
import xyz.msws.zombie.data.TimeVariable;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class DaylightConfig extends ModuleConfig<DaylightSpawn> {

    protected ConfigMap<EntityType, Double> mobWeights = new ConfigMap<>(new EnumMap<>(EntityType.class), EntityType.class, Double.class);
    protected double minRange, maxRange;
    protected TimeVariable<Double> corruptChance;
    protected TimeVariable<Integer> chunkMobs;
    protected ConfigMap<Integer, Double> mobAmounts = new ConfigMap<>(new HashMap<>(), Integer.class, Double.class);
    protected Random random;
    protected int minBlockLevel, maxBlocklevel, minSkyLevel, maxSkyLevel;

    public DaylightConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
        random = new Random();
    }

    public boolean doCorrupt(World world) {
        return random.nextDouble() <= corruptChance.getValue(world.getTime());
    }

    public double getMobWeight(EntityType type) {
        return mobWeights.getOrDefault(type, 0.0);
    }

    public void setMobWeight(EntityType type, double rate) {
        mobWeights.put(type, rate);
    }

    public ConfigMap<EntityType, Double> getMobWeights() {
        return mobWeights;
    }

    public TimeVariable<Double> getCorruptionChance() {
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
        throw new IllegalArgumentException(String.format("Invalid map data: could not get random type (%.2f/%.2f)", v, total));
    }

    public int getRandomAmount() {
        return getRandom(mobAmounts);
    }

    public TimeVariable<Integer> getChunkMobs() {
        return chunkMobs;
    }

    public boolean allowSpawn(Block block) {
        if (block.isLiquid())
            return false;
        if (block.getLightFromBlocks() < minBlockLevel || block.getLightFromBlocks() > maxBlocklevel)
            return false;
        return block.getLightFromSky() >= minSkyLevel && block.getLightFromSky() <= maxSkyLevel;
    }

    @Override
    public String getName() {
        return "dayspawns";
    }
}
