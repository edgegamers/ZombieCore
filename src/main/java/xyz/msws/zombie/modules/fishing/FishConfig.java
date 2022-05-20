package xyz.msws.zombie.modules.fishing;

import lombok.Getter;
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
    @Getter
    protected long minTime, maxTime;
    @Getter
    protected boolean blockEnchants;

    public FishConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
    }

    public abstract double getChance(double x);

    public abstract Function<Double, Double> getEquation();

    public abstract boolean cancel(long time);

    public long getMaxTime() {
        return maxTime;
    }

    public boolean allow(Material mat) {
        return whitelist.contains(mat);
    }

    @Override
    public String getName() {
        return "fishing";
    }
}
