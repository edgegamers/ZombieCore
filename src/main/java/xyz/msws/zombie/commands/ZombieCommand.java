package xyz.msws.zombie.commands;

import org.bukkit.command.defaults.BukkitCommand;
import xyz.msws.zombie.api.ZCore;

/**
 * Represents any {@link ZCore} command
 */
public abstract class ZombieCommand extends BukkitCommand {

    protected final ZCore plugin;

    protected ZombieCommand(String name, ZCore plugin) {
        super(name);
        this.plugin = plugin;
    }
}
