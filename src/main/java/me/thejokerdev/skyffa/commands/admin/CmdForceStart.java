package me.thejokerdev.skyffa.commands.admin;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.commands.BaseCommand;
import me.thejokerdev.skyffa.language.MSG;
import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdForceStart implements BaseCommand {
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)){
            Utils.sendMessage(sender, true, "&c¡Este comando es solo para jugadores!");
            return true;
        }
        Player p = (Player)sender;
        if (!p.hasPermission(getPermission())){
            Utils.sendMessage(p, false, MSG.NOPERMISSIONS.toString());
            return true;
        }
        SkyPlayer skyPlayer = Main.getSkyPlayer(p);
        if (p == null){
            return true;
        }
        Arena arena = skyPlayer.getArena();
        if (arena != null){
            if (!arena.isForceStarted()){
                arena.setForceStarted(true);
                arena.setCountdown(10);
                Utils.sendMessage(sender, true, "&a¡La partida fue forzada a iniciar!");
            } else {
                Utils.sendMessage(sender, true, "&c¡La partida ya fue forzada a iniciar!");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String help(CommandSender paramCommandSender) {
        return "&8 - &7/skyffa &estart &a - &7Fuerza a iniciar una arena.";
    }

    @Override
    public String getPermission() {
        return "skyffa.command.start";
    }

    @Override
    public boolean console() {
        return false;
    }
}