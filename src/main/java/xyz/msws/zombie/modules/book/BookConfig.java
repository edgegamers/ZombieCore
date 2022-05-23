package xyz.msws.zombie.modules.book;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleConfig;

public abstract class BookConfig extends ModuleConfig<BookModule> {
    @Getter
    protected String bookJson;
    @Getter
    protected ItemStack book;
    @Getter
    private final NamespacedKey key;

    public BookConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
        key = new NamespacedKey(plugin, "zombiebook");
    }

    public boolean isBook(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.getOrDefault(key, PersistentDataType.INTEGER, 0) == 1;
    }

    @Override
    public String getName() {
        return "book";
    }
}
