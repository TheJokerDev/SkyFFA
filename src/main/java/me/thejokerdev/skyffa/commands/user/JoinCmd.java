package me.thejokerdev.skyffa.commands.user;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaManager;
import me.thejokerdev.skyffa.arena.ArenaState;
import me.thejokerdev.skyffa.commands.BaseCommand;
import me.thejokerdev.skyffa.events.enums.ArenaJoinCause;
import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JoinCmd implements BaseCommand {
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

        if (!skyPlayer.isInArena()){
            if (args.length == 0){
                Arena var1 = ArenaManager.getJoinableGame();
                var1.addPlayer(skyPlayer, ArenaJoinCause.COMMAND);
                return true;
            }
            if (args.length == 1){
                String var1 = args[0];
                Arena var2 = ArenaManager.getArena(var1);
                if (var2 == null){
                    Utils.sendMessage(p, true, "&c¡La arena "+var1+" no existe!");
                    return true;
                }
                if (var2.getArenaState() == ArenaState.WAITING || var2.getArenaState() == ArenaState.STARTING){
                    var2.addPlayer(skyPlayer, ArenaJoinCause.COMMAND);
                } else {
                    Utils.sendMessage(p, true, "&c¡No puedes entrar ahora a esa arena!");
                }
                return true;
            }
        }
        sendHelp(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1){
            ArrayList<String> arrayList1 = new ArrayList<>();
            for (Arena arena : ArenaManager.getJoinableArenas()){
                arrayList1.add(arena.getName());
            }
            ArrayList arrayList2 = new ArrayList();
            StringUtil.copyPartialMatches(args[0], arrayList1, arrayList2);
            Collections.sort(arrayList2);
            return arrayList2;
        }
        return null;
    }

    @Override
    public String help(CommandSender paramCommandSender) {
        return "&8 - &7/skyffa &ejoin &a - &7Mira más opciones.";
    }

    private void sendHelp(CommandSender sender){
        String str = "&8&m =================&7 [&aSkyFFA &bv." + Main.getPlugin().getDescription().getVersion() + "&7] &8&m================= ";
        Utils.sendMessage(sender, false,
                str," ",
                "&8 - &7/skyffa join &a - &7Entra a una arena random.",
                "&8 - &7/skyffa join &e<nombre>&a - &7Entra a una arena específica."
        );
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
