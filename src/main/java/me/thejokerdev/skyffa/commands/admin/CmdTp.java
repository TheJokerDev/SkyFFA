package me.thejokerdev.skyffa.commands.admin;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.commands.BaseCommand;
import me.thejokerdev.skyffa.language.MSG;
import me.thejokerdev.skyffa.utils.LocationUtil;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CmdTp implements BaseCommand {
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getPermission())){
            Utils.sendMessage(sender, false, MSG.NOPERMISSIONS.toString());
            return true;
        }
        if (!(sender instanceof Player)){
            Utils.sendMessage(sender, true, "&c¡Este comando es solo para jugadores!");
            return true;
        }
        Player p = (Player)sender;
        if (args.length != 1){
            help(p);
            return true;
        }
        String arg = args[0];
        if (arg.equalsIgnoreCase("lobby")){
            if (Main.spawn != null) {
                p.teleport(Main.spawn);
            }
            return true;
        }
        World w;
        try {
            w = Bukkit.getWorld(arg);
        } catch (Exception e) {
            Utils.sendMessage(p, true, "&c¡Ese mundo no existe!");
            return true;
        }
        p.teleport(LocationUtil.center(w.getSpawnLocation()));
        p.setAllowFlight(true);
        p.setFlying(true);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender paramCommandSender, String[] paramArrayOfString) {
        if (paramArrayOfString.length == 1){
            ArrayList arrayList1 = new ArrayList();
            for (String s : arenasList()){
                arrayList1.add(s);
            }
            ArrayList arrayList2 = new ArrayList();
            StringUtil.copyPartialMatches(paramArrayOfString[0], arrayList1, arrayList2);
            Collections.sort(arrayList2);
            return arrayList2;
        }
        return null;
    }

    @Override
    public String help(CommandSender paramCommandSender) {
        return "&8 - &7/skyffa &etp &a - &7Muévete a un mundo.";
    }

    @Override
    public String getPermission() {
        return "skyffa.admin";
    }

    @Override
    public boolean console() {
        return false;
    }

    public List<String> arenasList(){
        File folder = Main.getArenasFolder;
        File[] files = folder.listFiles(File::isDirectory);
        List<String> list = new ArrayList<>();
        if (Main.spawn != null){
            list.add("lobby");
        }
        if (files == null){
            return null;
        } else {
            for (File f : files) {
                list.add(f.getName());
            }
            return list;
        }
    }
}
