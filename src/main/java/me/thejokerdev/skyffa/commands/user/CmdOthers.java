package me.thejokerdev.skyffa.commands.user;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.events.enums.ArenaLeaveCause;
import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdOthers implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            Utils.sendMessage(sender, true, "&c¡Este comando es solo para jugadores!");
            return true;
        }
        Player player = (Player)sender;
        if (args.length == 0) {
            SkyPlayer skyPlayer = Main.getSkyPlayer(player);
            if (skyPlayer == null) {
                return false;
            }
            if (skyPlayer.isInArena()) {
                Arena arena = skyPlayer.getArena();
                if (skyPlayer.isInArena()) {
                    arena.removePlayer(skyPlayer, ArenaLeaveCause.COMMAND);
                    Main.log("CmdOther.onCommand - " + skyPlayer.getName() + " removed using command");
                }
            }
        }
        return true;
    }
}
