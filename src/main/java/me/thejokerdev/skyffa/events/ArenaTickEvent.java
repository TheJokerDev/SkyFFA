package me.thejokerdev.skyffa.events;

import me.thejokerdev.skyffa.arena.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaTickEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Arena arena;


    public ArenaTickEvent(Arena paramArena) { this.arena = paramArena; }



    public Arena getArena() { return this.arena; }



    public HandlerList getHandlers() { return handlers; }



    public static HandlerList getHandlerList() { return handlers; }
}
