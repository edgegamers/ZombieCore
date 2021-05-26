package xyz.msws.zombie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.EntityBuilder;

import java.util.List;

public class ZombieCoreCommand extends BaseCommand {

    private SpawnCommand spawn;

    public ZombieCoreCommand(String name, ZCore plugin) {
        super(name, plugin);


        commands.put("spawn", (spawn = new SpawnCommand("spawn", plugin)));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0)
            return super.onCommand(sender, command, label, args);

        if (sender instanceof Player) {
            Player player = (Player) sender;
            EntityBuilder<?> builder = spawn.getBuilder(player.getUniqueId());
            if (builder != null) {
                if (builder.accept(player, String.join(" ", args)))
                    spawn.removeBuilder(player.getUniqueId());
                return true;
            }
        }

        return super.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) throws IllegalArgumentException {
        if (!(sender instanceof Player))
            return super.tabComplete(sender, label, args);
        Player player = (Player) sender;
        if (spawn.getBuilder(player.getUniqueId()) != null)
            return spawn.tab(sender, label, args);

        return super.tabComplete(sender, label, args);
    }
}
