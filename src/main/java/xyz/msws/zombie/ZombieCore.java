package xyz.msws.zombie;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.commands.ZombieCoreCommand;
import xyz.msws.zombie.data.YMLZConfig;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleManager;
import xyz.msws.zombie.modules.breeding.AnimalBreeding;
import xyz.msws.zombie.modules.daylight.DaylightSpawn;
import xyz.msws.zombie.modules.fishing.FishModule;
import xyz.msws.zombie.modules.noenchant.NoEnchantSpawn;

import java.io.File;

public class ZombieCore extends JavaPlugin implements ZCore {
    private ModuleManager manager;
    private ZombieConfig config;

    @Override
    public void onEnable() {
        loadFiles();
        loadModules();

        getCommand("zombiecore").setExecutor(new ZombieCoreCommand("zombiecore", this));
    }

    private void loadFiles() {
        config = new YMLZConfig(this, new File(getDataFolder(), "config.yml"));
        config.load();
    }

    private void loadModules() {
        manager = new ModuleManager(this);

        manager.addModule(new AnimalBreeding(this));
        manager.addModule(new DaylightSpawn(this));
        manager.addModule(new FishModule(this));
        manager.addModule(new NoEnchantSpawn(this));

        manager.enable();
    }

    @Override
    public void onDisable() {
        manager.disable();
    }

    @Override
    public ModuleManager getModuleManager() {
        return manager;
    }

    @Override
    public ZombieConfig getZConfig() {
        return config;
    }
}
