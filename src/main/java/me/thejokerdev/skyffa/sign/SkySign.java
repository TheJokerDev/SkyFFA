package me.thejokerdev.skyffa.sign;

import me.thejokerdev.other.XMaterial;
import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaManager;
import me.thejokerdev.skyffa.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class SkySign {
    private final String location;
    private boolean rotation;
    private boolean updating;
    private Arena game;

    public SkySign(String var1) {
        this.location = var1;
        this.rotation = false;
    }

    public Sign getSign() {
        Location var1 = LocationUtil.getLocation(this.location);
        if (var1 == null) {
            Main.log("Trying to use null location for ArenaSign: " + this.location);
            return null;
        } else if (var1.getWorld() == null) {
            Main.log("Trying to use null world location for ArenaSign: " + this.location);
            return null;
        } else {
            Block var2 = var1.getWorld().getBlockAt(var1);
            return var2 == null || var2.getType() != XMaterial.OAK_WALL_SIGN.parseMaterial() && var2.getType() != XMaterial.OAK_SIGN.parseMaterial() ? null : (Sign)var2.getState();
        }
    }

    public Location getLocation() {
        return LocationUtil.getLocation(this.location);
    }

    public Arena getGame() {
        return this.game;
    }

    public void setGame(String var1) {
            this.game = ArenaManager.getArena(var1);
    }

    public boolean isRotation() {
        return this.rotation;
    }

    public void setRotation(boolean var1) {
        this.rotation = var1;
    }

    public boolean isUpdating() {
        return this.updating;
    }

    public void setUpdating(boolean var1) {
        this.updating = var1;
    }
}
