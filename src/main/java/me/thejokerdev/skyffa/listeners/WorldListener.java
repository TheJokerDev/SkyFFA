package me.thejokerdev.skyffa.listeners;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaManager;
import me.thejokerdev.skyffa.events.enums.ArenaLeaveCause;
import me.thejokerdev.skyffa.player.SkyPlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.io.Console;
import java.util.Iterator;

public class WorldListener implements Listener {

    public WorldListener() {
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent var1) {
        Iterator var2 = ArenaManager.getArenas().values().iterator();

        while(var2.hasNext()) {
            Arena var3 = (Arena)var2.next();
            if (var3.getWorld() == var1.getWorld() && !Main.getPlugin().getConfig().getBoolean("options.weather")) {
                if (!var1.isCancelled()) {
                    var1.setCancelled(var1.toWeatherState());
                }

                if (var1.getWorld().hasStorm()) {
                    var1.getWorld().setWeatherDuration(0);
                }
            }
        }

    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent var1) {
        Player var2 = var1.getPlayer();
        SkyPlayer var3 = Main.getSkyPlayer(var2);
        if (var3 != null && var3.isSpectating()) {
            Arena var4 = var3.getArena();
            if (var4 != null) {
                var4.removePlayer(var3, ArenaLeaveCause.WORLD_CHANGE);
                var3.setSpectating(false);
            }
        }
    }

    @EventHandler
    public void onSaveWorld(WorldSaveEvent var1) {
        Arena var2 = ArenaManager.getArena(var1.getWorld().getName());
        if (var2 != null) {
            if (!var2.isHardReset()) {
                Main.log(var1.getWorld().getName() + " has forced to hard world reset (due to something is saving the world)");
                var2.setHardReset(true);
            }
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent var1) {
        Iterator var2 = ArenaManager.getArenas().values().iterator();

        while(true) {
            Arena var3;
            do {
                if (!var2.hasNext()) {
                    return;
                }

                var3 = (Arena)var2.next();
            } while(var3.getWorld() != var1.getLocation().getWorld());

            if (var1.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
                var1.setCancelled(true);
            }

           /* if (!SkyWars.getPlugin().getConfig().getBoolean("options.creaturespawn")) {
                var1.setCancelled(true);
            }*/
        }
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onWorldUnload(WorldUnloadEvent var1) {
        if (var1.isCancelled() && ArenaManager.getArena(var1.getWorld().getName()) != null) {
            var1.setCancelled(false);
        }

    }
}