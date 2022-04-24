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
import xyz.msws.zombie.utils.Utils;

import java.util.*;

/**
 * Command for custom boss creation
 */
public class SpawnCommand extends SubCommand implements Listener {


    public SpawnCommand(String name, ZCore plugin) {
        super(name, plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        setPermission("zombiecore.command.spawn");
        setUsage(" [Entity]");
        setDescription("Starts customization of a custom mob");
    }

    private final Map<UUID, EntityBuilder<?>> builders = new HashMap<>();

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
            String name = Utils.getOption(args[0], plugin.getCustomMobs().keySet());
            if (name != null) {
                try {
                    builders.put(player.getUniqueId(), (EntityBuilder<?>) plugin.getCustomMobs().get(name).clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                MSG.tell(sender, Lang.COMMAND_SPAWN_STARTED, name);
                return true;
            }

            MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown entity type", args[0]);
            return true;
        }
        if (!type.isSpawnable()) {
            MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "That type is not spawnable", MSG.camelCase(type.toString()));
            return true;
        }

        if (type.getEntityClass() == null) {
            MSG.log("Could not find entity class for %s", type);
            MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "An error occurred, please check console for details", "Unknown Entity Class");
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
            for (String name : plugin.getCustomMobs().keySet()) {
                if (args.length == 0 || MSG.normalize(name).startsWith(MSG.normalize(args[0])))
                    result.add(name);
            }
            for (EntityType type : EntityType.values()) {
                if (!type.isSpawnable())
                    continue;
                if (args.length == 0 || MSG.normalize(type.toString()).startsWith(MSG.normalize(args[0])))
                    result.add(MSG.normalize(type.toString()));
            }
            return result;
        }

        if (args.length == 1) {
            List<String> attributes = new ArrayList<>();
            attributes.addAll(Arrays.asList("atkspd", "flyspd", "maxhp", "hp", "health", "name", "head", "chest", "hand", "off_hand", "legs", "feet"));
            attributes.addAll(Arrays.asList("speed", "damage", "potion", "removepotion", "followRange", "kbRes", "kbStr", "spawn", "new", "reset", "quit", "exit", "save"));
            attributes.addAll(Arrays.asList("headDropChance", "chestDropChance", "handDropChance", "off_handDropChance", "legsDropChance"));
            attributes.addAll(Arrays.asList("feetDropChance", "headDC", "chestDC", "handDC", "off_handDC", "legsDC", "feetDC", "dropChance"));

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
                        List<String> add = attr.tabComplete(args[args.length - 1], args, sender);
                        if (add == null || add.isEmpty())
                            continue;
                        result.addAll(add);
                    }
                }
            } else if (args[0].toLowerCase().contains("potion")) {
                if (args.length == 2) {
                    for (PotionEffectType type : PotionEffectType.values())
                        if (MSG.normalize(type.getName()).startsWith(args[1].toLowerCase()))
                            result.add(MSG.normalize(type.getName()));
                } else if (args.length == 3) {
                    result.add("<level>");
                } else if (args.length == 4) {
                    result.add("<duration>");
                }
            } else {
                EntityBuilder<?> builder = builders.get(player.getUniqueId());
                String add = builder.getDefault(args[0]);
                if (add != null)
                    result.add(add);
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
