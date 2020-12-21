package me.thejokerdev.skyffa.events;

import me.thejokerdev.skyffa.events.enums.SkySignUpdateCause;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkySignUpdateEvent
        extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private String arena;
    private SkySignUpdateCause cause;

    public SkySignUpdateEvent(String paramString, SkySignUpdateCause paramSkySignUpdateCause) {
        this.arena = paramString;
        this.cause = paramSkySignUpdateCause;
    }


    public String getArena() { return this.arena; }



    public SkySignUpdateCause getCause() { return this.cause; }



    public HandlerList getHandlers() { return handlers; }



    public static HandlerList getHandlerList() { return handlers; }
}