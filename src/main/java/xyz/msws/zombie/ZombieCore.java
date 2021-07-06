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
import xyz.msws.zombie.modules.apocalypse.ApoModule;
import xyz.msws.zombie.modules.book.BookModule;
import xyz.msws.zombie.modules.breeding.AnimalBreeding;
import xyz.msws.zombie.modules.crafting.CraftBlocker;
import xyz.msws.zombie.modules.daylight.DaylightSpawn;
import xyz.msws.zombie.modules.fishing.FishModule;
import xyz.msws.zombie.modules.named.NamedSpawn;
import xyz.msws.zombie.modules.noenchant.NoEnchantSpawn;
import xyz.msws.zombie.modules.passivespawn.PassiveSpawn;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ZombieCore extends JavaPlugin implements ZCore {
    private ModuleManager manager;
    private ZombieConfig config;
    private Map<String, EntityBuilder<?>> mobs = new HashMap<>();

    @Override
    public void onEnable() {
        loadFiles();
        loadModules();

        Objects.requireNonNull(getCommand("zombiecore")).setExecutor(new ZombieCoreCommand("zombiecore", this));
        refreshMobs();
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    private void loadFiles() {

        File langYml = new File(getDataFolder(), "lang.yml"), book = new File(getDataFolder(), "book.txt");
        if (!langYml.exists())
            Lang.saveFile(langYml);
        if (!book.exists())
            saveResource("book.txt", true);
        Lang.load(YamlConfiguration.loadConfiguration(langYml));
        config = new YMLZConfig(this, new File(getDataFolder(), "config.yml"));
        config.load();
    }

    private void loadModules() {
        this.manager = new ModuleManager(this);
        manager.addModule(new ItemFactory(this));
        manager.addModule(new AnimalBreeding(this));
        manager.addModule(new DaylightSpawn(this));
        manager.addModule(new FishModule(this));
        manager.addModule(new NoEnchantSpawn(this));
        manager.addModule(new NamedSpawn(this));
        manager.addModule(new PassiveSpawn(this));
        manager.addModule(new ApoModule(this));
        manager.addModule(new CraftBlocker(this));
        manager.addModule(new BookModule(this));

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
