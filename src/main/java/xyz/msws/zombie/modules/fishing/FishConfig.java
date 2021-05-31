package xyz.msws.zombie.modules.fishing;

import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.function.Function;

public abstract class FishConfig extends ModuleConfig<FishModule> {

    protected Function<Double, Double> method;
    protected long minTime, maxTime;

    public FishConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
    }

    public abstract double getChance(double x);

    public abstract Function<Double, Double> getEquation();

    public abstract boolean cancel(long time);

    public long getMinTime() {
        return minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    @Override
    public String getName() {
        return "fishing";
    }
}
