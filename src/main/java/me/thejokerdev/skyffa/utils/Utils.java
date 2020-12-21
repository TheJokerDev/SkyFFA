package me.thejokerdev.skyffa.utils;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.language.MSG;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static String ct(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String[] ct(String... array) {
        return Arrays.stream(array).map(Utils::ct).toArray(String[]::new);
    }

    public static List<String> ct(List<String> list) {
        return list.stream().map(Utils::ct).collect(Collectors.toList());
    }


    //File Utils
    public static FileConfigurationUtil getConfig(){
        return new FileConfigurationUtil(Main.getPlugin().getDataFolder(), "config.yml");
    }
    public static FileConfigurationUtil getSigns(){
        return new FileConfigurationUtil(Main.getPlugin().getDataFolder(), "signs.yml");
    }
    public static FileConfigurationUtil getLanguageFile(){
        return new FileConfigurationUtil(Main.getTranslationsFolder, getConfig().getString("language")+".yml");
    }

    //Extra Utils
    @SafeVarargs
    public static <T> List<T> toList(T... array) {
        List<T> list = new ArrayList();
        Collections.addAll(list, array);
        return list;
    }
    public static ConsoleCommandSender getConsole(){
        return Bukkit.getConsoleSender();
    }
    public static void sendMessage(CommandSender sender, Boolean prefix, String msg){
        if (sender instanceof Player){
            Player p = (Player)sender;
            if (prefix){
                p.sendMessage(ct(Main.prefix+msg));
            } else {
                p.sendMessage(ct(msg));
            }
        } else {
            if (prefix){
                getConsole().sendMessage(ct(Main.prefix + msg));
            } else {
                getConsole().sendMessage(ct(msg));
            }
        }
    }
    public static void sendMessage(CommandSender sender, Boolean prefix, String... list){
        for (String msg : ct(list)) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (prefix) {
                    p.sendMessage(Main.prefix + Utils.ct(msg));
                } else {
                    p.sendMessage(Utils.ct(msg));
                }
            } else {
                if (prefix) {
                    getConsole().sendMessage(Main.prefix + msg);
                } else {
                    getConsole().sendMessage(msg);
                }
            }
        }
    }

    public static FileConfigurationUtil getMenuFile(String menu){
        return new FileConfigurationUtil(Main.getPlugin().getDataFolder(), "menus/"+menu+".yml");
    }

    public static void createCreationWorld(Player p, String mapName){
        WorldCreator wc = new WorldCreator(mapName);

        wc.type(WorldType.FLAT);
        wc.generatorSettings("2;0;1;");

        wc.createWorld();
        World w = Bukkit.getWorld(mapName);
        if (w != null) {
            w.setSpawnLocation(0, 41, 0);
            w.setTime(6000);
            w.setStorm(false);
            w.setSpawnFlags(false, false);
            if (p != null) {
                p.teleport(LocationUtil.center(w.getSpawnLocation()));
                p.setGameMode(GameMode.CREATIVE);
                p.setFlying(true);
                p.getInventory().clear();
            }
        }
    }
    public static String getArenaDName(Arena arena){
        return arena.getDisplayName();
    }
    public static void sendMessage(CommandSender sender, Boolean prefix, List<String> list){
        for (String msg : ct(list)) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (prefix) {
                    p.sendMessage(Main.prefix + Utils.ct(msg));
                } else {
                    p.sendMessage(Utils.ct(msg));
                }
            } else {
                if (prefix) {
                    getConsole().sendMessage(Main.prefix + msg);
                } else {
                    getConsole().sendMessage(msg);
                }
            }
        }
    }

    public static FileConfigurationUtil getBoxFile() {
        return new FileConfigurationUtil(Main.getPlugin().getDataFolder(), "boxes.yml");
    }
    public static FileConfigurationUtil getBoardFile() {
        return new FileConfigurationUtil(Main.getPlugin().getDataFolder(), "board.yml");
    }

    public static String getTimePlayedInArena(int timeplayed){

        int var4 = timeplayed;
        int var5 = var4 % 86400 % 3600 % 60;
        int var6 = var4 % 86400 % 3600 / 60;
        boolean var9 = true;
        boolean var10 = true;
        if (var5 == 1) {
            var9 = false;
        }

        if (var6 == 1) {
            var10 = false;
        }


        String var13 = var9 ? MSG.VARIABLE_TIMEPLAYED_SECONDS.toString() : MSG.VARIABLE_TIMEPLAYED_SECOND.toString();
        String var14 = String.format(var13, var5);
        String var15 = var10 ? (MSG.VARIABLE_TIMEPLAYED_MINUTES).toString() : MSG.VARIABLE_TIMEPLAYED_MINUTE.toString();
        String var16 = String.format(var15, var6);
        String segundos="%SECONDS%";
        String minutos="%MINUTES%";
        if (var6 == 0) {
            var16 = "";
            minutos = "";
        }
        String var1 = minutos+segundos+".";

        String var21 = (var1).replaceAll("%SECONDS%", var14).replaceAll("%MINUTES%", var16);
        return var21;
    }

    public static boolean isNumeric(String var0) {
        try {
            Integer.parseInt(var0);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }
}
