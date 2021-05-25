package xyz.msws.zombie;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.features.ModuleManager;

public class ZombieCore extends JavaPlugin implements ZCore {
    private ModuleManager manager;

    @Override
    public void onEnable() {
        this.manager = new ModuleManager(this);


    }

    @Override
    public ModuleManager getModuleManager() {
        return manager;
    }

    @Override
    public ZombieConfig getZConfig() {
        return null;
    }
}
