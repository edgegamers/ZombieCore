package xyz.msws.zombie.data;

import org.bukkit.configuration.file.YamlConfiguration;
import xyz.msws.zombie.utils.MSG;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum Lang {
    COMMAND_PLAYER_ONLY("%prefix% You must be a player to run this command."),
    COMMAND_SPAWN_SPAWNED("%prefix% Successfully spawned a &e%s&7."),
    COMMAND_MISSING_ARGUMENT("%prefix% You must provide an argument: &e%s&7."),
    COMMAND_INVALID_ARGUMENT("%prefix% &cInvalid argument specified: &e%s &7(&8%s&7)."),
    COMMAND_SPAWN_SETATTRIBUTE("%prefix% Successfully set &e%s &7to &a%s&7."),
    COMMAND_SPAWN_ADDPOTION("%prefix% Successfully added &a%s %s %d&7."),
    COMMAND_SPAWN_REMOVE("%prefix% Successully removed &a%s&7."),
    COMMAND_SPAWN_CLEARED("%prefix% Reset custom boss. Type /zc spawn [Entity] to start."),
    COMMAND_SPAWN_STARTED("%prefix% Started customization of &e%s&7. Type &f/zc [property] [value]&7 to customize."),
    COMMAND_RELOAD("%prefix% Successfully reloaded files."),
    COMMAND_SPAWN_SAVED("%prefix% Successfully saved &e%s&7 as a custom boss."),
    COMMAND_DELETE("%prefix% Successfully deleted &e%s&7."),
    BREEDING_EGG("%prefix% &e%s&7 is a &dhopeless &5romantic&7."),
    COMMAND_CONFIG_ERROR("%prefix% Could not set &a%s&7 to &e%s&7 (&8%s&7)."),
    COMMAND_CONFIG_SET("%prefix% Successfully set &a%s&7 to &e%s&7."),
    ;

    private final String[] def;
    private String[] configured;

    Lang(String... def) {
        this.def = def;
        this.configured = def;
    }

    public static void saveFile(File file) {
        YamlConfiguration config = new YamlConfiguration();
        for (Lang l : Lang.values())
            config.set(l.toString(), l.getDefault());
        config.set("Placeholders.%prefix%", "&2&lZombie&3Core&8>&7");
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Assigns each enum's string to the one specified in the config if set
     *
     * @param config File to assign strings from
     */
    public static void load(YamlConfiguration config) {
        Map<String, String> places = new HashMap<>();
        if (config.contains("Placeholders")) {
            for (String s : config.getConfigurationSection("Placeholders").getKeys(false)) {
                places.put(s, config.getString("Placeholders." + s));
            }
        }

        for (String s : config.getKeys(false)) {
            if (s.equals("Placeholders"))
                continue;
            Lang l;
            try {
                l = Lang.valueOf(s);
            } catch (IllegalArgumentException e) {
                MSG.log("Unknown Lang key: %s", s);
                continue;
            }
            String v = config.getString(s);
            l.setConfigured(config.getString(s));
        }


        for (Lang l : Lang.values()) {
            String v = l.getConfigured();
            for (Map.Entry<String, String> p : places.entrySet())
                v = v.replace(p.getKey(), p.getValue());
            l.setConfigured(v);
        }
    }

    public void setConfigured(String... msg) {
        this.configured = msg;
    }

    public String getDefault() {
        return String.join("\n", def);
    }

    public String getConfigured() {
        return String.join("\n", configured);
    }

    public String format(Object... objects) {
        return String.format(getConfigured(), objects);
    }

}
