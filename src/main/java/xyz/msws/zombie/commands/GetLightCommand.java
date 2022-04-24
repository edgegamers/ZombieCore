package xyz.msws.zombie.commands;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.Lang;
import xyz.msws.zombie.utils.MSG;

import java.util.Arrays;

public class GetLightCommand extends SubCommand {
    protected GetLightCommand(String name, ZCore plugin) {
        super(name, plugin);
        setAliases(Arrays.asList("getlight"));
    }

    @Override
    protected boolean exec(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MSG.tell(sender, Lang.COMMAND_PLAYER_ONLY);
            return true;
        }

        Player player = (Player) sender;
        Block block = player.getLocation().getBlock();
        MSG.tell(sender, "&7The current level at %s is &e%d&7/&a%d&7/&e%d&7.", MSG.camelCase(block.getType().toString()), block.getLightLevel(), block.getLightFromBlocks(), block.getLightFromSky());
        return true;
    }
}
