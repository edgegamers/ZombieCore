package xyz.msws.zombie.utils;

import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Global utility class for commonly used methods
 *
 * @author MSWS
 */
public class Utils {

    public static Color getColor(String line) {
        if (!line.contains("|")) {
            try {
                CColor custom = null;
                custom = CColor.valueOf(line.toUpperCase());
                return custom.bukkit();
            } catch (IllegalArgumentException ignored) {
            }
        }
        try {
            String rs = line.split("\\|")[0];
            String gs = line.split("\\|")[1];
            String bs = line.split("\\|")[2];
            return Color.fromRGB(Integer.parseInt(rs), Integer.parseInt(gs), Integer.parseInt(bs));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static PotionEffectType getPotionEffect(String type) {
        String result = getOption(type, Arrays.stream(PotionEffectType.values()).filter(Objects::nonNull).map(PotionEffectType::getName).collect(Collectors.toList()));
        return result == null ? null : PotionEffectType.getByName(result);
    }

    public static ItemFlag getItemFlag(String flag) {
        String result = getOption(flag, ItemFlag.values());
        return result == null ? null : ItemFlag.valueOf(result);
    }

    public static String getOption(String key, Collection<?> options) {
        List<String> values = options.stream().map(Object::toString).collect(Collectors.toList());
        values = values.stream().map(MSG::normalize).toList();
        key = MSG.normalize(key);
        if (values.contains(key)) return key;
        for (String s : values)
            if (s.startsWith(key)) return s;
        for (String s : values)
            if (s.contains(key)) return s;
        return null;
    }

    public static String getOption(String key, Object[] options) {
        return getOption(key, Arrays.asList(options));
    }

    @SuppressWarnings("deprecation")
    public static Enchantment getEnchantment(String ench) {
        try {
            return Enchantment.getByKey(NamespacedKey.minecraft(ench.toUpperCase()));
        } catch (IllegalArgumentException e) {
            ench = MSG.normalize(ench);
            for (Enchantment en : Enchantment.values())
                if (MSG.normalize(en.getKey().getKey()).equalsIgnoreCase(ench)) return en;
            for (Enchantment en : Enchantment.values())
                if (MSG.normalize(en.getKey().getKey()).startsWith(ench)) return en;
            for (Enchantment en : Enchantment.values())
                if (MSG.normalize(en.getKey().getKey()).contains(ench)) return en;
        } catch (NoClassDefFoundError e) {
            for (Enchantment en : Enchantment.values())
                if (MSG.normalize(en.getName()).equalsIgnoreCase(ench)) return en;
            for (Enchantment en : Enchantment.values())
                if (MSG.normalize(en.getName()).startsWith(ench)) return en;
            for (Enchantment en : Enchantment.values())
                if (MSG.normalize(en.getName()).contains(ench)) return en;
        }
        return null;
    }
}
