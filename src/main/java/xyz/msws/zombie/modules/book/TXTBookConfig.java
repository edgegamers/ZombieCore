package xyz.msws.zombie.modules.book;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.YMLZConfig;
import xyz.msws.zombie.utils.MSG;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TXTBookConfig extends BookConfig {

    public TXTBookConfig(ZCore plugin, YMLZConfig config) {
        super(plugin, config);
    }

    @Override
    public void load() {
        File file = new File(plugin.getDataFolder(), "book.txt");
        book = new ItemStack(Material.WRITTEN_BOOK);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            bookJson = new String(in.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Bukkit.getUnsafe().modifyItemStack(book, bookJson);
        } catch (Exception e) {
            MSG.log("An error occurred when attempting to generate a book with json: \n%s", bookJson);
        }

        ItemMeta meta = book.getItemMeta();
        assert meta != null;
        meta.getPersistentDataContainer().set(getKey(), PersistentDataType.INTEGER, 1);
        book.setItemMeta(meta);
    }

    @Override
    public void save() {

    }
}
