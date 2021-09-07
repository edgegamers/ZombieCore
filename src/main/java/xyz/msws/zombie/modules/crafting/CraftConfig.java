package xyz.msws.zombie.modules.crafting;

import org.bukkit.Material;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigCollection;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.EnumSet;

public abstract class CraftConfig extends ModuleConfig<CraftBlocker> {

    protected ConfigCollection<Material> block = new ConfigCollection<>(EnumSet.noneOf(Material.class), Material.class);
    protected String result;

    public CraftConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
    }

    public boolean blockRecipe(Material mat) {
        return block.contains(mat);
    }

    public String getItemResult() {
        return result;
    }

    @Override
    public String getName() {
        return "crafting";
    }
}
