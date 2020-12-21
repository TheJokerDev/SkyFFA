package me.thejokerdev.skyffa.commands.admin;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.commands.BaseCommand;
import me.thejokerdev.skyffa.language.MSG;
import me.thejokerdev.skyffa.utils.LocationUtil;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdSetSpawn implements BaseCommand {
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
        Location loc = p.getLocation();
        Main.spawn = loc;
        Utils.getConfig().set("spawn", LocationUtil.getString(loc, false));
        Utils.sendMessage(p, true, "&a¡Spawn establecido en tu locación!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender paramCommandSender, String[] paramArrayOfString) {
        return null;
    }

    @Override
    public String help(CommandSender paramCommandSender) {
        return "&8 - &7/skyffa &esetspawn &a - &7Establece el spawn.";
    }

    @Override
    public String getPermission() {
        return "skyffa.admin";
    }

    @Override
    public boolean console() {
        return false;
    }
}
