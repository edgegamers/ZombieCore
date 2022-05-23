package xyz.msws.zombie.commands;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.Lang;
import xyz.msws.zombie.utils.MSG;

import java.util.*;

public class CountCommand extends SubCommand {
    protected CountCommand(ZCore plugin) {
        super("count", plugin);
        setUsage("");
        setDescription("Counts nearby entities");
    }

    @Override
    protected boolean exec(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MSG.tell(sender, Lang.COMMAND_PLAYER_ONLY);
            return true;
        }
        int[] chunkRanges = new int[]{1, 4, 16};
        int maxRange = chunkRanges[chunkRanges.length - 1] * 16;
        LinkedHashMap<EntityType, Map<Integer, Integer>> results = new LinkedHashMap<>();
        LinkedHashMap<EntityType, HashSet<Chunk>> chunks = new LinkedHashMap<>();
        Map<EntityType, Integer> counts = new HashMap<>();

        for (Entity ent : player.getNearbyEntities(maxRange / 2.0, Integer.MAX_VALUE, maxRange / 2.0)) {
            for (int i = chunkRanges.length - 1; i >= 0; i--) {
                Location hLoc = ent.getLocation().clone(), hpLoc = player.getLocation().clone();
                hLoc.setY(0);
                hpLoc.setY(0);
                if (hLoc.distanceSquared(hpLoc) > Math.pow(chunkRanges[i] * 16, 2))
                    break;
                HashSet<Chunk> cs = chunks.getOrDefault(ent.getType(), new HashSet<>());
                cs.add(ent.getLocation().getChunk());
                chunks.put(ent.getType(), cs);

                Map<Integer, Integer> rs = results.getOrDefault(ent.getType(), new HashMap<>());
                rs.put(chunkRanges[i], rs.getOrDefault(chunkRanges[i], 0) + 1);
                results.put(ent.getType(), rs);
            }
            Map<Integer, Integer> rs = results.getOrDefault(ent.getType(), new HashMap<>());
            counts.put(ent.getType(), rs.getOrDefault(chunkRanges[chunkRanges.length - 1], 0));
        }
        int total = counts.values().stream().mapToInt(i -> i).sum();
        if (total == 0) {
            MSG.tell(sender, "&cNo mobs nearby.");
            return true;
        }
        StringJoiner joiner = new StringJoiner("\n");
        StringBuilder header = new StringBuilder("\n" + MSG.FORMATTER + "Mobs within chunks " + MSG.FORMATTER + "(");
        for (int chunk : chunkRanges) {
            header.append(MSG.NUMBER).append(chunk).append(MSG.FORMATTER).append("/");
        }
        header = new StringBuilder(header.substring(0, header.length() - 2) + MSG.FORMATTER + ")");
        header.append(String.format(" &8[&7%d/&f%d total&8]", total, player.getWorld().getEntities().size()));
        joiner.add(header.toString());
        for (Map.Entry<EntityType, Integer> entry : counts.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList()) {
            StringBuilder builder = new StringBuilder("&3" + String.format("%1.0f", (double) entry.getValue() / total * 100) + "%%&9> " + MSG.FORMAT_INFO + MSG.camelCase(entry.getKey().toString()) + ": " + MSG.NUMBER);
            for (Map.Entry<Integer, Integer> mobs : results.get(entry.getKey()).entrySet().stream().sorted(Map.Entry.comparingByValue()).toList()) {
                builder.append(mobs.getValue()).append(ChatColor.DARK_GRAY).append("/").append(MSG.NUMBER);
            }
            builder.delete(builder.length() - 3, builder.length());
            int cAmo = chunks.getOrDefault(entry.getKey(), new HashSet<>()).size();
            builder.append(String.format(" &8[&b%d &7chunks &9> &a%.2f&7/chunk&8]", cAmo, (double) counts.get(entry.getKey()) / cAmo));
            joiner.add(builder);
        }
        MSG.tell(sender, joiner.toString());
        return true;
    }
}
