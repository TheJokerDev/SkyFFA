package me.thejokerdev.skyffa.menus.user;

import me.thejokerdev.other.SimpleItem;
import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaManager;
import me.thejokerdev.skyffa.arena.ArenaState;
import me.thejokerdev.skyffa.menus.Menu;
import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.utils.FileConfigurationUtil;
import me.thejokerdev.skyffa.utils.ItemsUtil;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MenuArenas extends Menu {

    public static ArrayList<String> openedGames = new ArrayList<>();
    public HashMap<Player, Integer> pageRecord = new HashMap<>();
    public MenuArenas(Player var1) {
        super(var1, "arenas", Utils.getMenuFile("Arenas").getString("title"), 6);
    }

    @Override
    public void onOpen(InventoryOpenEvent var1) {
        openedGames.add(getPlayer().getName());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!openedGames.contains(getPlayer().getName())){
                    this.cancel();
                }
                update();
            }
        }.runTaskTimerAsynchronously(Main.getPlugin(), 0, 20L);
    }

    @Override
    public void onClose(InventoryCloseEvent var1) {
        openedGames.remove(getPlayer().getName());
        if (pageRecord.containsKey(getPlayer())) {
            pageRecord.remove(getPlayer());
        }
    }

    @Override
    public void onClick(InventoryClickEvent var1) {
        Player p = getPlayer();
        ItemStack click = var1.getCurrentItem();
        if (click ==null || click.getType() == Material.AIR){
            return;
        }
        FileConfigurationUtil config =Utils.getMenuFile("Arenas");
        if (config.get("extraItems") == null){
            return;
        }
        List<Integer> slots = new ArrayList<>();
        for (String key : config.getSection("extraItems").getKeys(false)) {
            if (config.get("extraItems") != null) {
                if (config.getString("extraItems." + key + ".slot").contains(",")) {
                    for (String s : config.getString("extraItems." + key + ".slot").split(",")) {
                        int i = Integer.parseInt(s);
                        slots.add(i);
                    }
                } else {
                    slots.add(config.getInt("extraItems."+key+".slot"));
                }
                for (int i2 : slots) {
                    if (var1.getSlot() == i2) {
                        List<String> commands = config.getStringList("extraItems." + key + ".actions");
                        boolean hasCommands = commands != null;
                        if (hasCommands) {
                            for (int i = 0; i < commands.size(); i++) {
                                String string;
                                String line = commands.get(i);
                                if (line.startsWith("[player]")) {
                                    string = line.replace("[player]", "");
                                    string = string.replaceAll("%pos-x%", String.valueOf(p.getLocation().getBlockX()));
                                    string = string.replaceAll("%pos-y%", String.valueOf(p.getLocation().getBlockY()));
                                    string = string.replaceAll("%pos-z%", String.valueOf(p.getLocation().getBlockZ()));
                                    p.chat(string.replaceAll("%player%", p.getName()));
                                } else if (line.startsWith("[console]")) {
                                    string = line.replace("[console]", "");
                                    string = string.replaceAll("%pos-x%", String.valueOf(p.getLocation().getBlockX()));
                                    string = string.replaceAll("%pos-y%", String.valueOf(p.getLocation().getBlockY()));
                                    string = string.replaceAll("%pos-z%", String.valueOf(p.getLocation().getBlockZ()));
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string.replaceFirst("/", "").replaceAll("%player%", p.getName()));
                                } else if (line.equals("[close]")) {
                                    p.closeInventory();
                                } else if (line.startsWith("[message]")) {
                                    string = line.replace("[message]", "");
                                    p.sendMessage(Utils.ct(string).replaceAll("%player%", p.getName()));
                                }
                            }
                        }
                        return;
                    }
                }
                slots.clear();
            }
        }
        SkyPlayer var3 = Main.getSkyPlayer(getPlayer());
        Arena var6;
        String clickName = null;
        if (click.hasItemMeta()) {
            clickName = ChatColor.stripColor(click.getItemMeta().getDisplayName());
            if (clickName.contains(" ")){
                clickName = clickName.split(" ")[1];
            } else if (clickName.contains(" - ")){
                clickName = clickName.split(" ")[1];
            }
        }
        Arena game = ArenaManager.getArena(clickName);
        if (game != null){
            getPlayer().chat("/skyffa join "+game.getName());
        }
    }

    @Override
    public void update() {
        List<Arena> arenas = ArenaManager.getJoinableArenas();
        int page;
        if (!pageRecord.containsKey(getPlayer())){
            page = 0;
            pageRecord.put(getPlayer(), 0);
        } else {
            page = pageRecord.get(getPlayer());
        }
        if (page > 0 && arenas.size() < (page+1)*21){
            update();
            return;
        }
        if (page>0){
            setItem(Utils.getMenuFile("Arenas").getInt("items.previous.slot"), ItemsUtil.createItem(Utils.getMenuFile("Arenas").getSection("items.previous"), getPlayer()));
        }
        if (arenas.size()>(page+1)*21){
            setItem(Utils.getMenuFile("Arenas").getInt("items.next.slot"), ItemsUtil.createItem(Utils.getMenuFile("Arenas").getSection("items.next"), getPlayer()));
        }
        if (arenas.size() > 21){
            arenas = arenas.subList(page*21, Math.min((page*21)+21, arenas.size()));
        }
        int slot = 10;
        if (arenas.size() > 0){
            for (Arena game: arenas){
                if (slot == 17 || slot == 26) {
                    slot = slot + 2;
                }
                setItem(slot, getArenaItem(ArenaManager.getArena(game.getName())));
                slot++;
            }
        } else {
            setItem(Utils.getMenuFile("Arenas").getInt("items.noArenas.slot"), ItemsUtil.createItem(Utils.getMenuFile("Arenas").getSection("items.noArenas"), getPlayer()));
        }
        FileConfigurationUtil config =Utils.getMenuFile("Arenas");
        if (config.get("extraItems") != null){
            for (String key : config.getSection("extraItems").getKeys(false)){
                int slot2 = 0;
                List<Integer> slots = new ArrayList<>();
                if (config.getString("extraItems."+key+".slot").contains(",")){
                    for (String s : config.getString("extraItems."+key+".slot").split(",")){
                        int i = Integer.parseInt(s);
                        slots.add(i);
                    }
                } else {
                    slot2 = config.getInt("extraItems."+key+".slot");
                }
                if (slots.isEmpty()) {
                    setItem(slot2, ItemsUtil.createItem(config.getSection("extraItems." + key), getPlayer()));
                } else {
                    for (int s : slots){
                        setItem(s, ItemsUtil.createItem(config.getSection("extraItems." + key), getPlayer()));
                    }
                }
            }
        }
    }

    public ItemStack getArenaItem(Arena arena){
        SimpleItem item;
        if (arena.getArenaState() == ArenaState.WAITING){
            if (arena.getAlivePlayers() == arena.getMaxPlayers()){
                item = ItemsUtil.createItem(Utils.getMenuFile("Arenas").getSection("items.FULL"), getPlayer());
                item = setArenaPlaceholders(item, arena);
            } else {
                item = ItemsUtil.createItem(Utils.getMenuFile("Arenas").getSection("items." + arena.getArenaState().name()), getPlayer());
                item = setArenaPlaceholders(item, arena);
            }
            return item.build();
        } else {
            item = ItemsUtil.createItem(Utils.getMenuFile("Arenas").getSection("items." + arena.getArenaState().name()), getPlayer());
            item = setArenaPlaceholders(item, arena);
            return item.build();
        }
    }
    public SimpleItem setArenaPlaceholders(SimpleItem item, Arena arena){
        if (item != null){
            item.setDisplayName(item.getDisplayName().replaceAll("%arena%", arena.getDisplayName()));
            for (int i = 0; i<item.getLore().size(); i++){
                item.getLore().set(i, item.getLore().get(i).replaceAll("%arena%", arena.getDisplayName()));
                item.getLore().set(i, item.getLore().get(i).replaceAll("%p-alive%", String.valueOf(arena.getAlivePlayers())));
                item.getLore().set(i, item.getLore().get(i).replaceAll("%p-max%", String.valueOf(arena.getMaxPlayers())));
                item.getLore().set(i, item.getLore().get(i).replaceAll("%p-max%", String.valueOf(arena.getMaxPlayers())));
            }
        }
        return item;
    }

}
