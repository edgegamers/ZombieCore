package xyz.msws.zombie.commands;

import org.bukkit.command.CommandSender;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.Lang;
import xyz.msws.zombie.utils.MSG;

import java.util.ArrayList;
import java.util.List;

public class DeleteCommand extends SubCommand {
    protected DeleteCommand(String name, ZCore plugin) {
        super(name, plugin);
        setPermission("zombiecore.command.delete");
        setDescription("Deletes saved custom mobs");
        setUsage("[Mob]");
    }

    @Override
    protected boolean exec(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            MSG.tell(sender, Lang.COMMAND_MISSING_ARGUMENT, "Mob Name");
            return true;
        }

        if (!(plugin.getCustomMobs().containsKey(args[0]))) {
            MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "No Custom Mob is under that name", args[0]);
            return true;
        }

        plugin.getCustomMobs().get(args[0]).delete(args[0]);
        plugin.getCustomMobs().remove(args[0]);
        MSG.tell(sender, Lang.COMMAND_DELETE, args[0]);
        return true;
    }

    @Override
    protected List<String> tab(CommandSender sender, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length > 1)
            return result;
        for (String res : plugin.getCustomMobs().keySet()) {
            if (res.toLowerCase().startsWith(args[0].toLowerCase()))
                result.add(res);
        }
        return result;
    }
}
