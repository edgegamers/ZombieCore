package xyz.msws.zombie.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper method for {@link org.bukkit.configuration.serialization.ConfigurationSerialization}
 * All methods are shallow copies and ignore transient fields
 */
public class Serializer {
    private static final Map<Class<?>, List<Field>> fieldCache = new HashMap<>();
    private static final Map<Class<?>, Constructor<?>> constructorCache = new HashMap<>();

    /**
     * Serializes the given object
     *
     * @param obj Object to serialize
     * @return Map result
     */
    public static Map<String, Object> serialize(Object obj) {
        Map<String, Object> data = new HashMap<>();
        for (Field f : getFields(obj.getClass())) {
            try {
                data.put(f.getName(), f.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    /**
     * Deserializes a map to the given object type
     *
     * @param data  data to deserialize
     * @param clazz class object type to return
     * @param <T>   type
     * @return The given object, or null if there was an error
     */
    public static <T> T deserialize(Map<String, Object> data, Class<T> clazz) {
        Constructor<T> struct = getConstructor(clazz);
        if (struct == null) throw new IllegalArgumentException(clazz.getName() + " does not have an empty constructor");

        T obj;

        try {
            obj = struct.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        for (Field f : getFields(clazz)) {
            if (!data.containsKey(f.getName())) continue;
            Object v = cast(data.get(f.getName()), f.getType());
            if (v == null)
                throw new ClassCastException(data.get(f.getName()).getClass() + " cannot be casted to " + f.getType());
            try {
                f.set(obj, v);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return obj;
    }

    /**
     * Attempts to cast the specified object to the new type, accounts for UUIDs, collection/lists, and primitives
     *
     * @param obj   Source object to cast
     * @param clazz class type to cast to
     * @param <T>   type
     * @return The given object cast
     */
    public static <T> T cast(Object obj, Class<T> clazz) {
        if (clazz.isAssignableFrom(obj.getClass())) return clazz.cast(obj);
        if (clazz.equals(UUID.class)) {
            return clazz.cast(UUID.fromString((String) obj));
        } else if (clazz.isArray()) {
            if (obj instanceof Collection<?> col) {
                return clazz.cast(col.toArray(new Object[0]));
            }
        } else if (clazz.isPrimitive() && obj instanceof Number n) {
            return (T) n;
        }
        throw new ClassCastException("Could not cast " + obj.getClass() + " to " + clazz);
    }

    private static List<Field> getFields(Class<?> clazz) {
        if (fieldCache.containsKey(clazz)) return fieldCache.get(clazz);
        List<Field> result = Arrays.stream(clazz.getDeclaredFields()).filter(f -> !Modifier.isTransient(f.getModifiers())).collect(Collectors.toList());
        result.forEach(f -> f.setAccessible(true));
        fieldCache.put(clazz, result);
        return result;
    }

    private static <T> Constructor<T> getConstructor(Class<T> clazz) {
        if (constructorCache.containsKey(clazz)) return (Constructor<T>) constructorCache.get(clazz);
        Constructor<T> result = null;
        try {
            result = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        constructorCache.put(clazz, result);
        return result;
    }

    public static <E extends Enum<E>> EnumSet<E> getEnumSet(Collection<String> strings, Class<E> clazz) {
        EnumSet<E> list = EnumSet.noneOf(clazz);
        boolean whitelist = true;
        if (strings.contains("ALL")) {
            list.addAll(Arrays.asList(clazz.getEnumConstants()));
            whitelist = false;
            strings.remove("ALL");
            MSG.log(clazz.getName() + " list had ALL, adding %d items (result: %d)", clazz.getEnumConstants().length, list.size());
        }
        for (String s : strings) {
            if (s.isEmpty()) continue;
            E type = getEnum(s, clazz);
            if (type == null) {
                MSG.warn("Invalid enum specified: %s", s);
                continue;
            }
            if (!whitelist)
                list.remove(type);
            else
                list.add(type);
        }
        return list;
    }

    public static <E extends Enum<E>> E getEnum(String s, Class<E> clazz) {
        s = MSG.normalize(s);
        for (E e : clazz.getEnumConstants())
            if (s.equals(MSG.normalize(e.toString()))) return e;
        for (E e : clazz.getEnumConstants())
            if (s.startsWith(MSG.normalize(e.toString()))) return e;
        for (E e : clazz.getEnumConstants())
            if (s.contains(MSG.normalize(e.toString()))) return e;
        return null;
    }
}
