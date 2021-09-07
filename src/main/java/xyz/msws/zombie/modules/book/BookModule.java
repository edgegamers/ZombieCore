package xyz.msws.zombie.modules.book;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.modules.EventModule;

public class BookModule extends EventModule {

    protected BookConfig config;

    public BookModule(ZCore plugin) {
        super(plugin);
        this.config = plugin.getZConfig().getConfig(BookConfig.class);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (!config.isBook(item))
            return;
        event.getItemDrop().remove();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem(), cursor = event.getCursor();
        if (item == null || cursor == null)
            return;
        HumanEntity player = event.getWhoClicked();


        if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) {
            if (event.getClickedInventory() != null && event.getClickedInventory().equals(player.getInventory()))
                return;
            if (config.isBook(cursor)) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }
        }
        if (event.isShiftClick()) {
            if (player.getOpenInventory().getTopInventory().getType() == InventoryType.CRAFTING && player.getOpenInventory().getBottomInventory().getType() == InventoryType.PLAYER)
                return;
            if (config.isBook(item)) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }
        }

        removeBook(event.getInventory());
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        removeBook(event.getInventory());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        removeBook(event.getInventory());
    }

    private void removeBook(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (!config.isBook(item))
                continue;
            inventory.setItem(i, new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onSwap(InventoryMoveItemEvent event) {
        if (!config.isBook(event.getItem()))
            return;
        event.setItem(new ItemStack(Material.AIR));
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!config.isBook(event.getCursor()))
            return;
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
    }

    public void giveBook(Player player) {
        int bookSlot = getBookSlot(player), spaceSlot = getSpace(player);
        if (spaceSlot == -1) { // Player has no space in inventory
            player.openBook(config.getBook());
            return;
        }
        if (bookSlot >= 0 && bookSlot < 9) {
            player.getInventory().setHeldItemSlot(bookSlot);
        } else if (spaceSlot < 9) {
            player.getInventory().setHeldItemSlot(spaceSlot);
        }

        player.getInventory().setItem(bookSlot == -1 ? spaceSlot : bookSlot, player.getInventory().getItemInMainHand());
        player.getInventory().setItemInMainHand(config.getBook());
        player.openBook(config.getBook());
    }

    public int getBookSlot(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (config.isBook(item))
                return i;
        }
        return -1;
    }

    public int getBookSlot(HumanEntity player) {
        return getBookSlot(player.getInventory());
    }

    private int getSpace(Inventory inv) {
        // Armor / Crafting / Non storage slots >= 36
        for (int i = 0; i < inv.getSize() && i < 36; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR)
                return i;
        }
        return -1;
    }

    private int getSpace(HumanEntity player) {
        return getSpace(player.getInventory());
    }

    @Override
    public void disable() {
        EntityDropItemEvent.getHandlerList().unregister(this);
    }
}
