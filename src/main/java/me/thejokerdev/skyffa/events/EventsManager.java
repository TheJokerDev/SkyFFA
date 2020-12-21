package me.thejokerdev.skyffa.events;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.SpectatorReason;
import me.thejokerdev.skyffa.player.SkyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EventsManager implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent paramPlayerDeathEvent) {
        SkyPlayer skyPlayer2;
        Player player1 = paramPlayerDeathEvent.getEntity();
        SkyPlayer skyPlayer1 = Main.getSkyPlayer(player1);

        Player player2 = paramPlayerDeathEvent.getEntity().getKiller();

        if (player2 == null) {
            skyPlayer2 = null;
        } else {
            skyPlayer2 = Main.getSkyPlayer(player2);
        }
        if (skyPlayer1 == null) {
            return;
        }
        if (skyPlayer1.isInArena()) {
            Arena arena = skyPlayer1.getArena();
            paramPlayerDeathEvent.setDeathMessage(null);
            skyPlayer1.setSpectating(true, SpectatorReason.DEATH);
            player1.setHealth(player1.getMaxHealth());
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player1.spigot().respawn(), 1L);
            SkyPlayerDeathEvent skyPlayerDeathEvent = new SkyPlayerDeathEvent(skyPlayer1, skyPlayer2, arena, paramPlayerDeathEvent);
            Bukkit.getServer().getPluginManager().callEvent(skyPlayerDeathEvent);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent paramPlayerRespawnEvent) {
        Player player = paramPlayerRespawnEvent.getPlayer();
        SkyPlayer skyPlayer = Main.getSkyPlayer(player);
        if (skyPlayer == null) {
            return;
        }
        if (skyPlayer.isInArena()) {
            paramPlayerRespawnEvent.setRespawnLocation(skyPlayer.getArenaSpawn());
        } else {
            paramPlayerRespawnEvent.setRespawnLocation(Main.spawn);
        }
    }
}