package me.thejokerdev.skyffa.listeners;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaState;
import me.thejokerdev.skyffa.player.SkyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
    @EventHandler
    public void onHunger(FoodLevelChangeEvent var1) {
        Player var2 = (Player)var1.getEntity();
        SkyPlayer var3 = Main.getSkyPlayer(var2);
        if (var3 == null) {
            Main.log("PlayerListener.onHunger - null Player");
        } else {
            if (var3.isInArena()) {
                Arena var4 = var3.getArena();
                if (var4.getArenaState() == ArenaState.WAITING || var4.getArenaState() == ArenaState.STARTING || var4.getArenaState() == ArenaState.ENDING) {
                    var1.setCancelled(true);
                }

                if (var3.isSpectating()) {
                    var1.setCancelled(true);
                }
            }

        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent var1) {
        Player var2 = var1.getPlayer();
        SkyPlayer var3 = Main.getSkyPlayer(var2);
        if (var3 == null) {
            Main.log("PlayerListener.onMove - null Player");
        } else {
            if (var3.isInArena() && !var2.isDead()) {
                Arena var4 = var3.getArena();
                if (var4 == null) {
                    return;
                }

                if (var3.getPlayer().getWorld() == Bukkit.getWorlds().get(0)) {
                    var3.teleport(var4.getSpawn());
                }

                if (var4.getArenaState() != ArenaState.INGAME && var4.getArenaState() != ArenaState.ENDING) {

                    if (var2.getWorld().equals(var4.getWorld()) && var2.getLocation().distanceSquared(var3.getArenaSpawn()) >= 2.5D && (var4.getArenaState() == ArenaState.WAITING || var4.getArenaState() == ArenaState.STARTING)) {
                        var3.teleport(var3.getArenaSpawn());
                    }
                } else {
                    if (!var2.getWorld().equals(var4.getWorld())) {
                        var3.teleport(var3.getArenaSpawn());
                    }

                    if (var2.getLocation().getY() <= 0.0D && (var3.isSpectating() || var4.getArenaState() == ArenaState.ENDING)) {
                        var3.teleport(var4.getSpawn());
                    }
                }
            }

        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent var1) {
        Player var2 = var1.getPlayer();
        SkyPlayer var3 = Main.getSkyPlayer(var2);
        if (var3 == null) {
            Main.log("PlayerListener.onDrop - null Player");
        } else {
            if (var3.isInArena()) {
                Arena var4 = var3.getArena();
                if (var4.getArenaState() == ArenaState.WAITING || var4.getArenaState() == ArenaState.STARTING) {
                    var1.setCancelled(true);
                }

                if (var3.isSpectating()) {
                    var1.setCancelled(true);
                }
            }

        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent var1) {
        Player var2 = var1.getPlayer();
        SkyPlayer var3 = Main.getSkyPlayer(var2);
        if (var3 == null) {
            Main.log("PlayerListener.onPickUp - null Player");
        } else {
            if (var3.isInArena()) {
                Arena var4 = var3.getArena();
                if (var4.getArenaState() == ArenaState.WAITING || var4.getArenaState() == ArenaState.STARTING || var4.getArenaState() == ArenaState.ENDING) {
                    var1.setCancelled(true);
                }

                if (var3.isSpectating()) {
                    var1.setCancelled(true);
                }
            }

        }
    }

    @EventHandler
    public void onAnimation(BlockDamageEvent var1) {
        Player var2 = var1.getPlayer();
        SkyPlayer var3 = Main.getSkyPlayer(var2);
        if (var3 == null) {
            Main.log("PlayerListener.onAnimation - null Player");
        } else {
            if (var3.isInArena()) {
                Arena var4 = var3.getArena();
                if (var4.getArenaState() == ArenaState.WAITING || var4.getArenaState() == ArenaState.STARTING) {
                    var1.setCancelled(true);
                }
            }
        }
    }
}
