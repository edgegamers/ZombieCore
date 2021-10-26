package xyz.msws.zombie.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.Lang;
import xyz.msws.zombie.modules.daylight.DaylightConfig;
import xyz.msws.zombie.utils.MSG;

public class DebugCommand extends SubCommand {
    protected DebugCommand(String name, ZCore plugin) {
        super(name, plugin);
        setPermission("zombiecore.command.debug");
        setDescription("Gets info about the current apocalypse");
        setUsage("");
    }

    @Override
    protected boolean exec(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MSG.tell(sender, Lang.COMMAND_PLAYER_ONLY);
            return true;
        }
        DaylightConfig daySpawn = plugin.getZConfig().getConfig(DaylightConfig.class);
        double dayRate = daySpawn.getCorruptionChance().getValue(player.getWorld().getTime());
        int chunkLimit = daySpawn.getChunkMobs().getValue(player.getWorld().getTime());
        MSG.tell(sender, Lang.COMMAND_DEBUG, dayRate * 100, chunkLimit);
        return true;
    }
}
