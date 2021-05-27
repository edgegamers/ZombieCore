package xyz.msws.zombie.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.EntityBuilder;
import xyz.msws.zombie.data.Lang;
import xyz.msws.zombie.data.items.ItemAttribute;
import xyz.msws.zombie.utils.MSG;
import xyz.msws.zombie.utils.Serializer;

import java.util.*;

/**
 * Command for custom boss creation
 */
public class SpawnCommand extends SubCommand implements Listener {
    public SpawnCommand(String name, ZCore plugin) {
        super(name, plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        setPermission("zombiecore.command.spawn");
        setUsage("/<command> spawn [Entity]");
        setDescription("Starts customization of a custom mob");
    }

    private Map<UUID, EntityBuilder<?>> builders = new HashMap<>();

    @Override
    protected boolean exec(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MSG.tell(sender, Lang.COMMAND_PLAYER_ONLY);
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            MSG.tell(sender, Lang.COMMAND_MISSING_ARGUMENT, "Entity Type");
            return true;
        }

        EntityType type;
        type = Serializer.getEnum(args[0], EntityType.class);
        if (type == null) {
            MSG.tell(sender, "Unknown entity type", args[0]);
            return true;
        }
        if (!type.isSpawnable()) {
            MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "That type is not spawnable", MSG.camelCase(type.toString()));
            return true;
        }

        if (type.getEntityClass() == null) {
            MSG.log("Could not find entity class for %s", type);
            MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "An error occured, please check console for details", "Unknown Entity Class");
        }

        builders.put(player.getUniqueId(), new EntityBuilder<>(plugin, type.getEntityClass()));
        MSG.tell(sender, Lang.COMMAND_SPAWN_STARTED, MSG.camelCase(type.toString()));
        return true;
    }

    @Override
    public List<String> tab(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> result = new ArrayList<>();
        if (!(sender instanceof Player))
            return result;
        Player player = (Player) sender;


        if (!builders.containsKey(player.getUniqueId())) {
            for (EntityType type : EntityType.values()) {
                if (!type.isSpawnable())
                    continue;
                if (args.length == 0 || type.toString().toLowerCase().startsWith(MSG.normalize(type.toString())))
                    result.add(MSG.normalize(type.toString()));
            }
            return result;
        }

        if (args.length == 1) {
            List<String> attributes = new ArrayList<>(Arrays.asList("atkspd", "maxhp", "hp", "name", "head", "chest", "hand", "off_hand", "legs", "feet", "speed", "damage", "potion", "followRange", "kbRes", "kbStr", "spawn", "new", "reset"));

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
        } else if (args.length >= 2) {
            if (args[0].matches("(?i)(head|chest|hand|off_hand|legs|feet)")) {
                if (args.length == 2) {
                    try {
                        for (Material mat : Material.values())
                            if (MSG.normalize(mat.getKey().getKey()).startsWith(args[1].toLowerCase()))
                                result.add(MSG.normalize(mat.getKey().getKey()));
                    } catch (NoSuchMethodError e) {
                        // 1.8 Compatibility
                        for (Material mat : Material.values()) {
                            if (MSG.normalize(mat.toString()).startsWith(args[1].toLowerCase())) {
                                result.add(MSG.normalize(mat.toString()));
                            }
                        }
                    }

                    for (String s : new String[]{"@hand", "@inventory", "@block", "@enderchest"}) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            result.add(s);
                        }
                    }
                }
                if (args.length > 2) {
                    for (ItemAttribute attr : plugin.getItemBuilder().getAttributes()) {
                        if (attr.getPermission() != null && !sender.hasPermission(attr.getPermission()))
                            continue;
                        List<String> add = attr.tabComplete(args[args.length - 1], args, sender);
                        if (add == null || add.isEmpty())
                            continue;
                        result.addAll(add);
                    }
                }
            } else if (args[0].equalsIgnoreCase("potion")) {
                if (args.length == 2) {
                    for (PotionEffectType type : PotionEffectType.values())
                        if (MSG.normalize(type.getName()).startsWith(args[1].toLowerCase()))
                            result.add(MSG.normalize(type.getName()));
                } else if (args.length == 3) {
                    result.add("<level>");
                } else if (args.length == 4) {
                    result.add("<duration>");
                }

            }

            return result;
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
