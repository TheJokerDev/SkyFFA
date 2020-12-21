package me.thejokerdev.skyffa.listeners.skyffa;

import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.events.SkyPlayerSpectatorEvent;
import me.thejokerdev.skyffa.language.MSG;
import me.thejokerdev.skyffa.player.SkyPlayer;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpectateListener implements Listener {
    @EventHandler
    public void onSkyPlayerSpectator(SkyPlayerSpectatorEvent paramSkyPlayerSpectatorEvent) {
        SkyPlayer skyPlayer = paramSkyPlayerSpectatorEvent.getPlayer();
        Arena arena = paramSkyPlayerSpectatorEvent.getGame();

        if (paramSkyPlayerSpectatorEvent.isLeaveReason() || !paramSkyPlayerSpectatorEvent.getSpectate()) {
            if (skyPlayer.getPlayer().getGameMode() != GameMode.SURVIVAL) {
                skyPlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
            }

            return;
        }
        if (paramSkyPlayerSpectatorEvent.getSpectate() && paramSkyPlayerSpectatorEvent.isDeathReason()) {
            skyPlayer.getPlayer().setGameMode(GameMode.SPECTATOR);
        }

        if (paramSkyPlayerSpectatorEvent.getSpectate() && paramSkyPlayerSpectatorEvent.isJoinReason()) {
            if (skyPlayer.getPlayer().getGameMode() != GameMode.SPECTATOR) {
                skyPlayer.getPlayer().setGameMode(GameMode.SPECTATOR);
                skyPlayer.teleport(skyPlayer.getArenaSpawn());
            }
        }
    }
}
