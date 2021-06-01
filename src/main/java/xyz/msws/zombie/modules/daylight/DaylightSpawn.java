package xyz.msws.zombie.modules.daylight;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.EventModule;
import xyz.msws.zombie.utils.MSG;

public class DaylightSpawn extends EventModule {
    private final DaylightConfig config;

    public DaylightSpawn(ZCore plugin) {
        super(plugin);
        config = plugin.getZConfig().getConfig(DaylightConfig.class);
    }

    @EventHandler(priority = EventPriority.LOW) // Towny Compatability
    public void onSpawn(CreatureSpawnEvent event) {
        if (config.getMobWeights().containsKey(event.getEntityType()))
            return;
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

        EntityType type = config.getRandomType();
        Location origin = event.getLocation().clone().add(config.getRandomOffset());
        int toSpawn = config.getRandomAmount();
        int attempts = 0;
        for (int i = 0; i < toSpawn; i++) {
            if (attempts++ > toSpawn * 2)
                break;
            Location loc = origin.clone().add(config.getRandomOffset(-2, 2));
            if (loc.getWorld() == null)
                break;
            Block block = loc.getWorld().getHighestBlockAt(loc);
            if (!config.allowSpawn(block)) {
                MSG.announce("&cBlocked &7spawn at &e%s &7(&2b&7:&a%d &6s&7:&e%d&7)", MSG.camelCase(block.getType().toString()), block.getLightFromBlocks(), block.getLightFromSky());
                i--;
                continue;
            }
            MSG.announce("&aAllowed &7spawn at &e%s &7(&2b&7:&a%d &6s&7:&e%d&7)", MSG.camelCase(block.getType().toString()), block.getLightFromBlocks(), block.getLightFromSky());
            loc.getWorld().spawnEntity(block.getLocation().add(.5, 1, .5), type);
        }
    }


    @Override
    public void disable() {
        EntitySpawnEvent.getHandlerList().unregister(this);
    }
}
