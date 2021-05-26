package xyz.msws.zombie.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.EntityBuilder;
import xyz.msws.zombie.utils.MSG;

import java.util.*;

public class SpawnCommand extends SubCommand implements Listener {
    public SpawnCommand(String name, ZCore plugin) {
        super(name, plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private Map<UUID, EntityBuilder<?>> builders = new HashMap<>();

    @Override
    protected boolean exec(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MSG.tell(sender, "You must be a player to run this command.");
            return true;
        }

        Player player = (Player) sender;
        if (builders.containsKey(player.getUniqueId())) {
            MSG.tell(sender, "You are already making another entity. Type \"quit\" to restart.");
            return true;
        }

        if (args.length != 1) {
            MSG.tell(sender, "You must specify an entity type.");
            return true;
        }

        EntityType type;
        try {
            type = EntityType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            MSG.tell(sender, "Unknown entity type.");
            return true;
        }
        if (!type.isSpawnable()) {
            MSG.tell(sender, "You cannot spawn that entity.");
            return true;
        }

        if (type.getEntityClass() == null) {
            MSG.tell(sender, "An unknown error occured, please check logs for details.");
            MSG.log("Could not find entity class for %s", type);
        }

        builders.put(player.getUniqueId(), new EntityBuilder<>(plugin, type.getEntityClass()));
        MSG.tell(sender, "Custom entity creation started. Type /zc [property] <value> to build the entity:");
        return true;
    }

    @Override
    public List<String> tab(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> result = new ArrayList<>();
        if (!(sender instanceof Player))
            return result;
        Player player = (Player) sender;

        if (args.length > 1)
            return result;

        if (!builders.containsKey(player.getUniqueId())) {
            for (EntityType type : EntityType.values()) {
                if (!type.isSpawnable())
                    continue;
                if (args.length == 0 || type.toString().toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(type.toString());
            }
            return result;
        }
        List<String> attributes = new ArrayList<>(Arrays.asList("atkspd", "maxhp", "hp", "name", "head", "chest", "hand", "off_hand", "legs", "feet", "speed", "damage", "followRange", "kbRes", "kbStr", "spawn"));

        Class<? extends Entity> clazz = builders.get(player.getUniqueId()).getType();

        if (Zombie.class.isAssignableFrom(clazz)) {
            attributes.add("reinforcement");
        } else if (Horse.class.isAssignableFrom(clazz)) {
            attributes.add("jumpStr");
        }

        for (String attr : attributes) {
            if (attr.toLowerCase().startsWith(args[0].toLowerCase()))
                result.add(attr);
        }

        return result;
    }

    public EntityBuilder<?> getBuilder(UUID uuid) {
        return builders.get(uuid);
    }

    public void removeBuilder(UUID uuid) {
        builders.remove(uuid);
    }
}
