package me.thejokerdev.skyffa.events;

import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.events.enums.ArenaLeaveCause;
import me.thejokerdev.skyffa.player.SkyPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaLeaveEvent
        extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private SkyPlayer player;
    private Arena game;
    private ArenaLeaveCause cause;

    public ArenaLeaveEvent(SkyPlayer paramSkyPlayer, Arena paramArena, ArenaLeaveCause paramArenaLeaveCause) {
        this.player = paramSkyPlayer;
        this.game = paramArena;
        this.cause = paramArenaLeaveCause;
    }


    public SkyPlayer getPlayer() { return this.player; }



    public Arena getGame() { return this.game; }



    public ArenaLeaveCause getCause() { return this.cause; }



    public HandlerList getHandlers() { return handlers; }



    public static HandlerList getHandlerList() { return handlers; }
}
