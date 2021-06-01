package xyz.msws.zombie.commands;

import org.bukkit.command.CommandSender;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigMap;
import xyz.msws.zombie.data.Lang;
import xyz.msws.zombie.modules.ModuleConfig;
import xyz.msws.zombie.utils.MSG;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ConfigCommand extends SubCommand {

    private Map<Class<ModuleConfig<?>>, Map<String, Field>> fields = new HashMap<>();
    private Map<String, ModuleConfig<?>> names = new HashMap<>();

    protected ConfigCommand(String name, ZCore plugin) {
        super(name, plugin);

        for (ModuleConfig<?> config : plugin.getZConfig().getConfigs())
            load(config);

        setPermission("zombiecore.command.config");
        setDescription("Modify ZombieCore configuration");
        setUsage("[feature] [key] [value]");
    }

    private void load(ModuleConfig<?> config) {
        names.put(config.getName(), config);
        Map<String, Field> fieldMap = fields.getOrDefault(config.getClass(), new HashMap<>());
        for (Field f : config.getClass().getSuperclass().getDeclaredFields()) {
            f.setAccessible(true);
            fieldMap.put(f.getName(), f);
        }
        fields.put((Class<ModuleConfig<?>>) config.getClass(), fieldMap);
    }

    @Override
    protected boolean exec(CommandSender sender, String label, String[] args) {
        String missing = switch (args.length) {
            case 0 -> "Feature";
            case 1 -> "Key";
            case 2 -> "Value";
            default -> null;
        };
        if (missing != null) {
            MSG.tell(sender, Lang.COMMAND_MISSING_ARGUMENT, missing);
            return true;
        }

        ModuleConfig<?> feature = names.get(args[0]);
        if (feature == null) {
            MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown feature", args[0]);
            return true;
        }
        Field field = fields.get(feature.getClass()).get(args[1]);
        if (field == null) {
            MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown key", args[1]);
            return true;
        }

        StringJoiner joiner = new StringJoiner(" ");
        for (int i = 2; i < args.length; i++)
            joiner.add(args[i]);
        Class<?> type = field.getType();
        Object value = null;
        try {
            if (ConfigMap.class.isAssignableFrom(type)) {
                if (joiner.toString().split(" ").length < 2) {
                    MSG.tell(sender, Lang.COMMAND_MISSING_ARGUMENT, "Key Value");
                    return true;
                }
                String[] jArgs = joiner.toString().split(" ");
                String keyString = jArgs[0], valueString = jArgs[1];
                ConfigMap<?, ?> cm = (ConfigMap<?, ?>) field.get(feature);
                Object key = cast(keyString, cm.getKey());
                if (valueString.equalsIgnoreCase("null") || valueString.equalsIgnoreCase("remove")) {
                    cm.remove(key);
                    MSG.tell(sender, Lang.COMMAND_CONFIG_REMOVE, keyString, field.getName());
                    return true;
                }

                value = cast(valueString, cm.getValue());

                if (value == null) {
                    MSG.tell(sender, Lang.COMMAND_CONFIG_ERROR, args[1], valueString, "Unable to cast to " + cm.getValue());
                    return true;
                }

                cm.putObject(key, value);
                MSG.tell(sender, Lang.COMMAND_CONFIG_SET, field.getName() + " (" + key + ")", value);
                return true;
            } else {
                value = cast(joiner.toString(), type);
            }
        } catch (NumberFormatException nf) {
            MSG.tell(sender, Lang.COMMAND_CONFIG_ERROR, args[1], joiner.toString(), "Must be a " + type.getSimpleName());
            return true;
        } catch (ClassCastException cc) {
            MSG.tell(sender, Lang.COMMAND_CONFIG_ERROR, args[1], joiner.toString(), "Unable to cast to " + type.getSimpleName());
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (value == null) {
            MSG.tell(sender, Lang.COMMAND_CONFIG_ERROR, args[1], joiner.toString(), "Unable to parse, please set value in config manually.");
            return true;
        }
        try {
            field.set(feature, value);
        } catch (ClassCastException | IllegalAccessException e) {
            MSG.tell(sender, Lang.COMMAND_CONFIG_ERROR, args[1], joiner.toString(), e.getMessage());
            return true;
        }
        MSG.tell(sender, Lang.COMMAND_CONFIG_SET, field.getName(), value + "");
        return true;
    }

    private <T> T cast(Object obj, Class<T> type) throws ClassCastException, NumberFormatException {
        Object value = null;
        if (type == String.class) {
            value = obj.toString();
        } else if (type == boolean.class || type == Boolean.class) {
            value = Boolean.parseBoolean(obj.toString());
        } else if (type == int.class || type == Integer.class) {
            value = Integer.parseInt(obj.toString());
        } else if (type == double.class || type == Double.class) {
            value = Double.parseDouble(obj.toString());
        } else if (type == float.class || type == Float.class) {
            value = Float.parseFloat(obj.toString());
        } else if (type == long.class || type == Long.class) {
            value = Long.parseLong(obj.toString());
        } else if (type.isEnum()) {
            try {
                value = type.getMethod("valueOf", String.class).invoke(null, obj.toString());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException unused) {
                try {
                    value = type.getMethod("fromString", String.class).invoke(null, obj.toString());
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException unused2) {
                    return null;
                }
            }
        }
        return type.cast(value);
    }

    @Override
    public List<String> tab(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> result = new ArrayList<>();
        ModuleConfig<?> conf;
        switch (args.length) {
            case 0:
            case 1:
                for (String res : names.keySet()) {
                    if (args.length == 0 || res.toLowerCase().startsWith(args[0].toLowerCase()))
                        result.add(res);
                }
                break;
            case 2:
                conf = names.get(args[0]);
                if (conf == null)
                    break;
                if (fields.get(conf.getClass()) == null)
                    break;
                for (String res : fields.get(conf.getClass()).keySet()) {
                    if (res.toLowerCase().startsWith(args[args.length - 1]))
                        result.add(res);
                }
                break;
            case 3:
                conf = names.get(args[0]);
                if (conf == null)
                    break;
                if (fields.get(conf.getClass()) == null)
                    break;
                if (!fields.get(conf.getClass()).containsKey(args[1]))
                    break;
                try {
                    Field field = fields.get(conf.getClass()).get(args[1]);
                    if (ConfigMap.class.isAssignableFrom(field.getType())) {
                        ConfigMap<?, ?> cm = (ConfigMap<?, ?>) field.get(conf);
                        for (Object obj : cm.keySet()) {
                            if (obj.toString().startsWith(args[args.length - 1]))
                                result.add(obj.toString());
                        }
                        break;
                    }
                    result.add(fields.get(conf.getClass()).get(args[1]).get(conf) + "");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                conf = names.get(args[0]);
                if (conf == null)
                    break;
                if (fields.get(conf.getClass()) == null)
                    break;
                if (!fields.get(conf.getClass()).containsKey(args[1]))
                    break;
                try {
                    Field field = fields.get(conf.getClass()).get(args[1]);
                    if (ConfigMap.class.isAssignableFrom(field.getType())) {
                        ConfigMap<?, ?> tv = (ConfigMap<?, ?>) field.get(conf);
                        Object key = cast(args[2], tv.getKey());
                        if (tv.get(key) == null)
                            break;
                        if (tv.get(key).toString().startsWith(args[args.length - 1]))
                            result.add(tv.get(key).toString());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                for (String res : new String[]{"null", "remove"}) {
                    if (res.startsWith(args[args.length - 1]))
                        result.add(res);
                }
                break;
        }
        return result;
    }
}
