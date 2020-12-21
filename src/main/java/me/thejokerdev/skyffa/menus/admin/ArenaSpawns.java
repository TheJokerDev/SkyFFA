package me.thejokerdev.skyffa.menus.admin;

import me.thejokerdev.other.SimpleItem;
import me.thejokerdev.other.XMaterial;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaManager;
import me.thejokerdev.skyffa.menus.Menu;
import me.thejokerdev.skyffa.menus.MenuListener;
import me.thejokerdev.skyffa.utils.ArraysUtil;
import me.thejokerdev.skyffa.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ArenaSpawns extends Menu {

    public ArenaSpawns(Player p){
        super(p, "arenaEditorSpawns", "Editor de Arenas - Spawnpoints", 6);
    }
    @Override
    public void onOpen(InventoryOpenEvent var1) {
        update();
    }

    @Override
    public void onClose(InventoryCloseEvent var1) {

    }

    @Override
    public void onClick(InventoryClickEvent var1) {
        ItemStack click = var1.getCurrentItem();
        Arena arena = ArraysUtil.arenaEdit.get(getPlayer());
        if (arena == null){
            getPlayer().closeInventory();
            return;
        }
        if (click.isSimilar(getBackItem().build())){
            new ArenaEdit(getPlayer(), arena);
            getPlayer().openInventory(MenuListener.getPlayerMenu(getPlayer(), "arenaEditor").getInventory());
            return;
        }
        if (!click.hasItemMeta() || click.getType() == XMaterial.AIR.parseMaterial()){
            return;
        }
        if (click.hasItemMeta()){
            String name = ChatColor.stripColor(click.getItemMeta().getDisplayName());
            Location loc;
            try {
                loc = LocationUtil.getLocation(name);
            } catch (Exception e) {
                return;
            }
            if (var1.isRightClick()){
                getPlayer().closeInventory();
                getPlayer().teleport(loc);
                return;
            }
            if (var1.isLeftClick()){
                if (arena.getSpawnPoints().size() >= var1.getSlot()) {
                    List var2 = arena.getConfig().getStringList("settings.spawnPoints");
                    var2.remove(var1.getSlot());
                    arena.getConfig().set("settings.spawnPoints", var2);
                    arena.getSpawnPoints().keySet().remove(LocationUtil.getLocation(name));
                    arena.reloadSpawnPoints();
                }
            }
        }
        update();
    }

    @Override
    public void update() {
        getInventory().clear();
        setItem(45, getBackItem());
        Arena arena = ArraysUtil.arenaEdit.get(getPlayer());
        if (arena == null){
            getPlayer().closeInventory();
            return;
        }
        if (arena.getSpawnPoints().size() == 0){
            setItem(22, getNullSpawnsItem());
        } else {
            for (int i = 0; i<arena.getSpawnPoints().size(); i++){
                Location loc = (Location) arena.getSpawnPoints().keySet().toArray()[i];
                addItem(getSpawnLocation(arena, loc));
            }
        }
    }
    private SimpleItem getBackItem(){
        SimpleItem item = new SimpleItem(XMaterial.ARROW);
        item.setDurability(item.getMaterial().getData());
        item.setDisplayName("&6Regresar");
        item.setLore("&7Clic para regresar.");
        return item;
    }

    private SimpleItem getNullSpawnsItem(){
        SimpleItem item = new SimpleItem(XMaterial.RED_STAINED_GLASS_PANE);
        item.setDurability(item.getMaterial().getData());
        item.setDisplayName("&c¡No hay spawns registrados!");
        return item;
    }

    private SimpleItem getSpawnLocation(Arena arena, Location loc){
        SimpleItem item = new SimpleItem(XMaterial.FEATHER);
        item.setDurability(item.getMaterial().getData());
        item.setDisplayName("&7"+LocationUtil.getString(loc, false));
        item.setLore("&7Arena: &r"+arena.getDisplayName(), "", "&eClick izquierdo para eliminar.", "&eClick derecho para ir");
        return item;
    }
}
