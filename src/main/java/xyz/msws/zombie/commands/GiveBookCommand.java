package xyz.msws.zombie.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.Lang;
import xyz.msws.zombie.modules.book.BookModule;
import xyz.msws.zombie.utils.MSG;

public class GiveBookCommand extends SubCommand {
    protected GiveBookCommand(String name, ZCore plugin) {
        super(name, plugin);
        setPermission("zombiecore.command.givebook");
        setUsage(" <player>");
        setDescription("Gives the guide book");
        setAliases("book", "gb");
    }

    @Override
    protected boolean exec(CommandSender sender, String label, String[] args) {
        Player target = null;
        if (sender instanceof Player) {
            target = (Player) sender;
        } else if (args.length == 0) {
            MSG.tell(sender, Lang.COMMAND_MISSING_ARGUMENT, "player");
            return true;
        }
        if (sender.hasPermission("zombiecore.command.givebook.others") && args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
        }

        if (target == null) {
            MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, String.join(" ", args), "Invalid player");
            return true;
        }

        plugin.getModuleManager().getModule(BookModule.class).giveBook(target);
        MSG.tell(sender, Lang.COMMAND_GIVEBOOK, target.getName());
        return true;
    }
}
