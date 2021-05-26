package xyz.msws.zombie.data;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import xyz.msws.zombie.utils.MSG;

import java.util.*;

public class EntityBuilder<T extends Entity> {
    private final Class<T> type;
    private final List<Map.Entry<Attribute, AttributeModifier>> modifiers = new ArrayList<>();
    private String name = null;
    private final Map<EquipmentSlot, ItemStack> items = new HashMap<>();
    private final List<PotionEffect> effects = new ArrayList<>();
    private double hp;

    public EntityBuilder(Class<T> type) {
        this.type = type;
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

    public String accept(String query) {
        Attribute type = null;
        AttributeModifier modifier = new AttributeModifier("", 0, AttributeModifier.Operation.ADD_NUMBER);
        if (!query.contains(" "))
            return "Must specify attribute";
        String value = String.join(" ", query.substring(query.indexOf(" ")));
//        "maxhp", "hp", "name", "helmet", "chest", "hand", "offhand", "legs", "boots", "speed", "damage", "followRange", "kbRes")
        switch (query.split(" ")[0].toLowerCase()) {
            case "hp":
                hp = Double.parseDouble(value);
                return String.format("Successfully set hp to %.2f");
            case "maxhp":
                type = Attribute.GENERIC_MAX_HEALTH;
                break;
            case "name":
                this.name = value;
                return String.format("Successfully set name to %s", name);
            case "helmet":
                break;
            case "chest":
                break;
            case "hand":
                break;
            case "offhand":
                break;
            case "legs":
                break;
            case "boots":
                break;
            case "speed":
                break;
            case "damage":
                break;
            case "followrange":
                break;
            case "kbres":
                break;
            case "reinforcement":
                break;
            case "jumpstr":
                break;
        }

        return String.format("Successfully set %s to %s", type, modifier);
    }

    public <E extends Entity> E spawn(Location loc, Class<E> clazz) {
        if (loc.getWorld() == null)
            throw new NullPointerException();
        E ent = loc.getWorld().spawn(loc, clazz);

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
                MSG.log("Could not apply %s attribute to %s", entry.getKey().getKey(), clazz.getName());
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
