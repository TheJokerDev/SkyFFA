package me.thejokerdev.skyffa.commands.user;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.commands.BaseCommand;
import me.thejokerdev.skyffa.menus.MenuListener;
import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdArenas implements BaseCommand {
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)){
            Utils.sendMessage(sender, true, "&c¡Este comando es solo para jugadores!");
            return true;
        }
        Player p = (Player)sender;
        SkyPlayer skyPlayer = Main.getSkyPlayer(p);
        if (skyPlayer == null){
            return true;
        }

        if (!skyPlayer.isInArena()) {
            if (args.length == 0){
                p.openInventory(MenuListener.getPlayerMenu(p, "arenas").getInventory());
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender paramCommandSender, String[] paramArrayOfString) {
        return null;
    }

    @Override
    public String help(CommandSender paramCommandSender) {
        return "&8 - &7/skyffa &earenas &a - &7Abre el menú de arenas.";
    }

    @Override
    public String getPermission() {
        return "skyffa.user";
    }

    @Override
    public boolean console() {
        return false;
    }
}
