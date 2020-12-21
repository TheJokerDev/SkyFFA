package me.thejokerdev.skyffa.listeners;

import me.thejokerdev.other.ActionBar;
import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaState;
import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DamageListener implements Listener {
    public static HashMap<UUID, UUID> lastDamage = new HashMap();

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent paramEntityDamageEvent) {
        if (paramEntityDamageEvent.getEntity() instanceof Player) {
            Player player = (Player)paramEntityDamageEvent.getEntity();
            SkyPlayer skyPlayer = Main.getSkyPlayer(player);
            if (skyPlayer == null) {
                Main.log("DamageListener.onPlayerDamage - null Player");
                return;
            }
            if (player.getLocation().getWorld().equals(Main.spawn.getWorld()) && !skyPlayer.isInArena()){
                if (paramEntityDamageEvent.getCause() == EntityDamageEvent.DamageCause.VOID){
                    paramEntityDamageEvent.setCancelled(true);
                    player.teleport(Main.spawn);
                }
            }
            if (skyPlayer.isInArena()) {
                Arena arena = skyPlayer.getArena();
                if (arena.getArenaState() == ArenaState.WAITING || arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.ENDING) {
                    paramEntityDamageEvent.setCancelled(true);
                }
                if (paramEntityDamageEvent.getCause() == EntityDamageEvent.DamageCause.FALL &&
                        !arena.isFallDamage() && arena.getArenaState() == ArenaState.INGAME) {
                    paramEntityDamageEvent.setCancelled(true);
                }
                if (arena.getArenaState() == ArenaState.INGAME) {
                    if (paramEntityDamageEvent.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        paramEntityDamageEvent.setDamage(5.0);
                    }
                }

                if (skyPlayer.isSpectating()) {
                    skyPlayer.getPlayer().setFireTicks(0);
                    paramEntityDamageEvent.setCancelled(true);
                }
                if (skyPlayer.isSpectating() || arena.getArenaState() == ArenaState.ENDING){
                    if (paramEntityDamageEvent.getCause() == EntityDamageEvent.DamageCause.VOID){
                        paramEntityDamageEvent.setCancelled(true);
                        skyPlayer.teleport(arena.getSpawn());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent paramEntityDamageByEntityEvent) {
        if (paramEntityDamageByEntityEvent.getEntity() instanceof Player && paramEntityDamageByEntityEvent.getDamager() instanceof Player) {
            Player player1 = (Player)paramEntityDamageByEntityEvent.getEntity();
            Player player2 = (Player)paramEntityDamageByEntityEvent.getDamager();
            SkyPlayer skyPlayer1 = Main.getSkyPlayer(player1);
            SkyPlayer skyPlayer2 = Main.getSkyPlayer(player2);

            if (skyPlayer1 == null) {
                Main.log("DamageListener.onPlayerDamageByPlayer - null Player");
                return;
            }
            if (skyPlayer2 == null) {
                Main.log("DamageListener.onPlayerDamageByPlayer - null Damage Player");
                return;
            }
            if (skyPlayer1.isInArena()) {
                Arena arena = skyPlayer1.getArena();
                if (skyPlayer2.isSpectating()) {
                    paramEntityDamageByEntityEvent.setCancelled(true);
                }
                if (arena.getArenaState() == ArenaState.WAITING || arena.getArenaState()== ArenaState.STARTING || arena.getArenaState() == ArenaState.ENDING) {
                    paramEntityDamageByEntityEvent.setCancelled(true);
                }
                if (arena.getArenaState() == ArenaState.INGAME && !skyPlayer1.isSpectating() && !skyPlayer2.isSpectating()) {
                    lastDamage.put(player1.getUniqueId(), player2.getUniqueId());
                    Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () ->
                            lastDamage.remove(player1.getUniqueId(), player2.getUniqueId()), 20L * 3);
                }
            }
        }
    }
    @EventHandler
    public void arrowDamageEvent(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damagee = event.getEntity();
        if (damager instanceof Arrow) {
            try {
                damager = (Entity)((Arrow)damager).getShooter();
            } catch (Exception exception) {
                Main.log("Could not get shooter");
                event.setDamage(0.0D);
                event.setCancelled(true);
                return;
            }
        } else {
            return;
        }
        if (!(damager instanceof Player) || !(damagee instanceof LivingEntity)) {
            return;
        }
        Player p = (Player)damager;
        List<String> msg2 = Utils.getLanguageFile().getStringList("game.arrowShoot");
        String targetName = (damagee instanceof Player) ? ((Player)damagee).getDisplayName() : damagee.getType().name();
        int newhealth;
        if ((newhealth = (int)(((LivingEntity)damagee).getHealth() - event.getFinalDamage())) < 0) {
            newhealth = 0;
        }
        SkyPlayer skyPlayer = Main.getSkyPlayer((Player) damagee);
        if (skyPlayer.isInArena() && skyPlayer.getArena().getArenaState() != ArenaState.ENDING) {
            String msg = String.format(msg2.get(new Random().nextInt(msg2.size())), targetName, newhealth);
            ActionBar.sendActionBar(Main.getPlugin(), p, Utils.ct(msg), 60);
        }
    }
}
