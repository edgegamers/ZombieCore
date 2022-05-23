package xyz.msws.zombie.commands;

import org.bukkit.command.CommandSender;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigCollection;
import xyz.msws.zombie.data.ConfigMap;
import xyz.msws.zombie.data.Lang;
import xyz.msws.zombie.modules.ModuleConfig;
import xyz.msws.zombie.utils.MSG;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigCommand extends SubCommand {

    private final Map<Class<ModuleConfig<?>>, Map<String, Field>> fields = new HashMap<>();
    private final Map<String, ModuleConfig<?>> names = new HashMap<>();

    protected ConfigCommand(ZCore plugin) {
        super("config", plugin);

        for (ModuleConfig<?> config : plugin.getZConfig().getConfigs())
            load(config);

        setPermission("zombiecore.command.config");
        setDescription("Modify ZombieCore configuration");
        setUsage(" [feature] [key] [value]");
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
                if (key == null) {
                    MSG.tell(sender, Lang.COMMAND_CONFIG_ERROR, field.getName(), keyString, "Must be a " + cm.getKey().getSimpleName());
                    return true;
                }
                if (value == null) {
                    MSG.tell(sender, Lang.COMMAND_CONFIG_ERROR, field.getName(), valueString, "Unable to cast to " + cm.getValue().getSimpleName());
                    return true;
                }

                cm.putObject(key, value);
                MSG.tell(sender, Lang.COMMAND_CONFIG_SET, field.getName() + " (" + key + ")", value);
                return true;
            }
            if (ConfigCollection.class.isAssignableFrom(type)) {
                String[] jArgs = joiner.toString().split(" ");
                ConfigCollection<?> cm = (ConfigCollection<?>) field.get(feature);
                if (jArgs[0].equalsIgnoreCase("list")) {
                    MSG.tell(sender, Lang.COMMAND_CONFIG_LIST, field.getName(), cm.stream().map(Object::toString).collect(Collectors.joining("&7, &e")));
                    return true;
                }
                if (joiner.toString().split(" ").length < 2) {
                    MSG.tell(sender, Lang.COMMAND_MISSING_ARGUMENT, "Key Operation");
                    return true;
                }
                String keyString = jArgs[0], valueString = jArgs[1];
                value = cast(valueString, cm.type());
                if (value == null) {
                    MSG.tell(sender, Lang.COMMAND_CONFIG_ERROR, field.getName(), valueString, "Unable to cast to " + cm.type().getSimpleName());
                    return true;
                }
                switch (keyString.toLowerCase()) {
                    case "add" -> {
                        cm.addObject(value);
                        MSG.tell(sender, Lang.COMMAND_CONFIG_ADD, value, field.getName());
                        return true;
                    }
                    case "remove" -> {
                        cm.remove(value);
                        MSG.tell(sender, Lang.COMMAND_CONFIG_REMOVE, value, field.getName());
                        return true;
                    }
                    case "clear" -> {
                        cm.clear();
                        MSG.tell(sender, Lang.COMMAND_CONFIG_CLEAR, field.getName());
                        return true;
                    }
                    default -> {
                        MSG.tell(sender, Lang.COMMAND_INVALID_ARGUMENT, "Unknown key operation", keyString);
                        return true;
                    }
                }
            }
            MSG.log("Casting " + joiner.toString() + " to " + type);
            MSG.log("result: " + cast(joiner.toString(), type));
            value = cast(joiner.toString(), type);
        } catch (NumberFormatException nf) {
            MSG.tell(sender, Lang.COMMAND_CONFIG_ERROR, field.getName(), joiner.toString(), "Must be a " + type.getSimpleName());
            return true;
        } catch (ClassCastException cc) {
            MSG.tell(sender, Lang.COMMAND_CONFIG_ERROR, field.getName(), joiner.toString(), "Unable to cast " + value + " [" + joiner.toString() + "]" + " to " + type.getSimpleName());
            cc.printStackTrace();
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            MSG.tell(sender, Lang.COMMAND_CONFIG_ERROR, field.getName(), joiner.toString(), e.getMessage());
            return true;
        }
        if (value == null) {
            MSG.tell(sender, Lang.COMMAND_CONFIG_ERROR, field.getName(), joiner.toString(), "Unable to parse, please set value in config manually.");
            return true;
        }
        try {
            field.set(feature, value);
        } catch (IllegalAccessException e) {
            MSG.tell(sender, Lang.COMMAND_CONFIG_ERROR, args[1], joiner.toString(), e.getMessage());
            e.printStackTrace();
            return true;
        }
        MSG.tell(sender, Lang.COMMAND_CONFIG_SET, field.getName(), value + "");
        return true;
    }

    private <T> T cast(java.io.Serializable obj, Class<T> type) throws ClassCastException, NumberFormatException {
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
                value = type.getDeclaredMethod("valueOf", String.class).invoke(null, obj.toString());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException unused) {
                unused.printStackTrace();
                try {
                    value = type.getDeclaredMethod("fromString", String.class).invoke(null, obj.toString());
                    MSG.log("value (fromString) assigned to %s", value + "");
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException unused2) {
                    return null;
                }
            }
        }
        return (T) value;
    }

    @Override
    public List<String> tab(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> result = new ArrayList<>();
        ModuleConfig<?> conf = null;
        Field field = null;
        if (args.length >= 2)
            conf = names.get(args[0]);
        if (args.length >= 3 && conf != null && fields.containsKey(conf.getClass()))
            field = fields.get(conf.getClass()).get(args[1]);
        switch (args.length) {
            case 0:
            case 1:
                for (String res : names.keySet()) {
                    if (args.length == 0 || res.toLowerCase().startsWith(args[0].toLowerCase()))
                        result.add(res);
                }
                break;
            case 2:
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
                if (conf == null || field == null)
                    break;
                try {
                    if (ConfigMap.class.isAssignableFrom(field.getType())) {
                        ConfigMap<?, ?> cm = (ConfigMap<?, ?>) field.get(conf);
                        for (Object obj : cm.keySet()) {
                            if (MSG.normalize(obj.toString()).startsWith(MSG.normalize(args[args.length - 1])))
                                result.add(obj.toString());
                        }
                        break;
                    } else if (ConfigCollection.class.isAssignableFrom(field.getType())) {
                        for (String res : new String[]{"add", "remove", "clear", "list"}) {
                            if (res.startsWith(args[args.length - 1].toLowerCase()))
                                result.add(res);
                        }
                    } else {
                        result.add(fields.get(conf.getClass()).get(args[1]).get(conf) + "");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                if (conf == null || field == null)
                    break;
                try {
                    if (ConfigMap.class.isAssignableFrom(field.getType())) {
                        ConfigMap<?, ?> tv = (ConfigMap<?, ?>) field.get(conf);
                        Object key = cast(args[2], tv.getKey());
                        if (tv.get(key) == null)
                            break;
                        if (MSG.normalize(tv.get(key).toString()).startsWith(MSG.normalize(args[args.length - 1])))
                            result.add(tv.get(key).toString());
                    } else if (ConfigCollection.class.isAssignableFrom(field.getType())) {
                        ConfigCollection<?> collection = (ConfigCollection<?>) field.get(conf);
                        for (Object res : collection)
                            if (MSG.normalize(res.toString()).startsWith(MSG.normalize(args[args.length - 1])))
                                result.add(res.toString());
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
