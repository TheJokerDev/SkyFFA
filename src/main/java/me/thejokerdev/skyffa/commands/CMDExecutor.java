package me.thejokerdev.skyffa.commands;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.commands.admin.CmdArena;
import me.thejokerdev.skyffa.commands.admin.CmdForceStart;
import me.thejokerdev.skyffa.commands.admin.CmdSetSpawn;
import me.thejokerdev.skyffa.commands.admin.CmdTp;
import me.thejokerdev.skyffa.commands.user.CmdArenas;
import me.thejokerdev.skyffa.commands.user.JoinCmd;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.*;

public class CMDExecutor implements CommandExecutor, TabCompleter {
    private final HashMap<String, BaseCommand> commands;

    public CMDExecutor() {
        commands = new HashMap();
        cmd = new ArrayList();


        loadCommands();

        cmd.clear();
        for (String str : this.commands.keySet()) {
            cmd.add(str);
        }
    }
    private final List<String> cmd;
    private void loadCommands() {
        commands.clear();
        commands.put("arena", new CmdArena());
        commands.put("tp", new CmdTp());
        commands.put("setspawn", new CmdSetSpawn());
        commands.put("join", new JoinCmd());
        commands.put("start", new CmdForceStart());
        commands.put("arenas", new CmdArenas());
    }

    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
            if (args == null || args.length < 1) {
                help(sender);
                return true;
            }
            if (args[0].equalsIgnoreCase("help")) {
                help(sender);
                return true;
            }
            String str = args[0];
            Vector vector = new Vector();
            vector.addAll(Arrays.asList(args));
            vector.remove(0);
            args = (String[])vector.toArray(new String[0]);
            if (!commands.containsKey(str)) {
                Utils.sendMessage(sender, true, "¡Este comando no existe!");
                return true;
            }
            try {
                commands.get(str).onCommand(sender, args);
            } catch (Exception exception) {
                exception.printStackTrace();
                Utils.sendMessage(sender, false, "&c¡Ocurrió un error al ejecutar el comando",
                        "&7Usa &e/"+cmd.getName()+" &ahelp&7 para obtener ayuda");
            }
            return true;
    }

    private void help(CommandSender sender) {
        String str = "&8&m =================&7 [&aSkyFFA &bv." + Main.getPlugin().getDescription().getVersion() + "&7] &8&m================= ";
        Utils.sendMessage(sender, false, str, " ");
        for (BaseCommand baseCommand : this.commands.values()) {
            if (!baseCommand.help(sender).isEmpty()) {
                Utils.sendMessage(sender, false, baseCommand.help(sender));
            }
        }
        Utils.sendMessage(sender, false, " ", "&8&m =============================================== ");
    }


    public List<String> onTabComplete(CommandSender sender, Command paramCommand, String paramString, String[] paramArrayOfString) {
            if (!sender.hasPermission("skyffa.admin")){
                return null;
            }
            if (paramArrayOfString.length == 1) {
                ArrayList arrayList = new ArrayList();
                StringUtil.copyPartialMatches(paramArrayOfString[0], cmd, arrayList);
                Collections.sort(arrayList);
                return arrayList;
            }

            if (paramArrayOfString.length >= 2) {
                String str = paramArrayOfString[0];
                Vector vector = new Vector(Arrays.asList(paramArrayOfString));
                vector.remove(0);
                paramArrayOfString = (String[])vector.toArray(new String[0]);

                if (!this.commands.containsKey(str)) {
                    Utils.sendMessage(sender, true, "¡Este comando no existe!");
                    return null;
                }
                List list = commands.get(str).onTabComplete(sender, paramArrayOfString);

                if (list == null) {
                    list = new ArrayList();
                }

                return list;
            }
        return null;
    }
}
