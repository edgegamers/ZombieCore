package xyz.msws.zombie.commands;

import org.bukkit.command.CommandSender;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.Lang;
import xyz.msws.zombie.utils.MSG;

import java.io.File;

public class ResetCommand extends SubCommand {
    protected ResetCommand(ZCore plugin) {
        super("reset", plugin);
        setPermission("zombiecore.command.reset");
        setUsage("");
        setDescription("Resets ZombieCore's config and lang files");
    }

    @Override
    protected boolean exec(CommandSender sender, String label, String[] args) {
        plugin.getZConfig().reset();
        Lang.saveFile(new File(plugin.getDataFolder(), "lang.yml"));
        MSG.tell(sender, "&2&lZombie&3Core&8>&7 Successfully reset all files.");
        return true;
    }
}
