package me.thejokerdev.skyffa.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Iterator;

public class MenuListener implements Listener {
    public static HashMap<String, HashMap<String, Menu>> menus = new HashMap();

    public MenuListener() {
    }

    public static HashMap<String, Menu> getPlayerMenus(Player var0) {
        return menus.containsKey(var0.getName()) ? menus.get(var0.getName()) : new HashMap();
    }

    public static Menu getPlayerMenu(Player var0, String var1) {
        return getPlayerMenus(var0).getOrDefault(var1, null);
    }

    @EventHandler
    public void onPlayerLeaveInvRemove(PlayerQuitEvent var1) {
        menus.remove(var1.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerKickInvRemove(PlayerKickEvent var1) {
        menus.remove(var1.getPlayer().getName());
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent var1) {
        Iterator var2 = getPlayerMenus((Player)var1.getPlayer()).values().iterator();

        while(var2.hasNext()) {
            Menu var3 = (Menu)var2.next();
            if (var1.getView().getTitle().equals(var3.getTitle())) {
                var3.onOpen(var1);
            }
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent var1) {
        Iterator var2 = getPlayerMenus((Player)var1.getPlayer()).values().iterator();

        while(var2.hasNext()) {
            Menu var3 = (Menu)var2.next();
            if (var1.getView().getTitle().equals(var3.getTitle())) {
                var3.onClose(var1);
            }
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent var1) {
        Iterator var2 = getPlayerMenus((Player)var1.getWhoClicked()).values().iterator();

        while(var2.hasNext()) {
            Menu var3 = (Menu)var2.next();
            if (var1.getView().getTitle().equals(var3.getTitle()) && var1.getCurrentItem() != null) {
                var1.setCancelled(true);
                var3.onClick(var1);
            }
        }

    }
}

