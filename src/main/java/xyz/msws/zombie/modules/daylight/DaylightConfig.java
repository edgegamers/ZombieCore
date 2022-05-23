package xyz.msws.zombie.modules.daylight;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.util.Vector;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigMap;
import xyz.msws.zombie.data.TimeVariable;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class DaylightConfig extends ModuleConfig<DaylightSpawn> {

    @Getter
    protected double minRange, maxRange, minPlayerRange;
    protected TimeVariable<Double> corruptChance;
    @Getter
    protected TimeVariable<Integer> chunkMobs;
    protected final ConfigMap<Integer, Double> mobAmounts = new ConfigMap<>(new HashMap<>(), Integer.class, Double.class);
    private final Random random;
    @Getter
    protected int minBlockLevel, maxBlocklevel, minSkyLevel, maxSkyLevel;

    public DaylightConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
        random = new Random();
    }

    public boolean doCorrupt(World world) {
        return random.nextDouble() <= corruptChance.getValue(world.getTime());
    }

    public TimeVariable<Double> getCorruptionChance() {
        return corruptChance;
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
            if (v < entry.getValue()) return entry.getKey();
            v -= entry.getValue();
        }
        throw new IllegalArgumentException(String.format("Invalid map data: could not get random type (%.2f/%.2f)", v, total));
    }

    public int getRandomAmount() {
        return getRandom(mobAmounts);
    }

    public boolean blockSpawn(Block block) {
        if (block.isLiquid() || block.getType() == Material.KELP_PLANT || block.getType() == Material.KELP) return true;
        if (block.getBlockData() instanceof Waterlogged log) {
            if (log.isWaterlogged()) return true;
        }
        if (block.getLightFromBlocks() < minBlockLevel || block.getLightFromBlocks() > maxBlocklevel) return true;
        return block.getLightFromSky() < minSkyLevel || block.getLightFromSky() > maxSkyLevel;
    }

    @Override
    public String getName() {
        return "dayspawns";
    }
}
