package me.thejokerdev.skyffa.player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaBox;
import me.thejokerdev.skyffa.arena.SpectatorReason;
import me.thejokerdev.skyffa.events.SkyPlayerSpectatorEvent;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Iterator;
import java.util.UUID;

public class SkyPlayer extends SkyData{
    private Location arenaSpawn;
    private String name;
    private UUID uniqueID;
    private ItemStack[] inv;
    private ItemStack[] armorContents;
    private boolean spectating;
    private Arena arena;
    private int xplevel;
    private float exp;
    private ArenaBox box;

    public boolean isSpectating() {
        return spectating;
    }

    public void setSpectating(boolean var1, SpectatorReason var2) {
        this.spectating = var1;
        if (var2 != null) {
            SkyPlayerSpectatorEvent var3 = new SkyPlayerSpectatorEvent(this, this.getArena(), var1, var2);
            Bukkit.getServer().getPluginManager().callEvent(var3);
        }

    }
    public void setSpectating(boolean var1){
        spectating = var1;
    }
    public Arena getArena() {
        return this.arena;
    }

    public SkyPlayer(String name, UUID uniqueID){
        this.name = name;
        this.uniqueID = uniqueID;
        this.spectating = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uniqueID) != null ? Bukkit.getPlayer(this.uniqueID) : Bukkit.getPlayer(this.name);
    }

    public void setUniqueID(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    public ItemStack[] getInv() {
        return inv;
    }

    public void setInv(ItemStack[] inv) {
        this.inv = inv;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }

    public void setArmorContents(ItemStack[] armorContents) {
        this.armorContents = armorContents;
    }

    public Location getArenaSpawn() {
        return arenaSpawn;
    }

    public void setArenaSpawn(Location arenaSpawn) {
        this.arenaSpawn = arenaSpawn;
    }

    public void clearInventory(boolean var1) {
        Player var2 = this.getPlayer();
        if (var2 != null) {
            if (Utils.getSigns().getBoolean("options.saveInventory") && var1) {
                this.armorContents = var2.getInventory().getArmorContents();
                this.inv = var2.getInventory().getContents();
                this.xplevel = var2.getLevel();
                this.exp = var2.getExp();
            }

            var2.getInventory().setArmorContents((ItemStack[])null);
            var2.getInventory().clear();
            var2.getInventory().setContents(new ItemStack[0]);
            Iterator var3 = var2.getActivePotionEffects().iterator();

            while(var3.hasNext()) {
                PotionEffect var4 = (PotionEffect)var3.next();
                var2.removePotionEffect(var4.getType());
            }


            var2.setHealth(var2.getMaxHealth());

            var2.setFoodLevel(20);
            var2.setExp(0.0F);
            var2.setLevel(0);
            if (!var1) {
                var2.setFlying(false);
                var2.setAllowFlight(false);
            }
            this.updateInventory();
        }
    }

    public void updateInventory() {
        this.getPlayer().updateInventory();
    }

    public void sendMessage(String var1) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            var1 = PlaceholderAPI.setPlaceholders(this.getPlayer(), var1);
        }

        if (var1 != null && !var1.isEmpty()) {
            Player var2 = this.getPlayer();
            if (var2 != null) {
                var2.sendMessage(ChatColor.translateAlternateColorCodes('&', Utils.ct(var1)));
            }
        }

    }

    public void teleport(Location var1) {
        Player var2 = this.getPlayer();
        if (var2 != null) {
            this.getPlayer().teleport(var1);
        }

    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public void setBox(ArenaBox arenaBox) {
        box = arenaBox;
    }

    public boolean isInArena() {
        return arena !=null;
    }
}
