package me.thejokerdev.skyffa.utils;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaState;
import me.thejokerdev.skyffa.player.SkyPlayer;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class Fireworks {
    private Player player;
    private SkyPlayer p;
    private Arena arena;
    public Fireworks(SkyPlayer p, Arena paramArena) {
        player = p.getPlayer();
        this.p = p;
        arena = paramArena;
        display();
    }

    public void display() {
        (new BukkitRunnable() {
            double time = 7.0D;

            public void run() {
                if (!p.isInArena()){
                    cancel();
                }
                if (p.getArena().getArenaState() != ArenaState.ENDING || p.getArena() != arena){
                    cancel();
                }
                if (this.time <= 0.0D) {
                    cancel();
                } else {
                    if (p.isInArena() && p.getArena() == arena) {
                        Fireworks.this.spawnRandom(player.getLocation());
                    } else {
                        cancel();
                    }
                    this.time--;
                }
            }
        }).runTaskTimer(Main.getPlugin(), 0L, 10L);
    }

    public Firework spawnRandom(Location paramLocation) {
        Firework firework = paramLocation.getWorld().spawn(paramLocation, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        switch (ThreadLocalRandom.current().nextInt(4)) {
            default:
                FireworkEffect.Type type = FireworkEffect.Type.BALL;
                fireworkMeta.addEffects(new FireworkEffect[] { FireworkEffect.builder().withColor(getRandomColor()).with(type).flicker(threadLocalRandom.nextBoolean()).trail(threadLocalRandom.nextBoolean()).withFade(getRandomColor()).build() });
                fireworkMeta.setPower(1);
                firework.setFireworkMeta(fireworkMeta);
                return firework;
            case 2:
                type = FireworkEffect.Type.BALL_LARGE;
                fireworkMeta.addEffects(new FireworkEffect[] { FireworkEffect.builder().withColor(getRandomColor()).with(type).flicker(threadLocalRandom.nextBoolean()).trail(threadLocalRandom.nextBoolean()).withFade(getRandomColor()).build() });
                fireworkMeta.setPower(1);
                firework.setFireworkMeta(fireworkMeta);
                return firework;
            case 3:
                type = FireworkEffect.Type.BURST;
                fireworkMeta.addEffects(new FireworkEffect[] { FireworkEffect.builder().withColor(getRandomColor()).with(type).flicker(threadLocalRandom.nextBoolean()).trail(threadLocalRandom.nextBoolean()).withFade(getRandomColor()).build() });
                fireworkMeta.setPower(1);
                firework.setFireworkMeta(fireworkMeta);
                return firework;
            case 4:
                break;
        }
        FireworkEffect.Type type = FireworkEffect.Type.STAR;
        fireworkMeta.addEffects(new FireworkEffect[] { FireworkEffect.builder().withColor(getRandomColor()).with(type).flicker(threadLocalRandom.nextBoolean()).trail(threadLocalRandom.nextBoolean()).withFade(getRandomColor()).build() });
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
        return firework;
    }

    public Color getRandomColor() {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        return Color.fromBGR(threadLocalRandom.nextInt(255), threadLocalRandom.nextInt(255), threadLocalRandom.nextInt(255));
    }
}