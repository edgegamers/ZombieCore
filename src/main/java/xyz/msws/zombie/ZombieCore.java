package xyz.msws.zombie;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.commands.ZombieCoreCommand;
import xyz.msws.zombie.data.EntityBuilder;
import xyz.msws.zombie.data.Lang;
import xyz.msws.zombie.data.YMLZConfig;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.data.items.ItemFactory;
import xyz.msws.zombie.modules.ModuleManager;
import xyz.msws.zombie.modules.breeding.AnimalBreeding;
import xyz.msws.zombie.modules.daylight.DaylightSpawn;
import xyz.msws.zombie.modules.fishing.FishModule;
import xyz.msws.zombie.modules.noenchant.NoEnchantSpawn;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ZombieCore extends JavaPlugin implements ZCore {
    private ModuleManager manager;
    private ZombieConfig config;
    private Map<String, EntityBuilder<?>> mobs = new HashMap<>();

    @Override
    public void onEnable() {
        loadFiles();
        loadModules();

        getCommand("zombiecore").setExecutor(new ZombieCoreCommand("zombiecore", this));
        refreshMobs();
    }

    private void loadFiles() {
        config = new YMLZConfig(this, new File(getDataFolder(), "config.yml"));
        config.load();
        File langYml = new File(getDataFolder(), "lang.yml");
        if (!langYml.exists())
            Lang.saveFile(langYml);
        Lang.load(YamlConfiguration.loadConfiguration(langYml));
    }

    private void loadModules() {
        manager = new ModuleManager(this);

        manager.addModule(new ItemFactory(this));
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

    @Override
    public ItemFactory getItemBuilder() {
        return manager.getModule(ItemFactory.class);
    }

    @Override
    public Map<String, EntityBuilder<?>> getCustomMobs() {
        return mobs;
    }

    public void refreshMobs() {
        mobs = new HashMap<>();
        File file = new File(getDataFolder(), "data.yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
        for (String key : data.getKeys(false))
            mobs.put(key, EntityBuilder.fromBlueprint(this, data.getStringList(key)));
    }
}
