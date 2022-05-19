package xyz.msws.zombie.modules.fishing;

import org.bukkit.Material;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigCollection;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.EnumSet;
import java.util.function.Function;

public abstract class FishConfig extends ModuleConfig<FishModule> {

    protected Function<Double, Double> method;
    protected ConfigCollection<Material> whitelist = new ConfigCollection<>(EnumSet.noneOf(Material.class), Material.class);
    //    protected ConfigCollection<Material> restricted = new ConfigCollection<>(EnumSet.noneOf(Material.class), Material.class);
    protected long minTime, maxTime;
    protected boolean blockEnchants;

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

    //    public boolean restrict(Material mat) {
//        return restricted.contains(mat);
//    }
    public boolean allow(Material mat) {
        return whitelist.contains(mat);
    }

    public boolean blockEnchants() {
        return blockEnchants;
    }

    @Override
    public String getName() {
        return "fishing";
    }
}
