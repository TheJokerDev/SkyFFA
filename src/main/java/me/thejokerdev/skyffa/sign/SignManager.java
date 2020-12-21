package me.thejokerdev.skyffa.sign;

import com.google.common.collect.Sets;
import me.thejokerdev.other.XMaterial;
import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaManager;
import me.thejokerdev.skyffa.arena.ArenaState;
import me.thejokerdev.skyffa.events.SkySignUpdateEvent;
import me.thejokerdev.skyffa.events.enums.ArenaJoinCause;
import me.thejokerdev.skyffa.events.enums.SkySignUpdateCause;
import me.thejokerdev.skyffa.language.MSG;
import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.utils.ItemsUtil;
import me.thejokerdev.skyffa.utils.LocationUtil;
import me.thejokerdev.skyffa.utils.SignUtils;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SignManager implements Listener {
    private static final HashMap<String, SkySign> locationSign = new HashMap();

    public SignManager() {
    }

    public static void loadSigns() {
        List var0 = Utils.getSigns().getStringList("signs");
        var0.sort(Comparator.comparing(String::length).reversed());
        Iterator var1 = var0.iterator();

        while(var1.hasNext()) {
            String var2 = (String)var1.next();
            String[] var3 = var2.split(";");
            String var4 = var3[0];
            String var5 = null;
            if (var3.length > 1 && var3[1] != null) {
                var5 = var3[1];
            }

            if (var5 == null) {
                var5 = "";
            }

            SkySign var6 = new SkySign(var4);
            initSign(var4, var5, var6);
        }

        Main.log("SignManager#loadSigns - Signs loaded");
    }

    private static List<SkySign> getArenaSigns(String var0) {
        ArrayList var1 = new ArrayList();
        Iterator var2 = locationSign.values().iterator();

        while(var2.hasNext()) {
            SkySign var3 = (SkySign)var2.next();
            if (var3.getGame() != null && var3.getGame().getName().equals(var0)) {
                var1.add(var3);
            }
        }

        return var1;
    }

    private static List<SkySign> getSearchingSigns() {
        ArrayList var0 = new ArrayList();
        Iterator var1 = locationSign.values().iterator();

        while(var1.hasNext()) {
            SkySign var2 = (SkySign)var1.next();
            if (var2.getGame() == null) {
                var0.add(var2);
            }
        }

        return var0;
    }

    private static void initSign(String var0, String var1, SkySign var2) {
        var2.setRotation(Utils.getSigns().getBoolean("rotation"));
        if (var1 != null && !var1.isEmpty()) {
            var2.setGame(var1);
        }

        locationSign.put(var0, var2);
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            Bukkit.getServer().getPluginManager().callEvent(new SkySignUpdateEvent(var1, SkySignUpdateCause.STATE));
        }, 5L);
    }

    public static Set<SkySign> getSigns() {
        return Collections.unmodifiableSet(Sets.newHashSet(locationSign.values()));
    }

    public static SkySign getSign(Location var0) {
        String var1 = LocationUtil.getString(var0, false);
        return locationSign.get(var1);
    }

    private static Arena[] getAvailableGames() {
        ArrayList var0 = new ArrayList();
        Iterator var2 = ArenaManager.getArenas().values().iterator();

        while (true) {
            Arena var3;
            do {
                do {
                    if (!var2.hasNext()) {
                        if (var0.isEmpty()) {
                            return new Arena[]{null};
                        }
                        return (Arena[]) var0.toArray(new Arena[0]);
                    }
                    var3 = (Arena) var2.next();
                } while (var3.getArenaState() != ArenaState.WAITING && var3.getArenaState() != ArenaState.STARTING);
            } while (var3.isFull());

            int var4 = 0;
            Iterator var5 = getArenaSigns(var3.getName()).iterator();

            while (var5.hasNext()) {
                SkySign var6 = (SkySign) var5.next();
                if (var6.isRotation()) {
                    ++var4;
                }
            }

            if (var4 == 0) {
                var0.add(var3);
            }
        }
    }

    public static void updateSign(SkySign var0) {
        if (var0 != null) {
            if (!var0.isUpdating()) {
                var0.setUpdating(true);
                if (var0.isRotation()) {
                    if (var0.getGame() == null) {
                        Arena[] var1 = getAvailableGames();
                        Arena var2 = var1[(new Random()).nextInt(var1.length)];
                        if (var2 == null) {
                            signFormatSearch(var0);
                            var0.setUpdating(false);
                            return;
                        }

                        var0.setGame(var2.getName());
                    }

                    int var4 = 0;
                    Iterator var5 = locationSign.values().iterator();

                    while (var5.hasNext()) {
                        SkySign var3 = (SkySign) var5.next();
                        if (var3 != var0 && var0.getGame() != null && var3.getGame() == var0.getGame()) {
                            ++var4;
                        }
                    }

                    if (var4 >= 1) {
                        signFormatSearch(var0);
                        var0.setUpdating(false);
                    }

                    if (var0.getGame().getArenaState() != ArenaState.ENDING && var0.getGame().getArenaState() != ArenaState.LOADING) {
                        if (var0.getGame().getArenaState() == ArenaState.INGAME) {
                            signFormat(var0);
                            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                                signFormatSearch(var0);
                                var0.setUpdating(false);
                                Bukkit.getServer().getPluginManager().callEvent(new SkySignUpdateEvent("", SkySignUpdateCause.STATE));
                            }, Utils.getSigns().getLong("rotationDelay"));
                        } else if ((var0.getGame().getArenaState() != ArenaState.INGAME || var0.getGame().getArenaState() != ArenaState.ENDING) && var0.getGame().getArenaState() != ArenaState.LOADING) {
                            signFormat(var0);
                            var0.setUpdating(false);
                        } else {
                            var0.setUpdating(false);
                        }
                    } else {
                        signFormatSearch(var0);
                        var0.setUpdating(false);
                    }
                }
            } else {
                if (var0.getGame() != null && (var0.getGame().getArenaState() == ArenaState.LOADING)) {
                    signFormatSearch(var0);
                    var0.setUpdating(false);
                    return;
                }

                signFormat(var0);
                var0.setUpdating(false);
            }

        }
    }

    private static void signFormat(SkySign var0) {
        Arena var1 = var0.getGame();
        Sign var2 = var0.getSign();
        if (var2 != null) {
            org.bukkit.material.Sign var3 = null;
            if (var2.getType() == XMaterial.OAK_WALL_SIGN.parseMaterial()) {
                var3 = (org.bukkit.material.Sign)var2.getData();
            }

            if (var3 == null) {
                Main.log("SignManager.signFormat - must be a wall sign (placed in wall)");
            } else if (var1 == null) {
                signFormatError(var2);
                Main.log("SignManager.signFormat - trying to load a null game");
            } else {
                Block var4 = var2.getBlock().getRelative(var3.getAttachedFace());
                ItemStack var5 = null;
                String var6 = "";
                if (var1.getArenaState() == ArenaState.WAITING) {
                    var6 = MSG.MOTD_WAITING.toString();
                    var5 = ItemsUtil.createShortItem(Utils.getSigns().getString("state.waiting"));
                }

                if (var1.getArenaState() == ArenaState.STARTING) {
                    var6 = MSG.MOTD_STARTING.toString();
                    var5 = ItemsUtil.createShortItem(Utils.getSigns().getString("state.starting"));
                }

                if (var1.isFull()) {
                    var6 = MSG.MOTD_FULL.toString();
                    var5 = ItemsUtil.createShortItem(Utils.getSigns().getString("state.full"));
                }

                if (var1.getArenaState() == ArenaState.INGAME || var1.getArenaState() == ArenaState.ENDING) {
                    var6 = MSG.MOTD_INGAME.toString();
                    var5 = ItemsUtil.createShortItem(Utils.getSigns().getString("state.ingame"));
                }

                List var7 = Utils.getSigns().getStringList("format.game");

                for(int var8 = 0; var8 < var7.size(); ++var8) {
                    SignUtils.setSignText(var2, var8, ChatColor.translateAlternateColorCodes('&', ((String)var7.get(var8)).replace("%map%", var1.getDisplayName()).replace("%state%", var6).replace("%players%", var1.getAlivePlayers() + "").replace("%maxplayers%", var1.getMaxPlayers() + "")));
                }

                ItemStack finalVar = var5;
                Bukkit.getServer().getScheduler().runTaskLater(Main.getPlugin(), () -> {
                    if (Utils.getSigns().getBoolean("change_block") && finalVar != null) {
                        var4.setType(finalVar.getType());
                        var4.setData((byte) finalVar.getDurability());
                    }

                    var2.update();
                }, 5L);
            }
        }
    }

    private static void signFormatSearch(SkySign var0) {
        Sign var1 = var0.getSign();
        if (var1 != null) {
            org.bukkit.material.Sign var2 = null;
            if (var1.getType() == Material.WALL_SIGN) {
                var2 = (org.bukkit.material.Sign)var1.getData();
            }

            if (var2 != null) {
                Block var3 = var1.getBlock().getRelative(var2.getAttachedFace());
                List var4 = Utils.getSigns().getStringList("format.searching");

                for(int var5 = 0; var5 < var4.size(); ++var5) {
                    SignUtils.setSignText(var1, var5, ChatColor.translateAlternateColorCodes('&', (String)var4.get(var5)));
                }

                if (var0.isRotation()) {
                    var0.setGame(null);
                }

                ItemStack var6 = ItemsUtil.createShortItem(Utils.getSigns().getString("state.searching"));
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                    if (Utils.getSigns().getBoolean("change_block") && var6 != null) {
                        var3.setType(var6.getType());
                        var3.setData((byte)var6.getDurability());
                    }

                    var1.update();
                }, 5L);
            }
        }
    }

    private static void signFormatError(Sign var0) {
        ArrayList var1 = new ArrayList();
        var1.add("&lSkyFFA");
        var1.add("&cERROR");
        var1.add("&cNo existe");
        var1.add("&cesa arena");

        for(int var2 = 0; var2 < var1.size(); ++var2) {
            SignUtils.setSignText(var0, var2, ChatColor.translateAlternateColorCodes('&', (String)var1.get(var2)));
        }

        var0.update();
        Material var6 = var0.getBlock().getType();
        org.bukkit.material.Sign var3 = null;
        if (var6 == null) {
            Main.log("SignManager.signFormatError - trying to get a sign but this doesn't exists");
        } else {
            if (var6 == XMaterial.OAK_WALL_SIGN.parseMaterial()) {
                var3 = (org.bukkit.material.Sign)var0.getBlock().getState().getData();
            }

            if (var3 != null) {
                Block var4 = var0.getBlock().getRelative(var3.getAttachedFace());
                Material var5 = Material.BEDROCK;
                var4.setType(var5);
            }
        }
    }

    private void addSign(Location var1, String var2) {
        String var3 = LocationUtil.getString(var1, false);
        SkySign var4 = new SkySign(var3);
        var4.setRotation(Utils.getSigns().getBoolean("rotation"));
        if (var2 != null && !var2.isEmpty()) {
            var4.setGame(var2);
        }

        locationSign.put(var3, var4);
        updateSign(var4);
    }

    private void removeSign(Location var1) {
        String var2 = LocationUtil.getString(var1, false);
        locationSign.remove(var2);
        Bukkit.getServer().getPluginManager().callEvent(new SkySignUpdateEvent("", SkySignUpdateCause.STATE));
    }

    @EventHandler
    public void onPlace(SignChangeEvent var1) {
        Block var2 = var1.getBlock();
        if ((var1.getPlayer().isOp() || var1.getPlayer().hasPermission("skyffa.admin")) && var1.getLine(0).equalsIgnoreCase("[SkyFFA]")) {
            if (var2.getType() == XMaterial.OAK_WALL_SIGN.parseMaterial()) {
                String var3 = var1.getLine(1);
                String var4 = LocationUtil.getString(var1.getBlock().getLocation(), false);
                if (var3 == null) {
                    var3 = "";
                }

                Iterator var5 = locationSign.keySet().iterator();

                while(var5.hasNext()) {
                    String var6 = (String)var5.next();
                    if (var6.equals(var4)) {
                        var1.getPlayer().sendMessage("§cYa había un cartel en esta ubicación");
                        return;
                    }
                }

                List var7 = Utils.getSigns().getStringList("signs");
                var7.add(var4 + ";" + var3);
                Utils.getSigns().set("signs", var7);
                Utils.getSigns().save();
                this.addSign(var1.getBlock().getLocation(), var3);
                var1.getPlayer().sendMessage("§a¡Cartel añadido!");
            } else {
                var1.getPlayer().sendMessage("§c¡Este cartel debe ponerse en una pared!");
            }
        }

    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent var1) {
        if ((var1.getAction() == Action.RIGHT_CLICK_BLOCK || var1.getAction() == Action.RIGHT_CLICK_AIR) && var1.hasBlock() && var1.getClickedBlock().getState() instanceof Sign) {
            Sign var2 = (Sign) var1.getClickedBlock().getState();
            Location var3 = var2.getLocation();
            SkySign var4 = getSign(var3);
            if (var4 == null) {
                return;
            }

            Arena var5 = var4.getGame();
            SkyPlayer var6 = Main.getSkyPlayer(var1.getPlayer());
            if (var6 == null) {
                return;
            }

            if (var5 == null) {
                return;
            }

            if (var5.getArenaState() == null) {
                return;
            }

            if (var5.getArenaState() == ArenaState.INGAME && !var1.getPlayer().hasPermission("skyffa.admin.spectate")) {
                var6.sendMessage(MSG.GAME_INGAME_MESSAGE.toString());
                return;
            }

            if (var5.getArenaState() == ArenaState.ENDING) {
                var6.sendMessage(MSG.GAME_INGAME_MESSAGE.toString());
                return;
            }

            if (var5.getAlivePlayers() >= var5.getMaxPlayers() && !var1.getPlayer().hasPermission("skyffa.admin.spectate")) {
                var6.sendMessage(MSG.GAME_FULL_MESSAGE.toString());
                return;
            }

            if (var5.getArenaState() == ArenaState.LOADING) {
                var6.sendMessage(MSG.GAME_LOADING.toString());
                return;
            }

            Arena var7 = var5;
            var7.addPlayer(var6, ArenaJoinCause.SIGN);
        }

    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent var1) {
        if (var1.getBlock().getType() == Material.WALL_SIGN) {
            Iterator var2 = getSigns().iterator();

            while(true) {
                while(true) {
                    SkySign var3;
                    do {
                        if (!var2.hasNext()) {
                            return;
                        }

                        var3 = (SkySign)var2.next();
                    } while(!var1.getBlock().getLocation().equals(var3.getLocation()));

                    if (!var1.getPlayer().isOp() && !var1.getPlayer().hasPermission("skyffa.admin")) {
                        var1.setCancelled(true);
                    } else {
                        this.removeSign(var3.getLocation());
                        int var4 = 0;
                        List var5 = Utils.getSigns().getStringList("signs");

                        for(Iterator var6 = var5.iterator(); var6.hasNext(); ++var4) {
                            String var7 = (String)var6.next();
                            String var8 = var7.split(";")[0];
                            String var9 = LocationUtil.getString(var3.getLocation(), false);
                            if (var8.equals(var9)) {
                                break;
                            }
                        }

                        var5.remove(var4);
                        Utils.getSigns().set("signs", var5);
                        Utils.getSigns().save();
                        var1.getPlayer().sendMessage("§cCartel eliminado");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSignUpdate(SkySignUpdateEvent var1) {
        Iterator var2;
        SkySign var3;
        if (var1.getArena() != null && !var1.getArena().isEmpty() && !getArenaSigns(var1.getArena()).isEmpty()) {
            var2 = getArenaSigns(var1.getArena()).iterator();

            while(var2.hasNext()) {
                var3 = (SkySign)var2.next();
                updateSign(var3);
            }
        } else {
            var2 = getSearchingSigns().iterator();

            while(var2.hasNext()) {
                var3 = (SkySign)var2.next();
                if (var3.isRotation()) {
                    updateSign(var3);
                    break;
                }
            }
        }

    }
}

