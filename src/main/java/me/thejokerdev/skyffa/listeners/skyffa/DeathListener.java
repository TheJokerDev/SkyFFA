package me.thejokerdev.skyffa.listeners.skyffa;

import me.thejokerdev.other.ActionBar;
import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.events.SkyPlayerDeathEvent;
import me.thejokerdev.skyffa.listeners.DamageListener;
import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DeathListener implements Listener {
    @EventHandler
    public void onSkyPlayerDeath(SkyPlayerDeathEvent var1) {
        SkyPlayer var1x = var1.getPlayer();
        SkyPlayer var2 = var1.getKiller();
        Arena var3 = var1.getGame();
        if (var2 != null) {
            DamageListener.lastDamage.remove(var1x.getUniqueID());
            var3.broadcast(String.format(Utils.ct("&e%s&7 murió a manos de &c%s&7."), var1x.getName(), var2.getName()));
            ActionBar.sendActionBar(var2.getPlayer(), Utils.ct("&e⚔ Mataste a " + var1x.getName() + " +5 puntos ⚔"));
            var2.getPlayer().playSound(var2.getPlayer().getLocation(), Sound.DRINK, 1F, 1F);
            var2.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5, 2, true, false));
            var2.getPlayer().setHealth(var2.getPlayer().getMaxHealth());
        } else {
            var3.broadcast(String.format(Utils.ct("&e%s&7 murió."), var1x.getName()));
        }
        Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
            ArenaListener.checkWinner(var3);
        });
        //Utils.updateTabKill(var3);
    }
}
