package me.thejokerdev.skyffa.events;

import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.SpectatorReason;
import me.thejokerdev.skyffa.player.SkyPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkyPlayerSpectatorEvent
        extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private SkyPlayer player;
    private Arena game;
    private boolean spectate;
    private SpectatorReason reason;

    public SkyPlayerSpectatorEvent(SkyPlayer paramSkyPlayer, Arena paramArena, boolean paramBoolean, SpectatorReason paramSpectatorReason) {
        this.player = paramSkyPlayer;
        this.game = paramArena;
        this.spectate = paramBoolean;
        this.reason = paramSpectatorReason;
    }


    public SkyPlayer getPlayer() { return this.player; }



    public Arena getGame() { return this.game; }



    public boolean getSpectate() { return this.spectate; }



    public SpectatorReason getReason() { return this.reason; }



    public boolean isDeathReason() { return (this.reason == SpectatorReason.DEATH); }



    public boolean isJoinReason() { return (this.reason == SpectatorReason.JOIN); }



    public boolean isLeaveReason() { return (this.reason == SpectatorReason.LEAVE); }



    public HandlerList getHandlers() { return handlers; }



    public static HandlerList getHandlerList() { return handlers; }
}
