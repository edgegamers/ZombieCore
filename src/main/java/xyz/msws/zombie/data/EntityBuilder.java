package xyz.msws.zombie.data;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.items.ItemBuilder;
import xyz.msws.zombie.utils.MSG;

import java.util.*;

public class EntityBuilder<T extends Entity> {
    private final Class<T> type;
    private final List<Map.Entry<Attribute, AttributeModifier>> modifiers = new ArrayList<>();
    private String name = null;
    private final Map<EquipmentSlot, ItemStack> items = new HashMap<>();
    private final List<PotionEffect> effects = new ArrayList<>();
    private double hp = -1;
    private final ZCore plugin;
    private ItemBuilder builder;

    public EntityBuilder(ZCore plugin, Class<T> type) {
        this.plugin = plugin;
        this.type = type;
        this.builder = plugin.getItemBuilder();
    }

    public EntityBuilder<T> withAttr(Attribute attr, AttributeModifier modifier) {
        modifiers.add(new AbstractMap.SimpleEntry<>(attr, modifier));
        return this;
    }

    public EntityBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public EntityBuilder<T> effect(PotionEffect effect) {
        effects.add(effect);
        return this;
    }

    public EntityBuilder<T> item(EquipmentSlot slot, ItemStack item) {
        items.put(slot, item);
        return this;
    }

    public boolean accept(Player sender, String query) {
        Attribute type = null;
        AttributeModifier modifier;

        if (query.equalsIgnoreCase("spawn")) {
            spawn(sender.getLocation());
            MSG.tell(sender, "Spawned entity");
            return false;
        }
        if (!query.contains(" ")) {
            MSG.tell(sender, "Must specify attribute");
            return false;
        }
        String value = String.join(" ", query.substring(query.indexOf(" ") + 1));
        MSG.log("value: %s", value);
        ItemStack item;
        switch (query.split(" ")[0].toLowerCase()) {
            case "hp":
                hp = Double.parseDouble(value);
                MSG.tell(sender, "Successfully set hp to %.2f", hp);
                return false;
            case "maxhp":
                type = Attribute.GENERIC_MAX_HEALTH;
                break;
            case "name":
                this.name = value;
                MSG.tell(sender, "Successfully set name to %s", name);
                return false;
            case "head":
            case "chest":
            case "hand":
            case "off_hand":
            case "legs":
            case "feet":
                item = builder.build(value);
                EquipmentSlot slot = EquipmentSlot.valueOf(query.split(" ")[0].toUpperCase());
                item(slot, item);
                MSG.tell(sender, "Successfully set the %s to %s", slot, builder.humanReadable(item));
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
            case "new":
                MSG.tell(sender, "Cleared properties");
                return true;
            default:
                MSG.tell(sender, "Unknown attribute");
                return false;
        }

        double d = Double.parseDouble(value);
        modifier = new AttributeModifier("", d, AttributeModifier.Operation.ADD_NUMBER);
        withAttr(type, modifier);
        MSG.tell(sender, "Successfully set %s to %.2f", type.getKey().getKey(), d);
        return false;
    }

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


    public Class<T> getType() {
        return type;
    }

}
