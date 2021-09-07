package xyz.msws.zombie.modules.daylight;

import com.ericdebouwer.zombieapocalypse.api.ApocalypseAPI;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.EventModule;

public class DaylightSpawn extends EventModule {
    private final DaylightConfig config;

    public DaylightSpawn(ZCore plugin) {
        super(plugin);
        config = plugin.getZConfig().getConfig(DaylightConfig.class);
    }

    @EventHandler(priority = EventPriority.LOW) // Towny Compatability
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL)
            return;
        if (!config.doCorrupt(event.getLocation().getWorld()))
            return;
        if (event.getLocation().getBlock().isLiquid())
            return;
        int cm = config.getChunkMobs().getValue(event.getLocation().getWorld());
        if (cm != -1) {
            Chunk chunk = event.getLocation().getChunk();
            int amo = 0;
            for (Entity ent : chunk.getEntities()) {
                if (!(ent instanceof Monster))
                    continue;
                amo++;
            }
            if (amo > cm)
                return;
        }
        Location origin = event.getLocation().clone().add(config.getRandomOffset());
        int toSpawn = config.getRandomAmount();
        int attempts = 0;
        for (int i = 0; i < toSpawn; i++) {
            if (attempts++ > toSpawn * 2)
                break;
            Location loc = origin.clone().add(config.getRandomOffset(-2, 2));
            if (loc.getWorld() == null)
                break;
            Block source = loc.getWorld().getHighestBlockAt(loc);
            Block block = loc.getWorld().getHighestBlockAt(loc).getRelative(BlockFace.UP);
            if (config.blockSpawn(source) || config.blockSpawn(block)) {
                i--;
                continue;
            }
            ApocalypseAPI.getInstance().spawnZombie(block.getLocation().add(.5, 0, .5));
        }
    }


    @Override
    public void disable() {
        EntitySpawnEvent.getHandlerList().unregister(this);
    }
}
