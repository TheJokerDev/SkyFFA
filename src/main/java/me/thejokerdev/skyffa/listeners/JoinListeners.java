package me.thejokerdev.skyffa.listeners;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.menus.admin.ArenaEdit;
import me.thejokerdev.skyffa.menus.user.MenuArenas;
import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.sign.SignManager;
import me.thejokerdev.skyffa.sign.SkySign;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Iterator;

public class JoinListeners implements Listener {
    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onAsnycPrePlayerLogin(AsyncPlayerPreLoginEvent var1) {
        if (!Main.getPlugin().isStarted) {
            var1.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Utils.ct("&c¡Servidor en inicio!"));
        }

    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if (!Main.firstJoin) {
            Main.firstJoin = true;
            Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), () -> {
                Iterator var0 = SignManager.getSigns().iterator();

                while(var0.hasNext()) {
                    SkySign var3 = (SkySign)var0.next();
                    SignManager.updateSign(var3);
                }

            }, 20L, 6000L);
        }
        Player p = e.getPlayer();
        if (Bukkit.getScoreboardManager().getMainScoreboard() != null && Bukkit.getScoreboardManager().getMainScoreboard() == p.getScoreboard()) {
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        if (p.getScoreboard() == null) {
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        if (Utils.getConfig().getBoolean("options.forceSpawn")){
            if (Main.spawn != null){
                p.teleport(Main.spawn);
            }
        }
        SkyPlayer var3 = new SkyPlayer(p.getName(), p.getUniqueId());
        Main.skyPlayersData.put(p.getName(), var3);
        Main.skyPlayersUUIDData.put(p.getUniqueId(), var3);
        new MenuArenas(p);
    }
}
