package xyz.msws.zombie.commands;

import org.bukkit.command.defaults.BukkitCommand;
import xyz.msws.zombie.api.ZCore;

public abstract class ZombieCommand extends BukkitCommand {

    protected ZCore plugin;

    protected ZombieCommand(String name, ZCore plugin) {
        super(name);
        this.plugin = plugin;
    }
}
