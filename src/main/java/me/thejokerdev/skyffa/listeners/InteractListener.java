package me.thejokerdev.skyffa.listeners;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaManager;
import me.thejokerdev.skyffa.arena.ArenaState;
import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.utils.ArraysUtil;
import me.thejokerdev.skyffa.utils.ItemsUtil;
import me.thejokerdev.skyffa.utils.LocationUtil;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InteractListener implements Listener {
    @EventHandler
    public void onClick(PlayerInteractEvent e){
        Player p = e.getPlayer();
        ItemStack click = e.getItem();
        if (ItemsUtil.compareItems(click, ItemsUtil.getSpawnItem())){
            e.setCancelled(true);
            if (e.getClickedBlock() == null){
                Utils.sendMessage(p, true, "&c¡Debes clickear un bloque para esto!");
                return;
            }
            Arena arena = null;
            try {
                arena = ArenaManager.getArena(ArraysUtil.arenaEdit.get(p).getName());
            } catch (Exception exception) {
                Utils.sendMessage(p, true, "&c¡Debes estar editando una arena!");
                return;
            }
            Block b = e.getClickedBlock();
            Location loc = LocationUtil.center(b.getLocation().add(0, 1, 0));
            if (e.getAction() == Action.LEFT_CLICK_BLOCK){
                List var1 = arena.getConfig().getStringList("settings.spawnPoints");
                var1.add(LocationUtil.getString(loc, true));
                arena.getConfig().set("settings.spawnPoints", var1);
                Utils.sendMessage(p, true, "&aLocación añadida (Spawn &e#"+var1.size()+"&a)");
                arena.reloadSpawnPoints();
            } else if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
                arena.setSpectatorSpawn(loc);
                Utils.sendMessage(p, true, "&a¡Locación para espectadores añadida!");
            }
        }
        if (ItemsUtil.compareItems(click, ItemsUtil.getCornersItem())){
            e.setCancelled(true);
            if (e.getClickedBlock() == null){
                Utils.sendMessage(p, true, "&c¡Debes clickear un bloque para esto!");
                return;
            }
            Arena arena;
            try {
                arena = ArenaManager.getArena(ArraysUtil.arenaEdit.get(p).getName());
            } catch (Exception exception) {
                Utils.sendMessage(p, true, "&c¡Debes estar editando una arena!");
                return;
            }
            Block b = e.getClickedBlock();
            Location loc = b.getLocation();
            if (e.getAction() == Action.LEFT_CLICK_BLOCK){
                arena.setHigherLocation(loc);
                Utils.sendMessage(p, true, "&aPunto máximo establecido");
            } else if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
                arena.setLowerLocation(loc);
                Utils.sendMessage(p, true, "&aPunto mínimo establecido");
            }
        }

        SkyPlayer skyPlayer = Main.getSkyPlayer(e.getPlayer());
        if (skyPlayer != null){
            if (skyPlayer.isInArena()){
                Arena arena = skyPlayer.getArena();
                if (arena != null){
                    if (arena.getArenaState() != ArenaState.INGAME){
                        e.setCancelled(true);
                        if (ItemsUtil.compareItems(click, ItemsUtil.getLeaveItem())){
                            p.chat("/leave");
                        }
                    }
                }
            }
        }
    }
}
