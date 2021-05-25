package xyz.msws.zombie.features;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager extends Module {

    protected List<Module> modules = new ArrayList<>();

    public ModuleManager(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        modules.forEach(Module::enable);
    }

    public <T extends Module> T addModule(T module) {
        modules.add(module);
        return module;
    }

    @Override
    public void disable() {
        modules.forEach(Module::disable);
        modules = null;
    }
}
