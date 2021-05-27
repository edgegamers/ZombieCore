package xyz.msws.zombie.modules.fishing;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.YMLZConfig;
import xyz.msws.zombie.utils.MSG;

import java.util.Random;
import java.util.function.Function;

public class YMLFishConfig extends FishConfig {

    private final YamlConfiguration config;
    private double constant, offset, exp;
    private final Random random;

    public YMLFishConfig(ZCore plugin, YMLZConfig config) {
        super(plugin, config);

        this.config = config.getYml();
        this.random = new Random();
    }

    @Override
    public void load() {
        ConfigurationSection fish = config.getConfigurationSection("Features.Fish");
        if (fish == null) {
            MSG.log("No fish features have been configured.");
            return;
        }

        constant = fish.getDouble("CancelChance.Constant", 80);
        offset = fish.getDouble("CancelChance.Offset", 80);
        exp = fish.getDouble("CancelChance.Exponent", -1);
        minTime = fish.getLong("MinTime", 20000);
        maxTime = fish.getLong("MaxTime", -1);

        this.method = x -> constant * Math.pow(x + offset, exp);
    }

    @Override
    public void save() {
        ConfigurationSection fish = config.createSection("Fish");
        fish.set("CancelChance.Constant", constant);
        fish.set("CancelChance.Offset", offset);
        fish.set("CancelChance.Exponent", exp);
        fish.set("MinTime", minTime);
        fish.set("MaxTime", maxTime);
    }

    @Override
    public double getChance(double x) {
        return method.apply(x);
    }

    @Override
    public Function<Double, Double> getEquation() {
        return method;
    }

    @Override
    public boolean cancel(long time) {
        if (maxTime != -1 && time > maxTime)
            return false;
        if (minTime != -1 && time < minTime)
            return true;
        double t = time / 1000.0;
        MSG.log("Checking f(%1f) = %.2f", t, method.apply(t));
        return random.nextDouble() < method.apply(t);
    }


}
