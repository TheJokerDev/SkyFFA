package me.thejokerdev.skyffa.events;

import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.player.SkyPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;

public class SkyPlayerDeathEvent
        extends Event {
    private static final HandlerList handlers = new HandlerList();

    private SkyPlayer player;
    private SkyPlayer killer;
    private Arena game;
    private PlayerDeathEvent originalEvent;

    public SkyPlayerDeathEvent(SkyPlayer paramSkyPlayer1, SkyPlayer paramSkyPlayer2, Arena paramArena, PlayerDeathEvent paramPlayerDeathEvent) {
        this.player = paramSkyPlayer1;
        this.killer = paramSkyPlayer2;
        this.game = paramArena;
        this.originalEvent = paramPlayerDeathEvent;
    }


    public SkyPlayer getPlayer() { return this.player; }



    public SkyPlayer getKiller() { return this.killer; }



    public Arena getGame() { return this.game; }



    public PlayerDeathEvent getDeathEvent() { return this.originalEvent; }



    public HandlerList getHandlers() { return handlers; }



    public static HandlerList getHandlerList() { return handlers; }
}
