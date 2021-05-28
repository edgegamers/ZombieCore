package xyz.msws.zombie.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.Lang;
import xyz.msws.zombie.utils.MSG;

import java.io.File;

public class ReloadCommand extends SubCommand {
    protected ReloadCommand(String name, ZCore plugin) {
        super(name, plugin);
        setPermission("zombiecore.command.reload");
        setUsage("");
        setDescription("Reload ZombieCore's config and lang files");
    }

    @Override
    protected boolean exec(CommandSender sender, String label, String[] args) {
        plugin.getZConfig().reload();
        Lang.load(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang.yml")));
        MSG.tell(sender, Lang.COMMAND_RELOAD);
        return true;
    }
}
