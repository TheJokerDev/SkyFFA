package me.thejokerdev.skyffa.events;

import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.events.enums.ArenaJoinCause;
import me.thejokerdev.skyffa.player.SkyPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaJoinEvent
        extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private SkyPlayer player;
    private Arena game;
    private ArenaJoinCause cause;

    public ArenaJoinEvent(SkyPlayer paramSkyPlayer, Arena paramArena, ArenaJoinCause paramArenaJoinCause) {
        this.player = paramSkyPlayer;
        this.game = paramArena;
        this.cause = paramArenaJoinCause;
    }


    public SkyPlayer getPlayer() { return this.player; }



    public Arena getGame() { return this.game; }



    public ArenaJoinCause getCause() { return this.cause; }



    public HandlerList getHandlers() { return handlers; }



    public static HandlerList getHandlerList() { return handlers; }
}

