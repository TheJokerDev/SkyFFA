package me.thejokerdev.skyffa.events;

import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.player.SkyPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaFinishEvent extends Event {
    public ArenaFinishEvent(Arena paramArena, SkyPlayer paramSkyPlayer) { this.arena = paramArena; this.winner = paramSkyPlayer; }
    private final Arena arena;
    private final SkyPlayer winner;
    private static final HandlerList handlerList = new HandlerList();
    public Arena getArena() { return this.arena; }
    public SkyPlayer getWinner() { return this.winner; }


    public static HandlerList getHandlerList() { return handlerList; }




    public HandlerList getHandlers() { return handlerList; }
}
