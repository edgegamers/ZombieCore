package xyz.msws.zombie.modules;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Parent module to manager other modules
 */
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

    /**
     * Returns the first valid module that matches the given class type
     *
     * @param clazz Module type to get
     * @param <T>   Module type to get
     * @return The first module that matches the requested type, or null if none exists
     */
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module mod : modules) {
            if (clazz.isAssignableFrom(mod.getClass()))
                return clazz.cast(mod);
        }
        return null;
    }
}
