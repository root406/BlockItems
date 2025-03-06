package de.joel.blockItems;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlockItems extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        removeIllegalItems(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (shouldRemove(event.getCurrentItem())) {
            event.setCurrentItem(null);
            event.setCancelled(true);
            event.getWhoClicked().sendMessage("§cDieses Item ist nicht erlaubt!");
        }
    }

    @EventHandler
    public void onCreativeInventory(InventoryCreativeEvent event) {
        if (shouldRemove(event.getCursor())) {
            event.setCursor(null);
            event.setCancelled(true);
            event.getWhoClicked().sendMessage("§cDieses Item ist nicht erlaubt!");
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (shouldRemove(event.getItem().getItemStack())) {
            event.getItem().remove();
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cDieses Item ist nicht erlaubt!");
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if (shouldRemove(item)) {
            event.setCancelled(true);
            player.getInventory().setItem(event.getNewSlot(), null);
            player.sendMessage("§cDieses Item ist nicht erlaubt!");
            player.updateInventory();
        }
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (shouldRemove(event.getMainHandItem())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cDieses Item ist nicht erlaubt!");
        }
    }

    private void removeIllegalItems(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (shouldRemove(item)) {
                player.getInventory().remove(item);
            }
        }
        player.updateInventory();
    }

    private boolean shouldRemove(ItemStack item) {
        if (item == null) return false;

        if (item.getType().toString().endsWith("_SPAWN_EGG")) {
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if (!data.getKeys().isEmpty()) {
                return true;
            }

            if (!meta.getEnchants().isEmpty()) {
                return true;
            }


            if (meta.hasAttributeModifiers()) {
                return true;
            }
        }

        if (item.getType() == Material.POTION || item.getType() == Material.LINGERING_POTION || item.getType() == Material.SPLASH_POTION) {
            return true;  // Blockiere Tränke
        }

        return false;
    }
}
