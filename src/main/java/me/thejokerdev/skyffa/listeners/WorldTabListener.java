package me.thejokerdev.skyffa.listeners;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Iterator;

public class WorldTabListener implements Listener {
    public WorldTabListener() {
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent var1) {
        Player var2 = var1.getPlayer();
        World var3 = var2.getWorld();
        World var4 = var1.getFrom();
        SkyPlayer var5 = Main.getSkyPlayer(var2);
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            if (var5 != null && var5.isInArena()) {
                Arena var6 = var5.getArena();
                Iterator var7 = var6.getAlivePlayer().iterator();

                while (var7.hasNext()) {
                    SkyPlayer var5x = (SkyPlayer) var7.next();
                    if (var5x != var5) {
                        var5x.getPlayer().hidePlayer(var2);
                        var2.hidePlayer(var5x.getPlayer());
                    }
                }

                Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                    Iterator var13 = var6.getAlivePlayer().iterator();

                    while (var13.hasNext()) {
                        SkyPlayer var14 = (SkyPlayer) var13.next();
                        if (var14 != var5) {
                            var14.getPlayer().showPlayer(var2);
                            var2.showPlayer(var14.getPlayer());
                        }
                    }

                }, 20L);
            } else {
                Iterator var3x = var3.getPlayers().iterator();

                while (var3x.hasNext()) {
                    Player var14 = (Player) var3x.next();
                    if (var14 != var2) {
                        var14.showPlayer(var2);
                        var2.showPlayer(var14);
                    }
                }
            }

        }, 20L);
        Iterator var6 = var4.getPlayers().iterator();

        while (var6.hasNext()) {
            Player var7 = (Player) var6.next();
            if (var7 != var2) {
                if (!Utils.getConfig().getBoolean("options.perWorldTabBypass")) {
                    var7.hidePlayer(var2);
                    var2.hidePlayer(var7);
                } else {
                    if (!var7.hasPermission("skywars.tab.bypass")) {
                        var7.hidePlayer(var2);
                    }

                    if (!var2.hasPermission("skywars.tab.bypass")) {
                        var2.hidePlayer(var7);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent var1) {
        Player var2;
        var2 = var1.getPlayer();
        World var7 = var2.getWorld();
        Iterator var8 = Bukkit.getServer().getOnlinePlayers().iterator();

        while (var8.hasNext()) {
            Player var9 = (Player) var8.next();
            if (var9 != var2) {
                if (var9.getWorld() == var7) {
                    var9.showPlayer(var2);
                    var2.showPlayer(var9);
                } else if (!Utils.getConfig().getBoolean("options.perWorldTabBypass")) {
                    var9.hidePlayer(var2);
                    var2.hidePlayer(var9);
                } else {
                    if (!var9.hasPermission("skywars.tab.bypass")) {
                        var9.hidePlayer(var2);
                    }

                    if (!var2.hasPermission("skywars.tab.bypass")) {
                        var2.hidePlayer(var9);
                    }
                }
            }
        }

    }
}
