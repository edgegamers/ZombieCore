package xyz.msws.zombie.data;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
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

import java.util.*;

/**
 * Helper builder for custom mobs
 *
 * @param <T> Entity Class
 */
public class EntityBuilder<T extends Entity> {
    private final Class<T> type;
    private final List<Map.Entry<Attribute, AttributeModifier>> modifiers = new ArrayList<>();
    private String name = null;
    private final Map<EquipmentSlot, ItemStack> items = new HashMap<>();
    private final List<PotionEffect> effects = new ArrayList<>();
    private double hp = -1;
    private final ZCore plugin;
    private final ItemFactory builder;

    public EntityBuilder(ZCore plugin, Class<T> type) {
        this.plugin = plugin;
        this.type = type;
        this.builder = plugin.getItemBuilder();
    }

    /**
     * Adds the specified attribute and modifiers to be assigned on spawn
     *
     * @param attr     {@link Attribute} type to add
     * @param modifier {@link AttributeModifier} modifier of said attribute
     * @return The current Builder
     */
    public EntityBuilder<T> withAttr(Attribute attr, AttributeModifier modifier) {
        modifiers.add(new AbstractMap.SimpleEntry<>(attr, modifier));
        return this;
    }

    /**
     * Sets the mob's custom name
     *
     * @param name Name to set
     * @return The current Builder
     */
    public EntityBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Adds the specified potion effect
     *
     * @param effect {@link PotionEffect} to add
     * @return The current Builder
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
     * @return The current Builder
     */
    public EntityBuilder<T> item(EquipmentSlot slot, ItemStack item) {
        items.put(slot, item);
        return this;
    }

    /**
     * Accepts a user input in the form of [property] [value], returns true if the user requested to reset the mob.
     *
     * @param sender {@link Player} that queried
     * @param query  Query String
     * @return true if the entity should be reset, false otherwise
     */
    public boolean accept(Player sender, String query) {
        Attribute type;
        AttributeModifier modifier;

        if (query.equalsIgnoreCase("spawn")) {
            EntityType entType = spawn(sender.getLocation()).getType();
            MSG.tell(sender, Lang.COMMAND_SPAWN_SPAWNED, MSG.camelCase(entType.toString()));
            return false;
        } else if (query.equalsIgnoreCase("new") || query.equalsIgnoreCase("reset")) {
            MSG.tell(sender, Lang.COMMAND_SPAWN_CLEARED);
            return true;
        }
        int args = query.split(" ").length;
        if (args < 2) {
            MSG.tell(sender, Lang.COMMAND_MISSING_ARGUMENT, args == 0 ? "Attribute Type" : "Value");
            return false;
        }
        String value = String.join(" ", query.substring(query.indexOf(" ") + 1));
        MSG.log("value: %s", value);
        ItemStack item;
        switch (query.split(" ")[0].toLowerCase()) {
            case "hp":
                hp = Double.parseDouble(value);
                MSG.tell(sender, Lang.COMMAND_SPAWN_SETATTRIBUTE, "health", hp);
                return false;
            case "maxhp":
                type = Attribute.GENERIC_MAX_HEALTH;
                break;
            case "name":
                this.name = value;
                MSG.tell(sender, Lang.COMMAND_SPAWN_SETATTRIBUTE, "name", name);
                return false;
            case "head":
            case "chest":
            case "hand":
            case "off_hand":
            case "legs":
            case "feet":
                item = builder.build(value);
                EquipmentSlot slot = Serializer.getEnum(query.split(" ")[0], EquipmentSlot.class);
                if (slot == null) {
                    MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown equipment slot", value);
                    return false;
                }
                item(slot, item);
                MSG.tell(sender, Lang.COMMAND_SPAWN_SETATTRIBUTE, MSG.camelCase(slot.toString()), builder.humanReadable(item));
                return false;
            case "speed":
                type = Attribute.GENERIC_MOVEMENT_SPEED;
                break;
            case "damage":
                type = Attribute.GENERIC_ATTACK_DAMAGE;
                break;
            case "followrange":
                type = Attribute.GENERIC_FOLLOW_RANGE;
                break;
            case "kbres":
                type = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
                break;
            case "kbstr":
                type = Attribute.GENERIC_ATTACK_KNOCKBACK;
                break;
            case "reinforcement":
                type = Attribute.ZOMBIE_SPAWN_REINFORCEMENTS;
                break;
            case "jumpstr":
                type = Attribute.HORSE_JUMP_STRENGTH;
                break;
            case "atkspd":
                type = Attribute.GENERIC_ATTACK_SPEED;
                break;
            case "potion":
                PotionEffectType pot = Utils.getPotionEffect(value.split(" ")[0]);
                if (pot == null) {
                    MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown potion type", value.split(" ")[0]);
                    return false;
                }
                int duration = Integer.MAX_VALUE;
                int level = 1;
                if (value.split(" ").length >= 2)
                    level = Integer.parseInt(value.split(" ")[1]);
                if (value.split(" ").length >= 3)
                    duration = Integer.parseInt(value.split(" ")[2]) * 20;
                this.effect(new PotionEffect(pot, duration, level));
                MSG.tell(sender, Lang.COMMAND_SPAWN_ADDPOTION, duration == Integer.MAX_VALUE ? "Permanent" : MSG.getDuration(duration / 20 * 1000L) + " of", MSG.camelCase(pot.getName()), level);
                return false;
            default:
                MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown attribute", query.split(" ")[0]);
                return false;
        }

        double d = Double.parseDouble(value);
        modifier = new AttributeModifier("", d, AttributeModifier.Operation.ADD_NUMBER);
        withAttr(type, modifier);
        MSG.tell(sender, Lang.COMMAND_SPAWN_SETATTRIBUTE, MSG.camelCase(type.getKey().getKey()), d);
        return false;
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
        if (hp != -1)
            living.setHealth(hp);

        for (Map.Entry<Attribute, AttributeModifier> entry : modifiers) {
            AttributeInstance attr = living.getAttribute(entry.getKey());
            if (attr == null) {
                MSG.log("Could not apply %s attribute to %s", entry.getKey().getKey(), type.getName());
                continue;
            }
            attr.addModifier(entry.getValue());
        }

        EntityEquipment eq = living.getEquipment();
        if (eq != null)
            for (Map.Entry<EquipmentSlot, ItemStack> entry : items.entrySet()) {
                eq.setItem(entry.getKey(), entry.getValue());
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

}
