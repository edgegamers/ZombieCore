package xyz.msws.zombie.data;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import xyz.msws.zombie.utils.MSG;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TimeVariable<T> {
    private final Map<Long, T> values = new TreeMap<>();

    public TimeVariable(Map<Long, T> data) {
        values.putAll(data);
    }

    public TimeVariable(ConfigurationSection section, Class<T> clazz) {
        if (section == null)
            throw new NullPointerException("Section is null");
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            values.put(Long.parseLong(entry.getKey()), clazz.cast(entry.getValue()));
        }
    }

    public T getValue(long time) {
        T last;
        List<Map.Entry<Long, T>> entries = new ArrayList<>(values.entrySet());
        for (int i = 0; i < entries.size() - 2; i++) {
            if (time >= entries.get(i).getKey() && time < entries.get(i + 1).getKey())
                return entries.get(i).getValue();
        }
        if (time >= entries.get(entries.size() - 1).getKey())
            return entries.get(entries.size() - 1).getValue();
        return null;
    }

    public T getValue(World world) {
        return getValue(world.getTime());
    }
}
