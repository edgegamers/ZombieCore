package xyz.msws.zombie.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.items.ItemFactory;
import xyz.msws.zombie.utils.MSG;
import xyz.msws.zombie.utils.Serializer;
import xyz.msws.zombie.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Helper builder for custom mobs
 *
 * @param <T> Entity Class
 */
public class EntityBuilder<T extends Entity> implements Cloneable {
    private final Class<T> type;
    private final List<Map.Entry<Attribute, AttributeModifier>> modifiers = new ArrayList<>();
    private String name = null;
    private final Map<EquipmentSlot, ItemStack> items = new HashMap<>();
    private final Map<EquipmentSlot, Float> rates = new HashMap<>();
    private final List<PotionEffect> effects = new ArrayList<>();
    private double hp = -1;
    private final ZCore plugin;
    private final ItemFactory builder;
    private final List<String> blueprint = new ArrayList<>();

    public EntityBuilder(ZCore plugin, Class<T> type) {
        this.plugin = plugin;
        this.type = type;
        this.builder = plugin.getItemBuilder();
        blueprint.add(type.getName());
    }

    public static <T extends Entity> EntityBuilder<?> fromBlueprint(ZCore plugin, List<String> bp) {
        Class<T> type;
        try {
            type = (Class<T>) Class.forName(bp.get(0));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        EntityBuilder<?> builder = new EntityBuilder<>(plugin, type);
        for (int i = 1; i < bp.size(); i++)
            builder.accept(bp.get(i));
        return builder;
    }

    public List<String> getBlueprint() {
        return blueprint;
    }

    /**
     * Adds the specified attribute and modifiers to be assigned on spawn
     *
     * @param attr     {@link Attribute} type to add
     * @param modifier {@link AttributeModifier} modifier of said attribute
     * @return The resulting Builder
     */
    public EntityBuilder<T> withAttr(Attribute attr, AttributeModifier modifier) {
        modifiers.add(new AbstractMap.SimpleEntry<>(attr, modifier));
        return this;
    }

    /**
     * Sets the mob's custom name
     *
     * @param name Name to set
     * @return The resulting Builder
     */
    public EntityBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set's the mob's current HP
     *
     * @param hp Hp points to set
     * @return The resulting Builder
     */
    public EntityBuilder<T> health(double hp) {
        this.hp = hp;
        return this;
    }

    /**
     * Adds the specified potion effect
     *
     * @param effect {@link PotionEffect} to add
     * @return The resulting Builder
     */
    public EntityBuilder<T> effect(PotionEffect effect) {
        effects.add(effect);
        return this;
    }

    /**
     * Sets the specified {@link EquipmentSlot} to the {@link ItemStack}
     *
     * @param slot {@link EquipmentSlot} to set
     * @param item {@link ItemStack} to set slot to
     * @return The resulting Builder
     */
    public EntityBuilder<T> item(EquipmentSlot slot, ItemStack item) {
        items.put(slot, item);
        return this;
    }


    /**
     * Accepts a user input in the form of [property] [value], returns true if the user requested to reset the mob.
     *
     * @param query Query String
     * @return true if the entity should be reset, false otherwise
     */
    public boolean accept(String query) {
        return accept(Bukkit.getConsoleSender(), query);
    }

    /**
     * Accepts a user input in the form of [property] [value], returns true if the user requested to reset the mob.
     *
     * @param sender {@link Player} that queried
     * @param query  Query String
     * @return true if the entity should be reset, false otherwise
     */
    public boolean accept(CommandSender sender, String query) {
        Attribute type;
        AttributeModifier modifier;
        blueprint.add(query);
        int args = query.split(" ").length;
        switch (query.toLowerCase()) {
            case "spawn" -> {
                if (!(sender instanceof Player))
                    return false;
                EntityType entType = spawn(((Player) sender).getLocation()).getType();
                MSG.tell(sender, Lang.COMMAND_SPAWN_SPAWNED, MSG.camelCase(entType.toString()));
                blueprint.remove(blueprint.size() - 1);
                return false;
            }
            case "new", "reset" -> {
                MSG.tell(sender, Lang.COMMAND_SPAWN_CLEARED);
                blueprint.clear();
                return true;
            }
            case "save" -> {
                blueprint.remove(blueprint.size() - 1);
                if (args != 2) {
                    MSG.tell(sender, Lang.COMMAND_MISSING_ARGUMENT, "Mob Name");
                    return false;
                }
                String name = query.split(" ")[1];
                File file = new File(plugin.getDataFolder(), "data.yml");
                YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
                data.set(name, this.getBlueprint());
                try {
                    data.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                plugin.refreshMobs();
                MSG.tell(sender, Lang.COMMAND_SPAWN_SAVED, name);
                return false;
            }
        }
        if (args < 2) {
            MSG.tell(sender, Lang.COMMAND_MISSING_ARGUMENT, args == 0 ? "Attribute Type" : "Value");
            return false;
        }
        String value = String.join(" ", query.substring(query.indexOf(" ") + 1));
        ItemStack item;
        switch (query.split(" ")[0].toLowerCase()) {
            case "hp", "health" -> {
                double hp = checkNumber(sender, value);
                if (hp == -1) {
                    blueprint.remove(blueprint.size() - 1);
                    return false;
                }
                health(hp);
                MSG.tell(sender, Lang.COMMAND_SPAWN_SETATTRIBUTE, "Health", hp);
                return false;
            }
            case "maxhp", "maxhealth" -> type = Attribute.GENERIC_MAX_HEALTH;
            case "name" -> {
                name(value);
                MSG.tell(sender, Lang.COMMAND_SPAWN_SETATTRIBUTE, "Name", name);
                return false;
            }
            case "head", "chest", "hand", "off_hand", "legs", "feet" -> {
                EquipmentSlot slot = Serializer.getEnum(query.split(" ")[0], EquipmentSlot.class);
                if (slot == null) {
                    MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown equipment slot", value);
                    blueprint.remove(blueprint.size() - 1);
                    return false;
                }
                item = builder.build(value);
                if (item == null) {
                    MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown item", value);
                    blueprint.remove(blueprint.size() - 1);
                    return false;
                }
                item(slot, item);
                MSG.tell(sender, Lang.COMMAND_SPAWN_SETATTRIBUTE, MSG.camelCase(slot.toString()), builder.humanReadable(item));
                return false;
            }
            case "speed", "movespeed" -> type = Attribute.GENERIC_MOVEMENT_SPEED;
            case "damage", "strength" -> type = Attribute.GENERIC_ATTACK_DAMAGE;
            case "followrange", "sight", "perception", "range" -> type = Attribute.GENERIC_FOLLOW_RANGE;
            case "kbres", "weight", "mass", "knockbacksesistance" -> type = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
            case "kbstr", "fightSpeed", "brute", "knockbackstrength" -> type = Attribute.GENERIC_ATTACK_KNOCKBACK;
            case "reinforcement" -> type = Attribute.ZOMBIE_SPAWN_REINFORCEMENTS;
            case "jumpstr", "jump", "jumpstrength" -> type = Attribute.HORSE_JUMP_STRENGTH;
            case "atkspd", "attackspeed", "rof", "reloadspeed" -> type = Attribute.GENERIC_ATTACK_SPEED;
            case "flyspd", "zoom", "flight" -> type = Attribute.GENERIC_FLYING_SPEED;
            case "potion", "potions" -> {
                PotionEffectType pot = Utils.getPotionEffect(value.split(" ")[0]);
                if (pot == null) {
                    MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown potion type", value.split(" ")[0]);
                    return false;
                }
                int duration = Integer.MAX_VALUE;
                int level = 1;
                try {
                    if (value.split(" ").length >= 2)
                        level = Integer.parseInt(value.split(" ")[1]);
                } catch (NumberFormatException e) {
                    MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown level", value.split(" ")[1]);
                    blueprint.remove(blueprint.size() - 1);
                    return false;
                }
                try {
                    if (value.split(" ").length >= 3)
                        duration = Integer.parseInt(value.split(" ")[2]) * 20;
                } catch (NumberFormatException e) {
                    MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown duration", value.split(" ")[2]);
                    blueprint.remove(blueprint.size() - 1);
                }
                this.effect(new PotionEffect(pot, duration, level));
                MSG.tell(sender, Lang.COMMAND_SPAWN_ADDPOTION, duration == Integer.MAX_VALUE ? "Permanent" : MSG.getDuration(duration / 20 * 1000L) + " of", MSG.camelCase(pot.getName()), level);
                return false;
            }
            case "removepotion" -> {
                PotionEffectType pot = Utils.getPotionEffect(value.split(" ")[0]);
                if (pot == null) {
                    MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown potion type", value.split(" ")[0]);
                    return false;
                }

                effects.removeIf(effect -> effect.getType() == pot);
                MSG.tell(sender, Lang.COMMAND_SPAWN_REMOVE, MSG.camelCase(pot.getName()));
                return false;
            }
            case "dropchance" -> {
                float chance = (float) checkNumber(value);
                if (chance == -1) {
                    blueprint.remove(blueprint.size() - 1);
                    return false;
                }
                if (chance > 1)
                    chance /= 100f;
                for (EquipmentSlot slot : EquipmentSlot.values())
                    rates.put(slot, chance);
                MSG.tell(sender, Lang.COMMAND_SPAWN_SETATTRIBUTE, "All Drop Chances", chance);
                return false;
            }
            case "headdropchance", "chestdropchance", "handdropchance", "off_handdropchance", "legsdropchance", "feetdropchance",
                    "headdc", "chestdc", "handdc", "off_handdc", "legsdc", "feetdc" -> {
                EquipmentSlot slot = Serializer.getEnum(query.split(" ")[0].toLowerCase().replace("dropchance", "").replace("dc", ""), EquipmentSlot.class);
                if (slot == null) {
                    MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown equipment slot", value);
                    blueprint.remove(blueprint.size() - 1);
                    return false;
                }
                float chance = (float) checkNumber(value);
                if (chance == -1) {
                    blueprint.remove(blueprint.size() - 1);
                    return false;
                }
                rates.put(slot, chance);
                MSG.tell(sender, Lang.COMMAND_SPAWN_SETATTRIBUTE, MSG.camelCase(slot.toString()) + " Drop Chance", chance);
                return false;
            }
            default -> {
                MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown attribute", query.split(" ")[0]);
                blueprint.remove(blueprint.size() - 1);
                return false;
            }
        }

        double d = checkNumber(sender, value);
        if (d == -1) {
            blueprint.remove(blueprint.size() - 1);
            return false;
        }
        modifier = new AttributeModifier("", d, AttributeModifier.Operation.ADD_NUMBER);
        withAttr(type, modifier);
        MSG.tell(sender, Lang.COMMAND_SPAWN_SETATTRIBUTE, MSG.camelCase(type.getKey().getKey()), d);
        return false;
    }

    private double checkNumber(CommandSender sender, String msg) {
        double result = checkNumber(msg);
        if (result == -1)
            MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Invalid number", msg);
        return result;
    }

    private double checkNumber(String msg) {
        try {
            return Double.parseDouble(msg);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Spawns the given entity at the specified location
     *
     * @param loc {@link Location} to spawn the entity at
     * @return The entity type specified in the original constructor ({@link EntityBuilder(Class)}
     */
    public T spawn(Location loc) {
        if (loc.getWorld() == null)
            throw new NullPointerException();
        T ent = loc.getWorld().spawn(loc, type, t -> t.setMetadata("ignoreZombie", new FixedMetadataValue(plugin, true)));

        if (name != null) {
            ent.setCustomName(MSG.color(name));
            ent.setCustomNameVisible(true);
        }

        if (!(ent instanceof LivingEntity))
            return ent;

        LivingEntity living = (LivingEntity) ent;
        for (Map.Entry<Attribute, AttributeModifier> entry : modifiers) {
            AttributeInstance attr = living.getAttribute(entry.getKey());
            if (attr == null) {
                MSG.log("Could not apply %s attribute to %s", entry.getKey().getKey(), type.getName());
                continue;
            }
            attr.addModifier(entry.getValue());
        }

        if (hp != -1) {
            if (living.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null && hp > living.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
                living.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hp);
            living.setHealth(hp);
        }

        EntityEquipment eq = living.getEquipment();
        if (eq != null) {
            for (Map.Entry<EquipmentSlot, ItemStack> entry : items.entrySet()) {
                eq.setItem(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<EquipmentSlot, Float> entry : rates.entrySet()) {
                switch (entry.getKey()) {
                    case HAND -> eq.setItemInMainHandDropChance(entry.getValue());
                    case OFF_HAND -> eq.setItemInOffHandDropChance(entry.getValue());
                    case HEAD -> eq.setHelmetDropChance(entry.getValue());
                    case CHEST -> eq.setChestplateDropChance(entry.getValue());
                    case LEGS -> eq.setLeggingsDropChance(entry.getValue());
                    case FEET -> eq.setBootsDropChance(entry.getValue());
                }
            }
        }


        for (PotionEffect effect : effects)
            living.addPotionEffect(effect);
        return ent;
    }


    /**
     * Returns the Builder's Entity Class
     *
     * @return The Entity class
     */
    public Class<T> getType() {
        return type;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void delete(String name) {
        File file = new File(plugin.getDataFolder(), "data.yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
        data.set(name, null);
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
