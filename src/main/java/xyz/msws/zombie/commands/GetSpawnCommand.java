package xyz.msws.zombie.commands;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.Lang;
import xyz.msws.zombie.modules.daylight.DaylightConfig;
import xyz.msws.zombie.utils.MSG;

import java.util.Arrays;

public class GetSpawnCommand extends SubCommand {
    protected GetSpawnCommand(String name, ZCore plugin) {
        super(name, plugin);

        setDescription("Checks if zombies can spawn where you are standing");
        setAliases(Arrays.asList("testspawn", "getspawn"));
    }

    @Override
    protected boolean exec(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MSG.tell(sender, Lang.COMMAND_PLAYER_ONLY);
            return true;
        }
        Player player = (Player) sender;
        Block feet = player.getLocation().getBlock(), head = feet.getRelative(BlockFace.UP);

        DaylightConfig config = plugin.getZConfig().getConfig(DaylightConfig.class);

        MSG.tell(player, "Head Block: %s (%b)", MSG.camelCase(head.getType().toString()), config.blockSpawn(head));
        MSG.tell(player, "Feet Block: %s (%b)", MSG.camelCase(feet.getType().toString()), config.blockSpawn(feet));
        return true;
    }
}
