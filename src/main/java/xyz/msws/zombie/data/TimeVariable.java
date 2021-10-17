package xyz.msws.zombie.data;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TimeVariable<T> extends ConfigMap<Long, T> {
    private final Class<T> type;

    public TimeVariable(ConfigurationSection section, Class<T> clazz) {
        super(new TreeMap<>(), Long.class, clazz);
        if (section == null)
            throw new NullPointerException("Section is null");
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            put(Long.parseLong(entry.getKey()), clazz.cast(entry.getValue()));
        }
        this.type = clazz;
    }

    public Class<T> getType() {
        return type;
    }

    public T getValue(long time) {
        List<Map.Entry<Long, T>> entries = new ArrayList<>(entrySet());
        if (time >= entries.get(entries.size() - 1).getKey())
            return entries.get(entries.size() - 1).getValue();
        for (int i = 0; i < entries.size() - 1; i++) {
            if (time >= entries.get(i).getKey() && time < entries.get(i + 1).getKey())
                return entries.get(i).getValue();
        }

        throw new NullPointerException("Could not get value for " + time + ". Ordered keys: " + entries.stream().map(s -> s.getKey().toString()).collect(Collectors.joining(", ")) + ".");
    }

    public T getValue(World world) {
        return getValue(world.getTime());
    }
}
