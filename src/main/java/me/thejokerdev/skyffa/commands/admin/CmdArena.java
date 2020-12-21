package me.thejokerdev.skyffa.commands.admin;

import me.thejokerdev.other.ActionBar;
import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaManager;
import me.thejokerdev.skyffa.commands.BaseCommand;
import me.thejokerdev.skyffa.language.MSG;
import me.thejokerdev.skyffa.menus.MenuListener;
import me.thejokerdev.skyffa.menus.admin.ArenaEdit;
import me.thejokerdev.skyffa.menus.admin.ArenaSpawns;
import me.thejokerdev.skyffa.utils.ArraysUtil;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CmdArena implements BaseCommand {
    private int timer;
    private String[] args = new String[]{"create", "edit", "editor", "list"};

    @Override
    public boolean onCommand(CommandSender sender, String[] Args) {
        if (!sender.hasPermission(getPermission())){
            Utils.sendMessage(sender, false, MSG.NOPERMISSIONS.toString());
            return true;
        }
        if (Args.length == 0){
            sendHelp(sender);
            return true;
        }
        if (Args.length > 0){
            String arg = Args[0].toLowerCase();
            switch (arg){
                case "create":{
                    if (Args.length == 2){
                        String str = Args[1];
                        if (arenasList() != null && arenasList().contains(str)){
                            Utils.sendMessage(sender, true, "&cEsa arena ya existe.");
                            return true;
                        }
                        Utils.sendMessage(sender, true, "&eCreando arena...");
                        try {
                            Utils.createCreationWorld((Player) sender, str);
                            new Arena(str, false);
                        } catch (Exception e) {
                            Utils.sendMessage(sender, true, "&cAlgo salió mal al crear la arena: "+str);
                            return true;
                        }
                        Utils.sendMessage(sender, true, "&aArena creada. Usa '/skyffa edit <arena>' para editarla");
                    } else {
                        Utils.sendMessage(sender, true, "&cUsa: /skyffa arena create <nombre>.");
                    }
                    break;
                }
                case "edit":{
                    if (!(sender instanceof Player)){
                        Utils.sendMessage(sender, true, "&c¡Este comando solo se puede llevar a cabo siendo un jugador!");
                        return true;
                    }
                    Player p = (Player)sender;
                    if (Args.length == 2){
                        String str = Args[1];
                        if (arenasList() == null || !arenasList().contains(str)){
                            Utils.sendMessage(p, true, "&cEsa arena no existe.");
                            return true;
                        }
                        if (ArraysUtil.arenaEdit.containsKey(p)){
                            Utils.sendMessage(p, true, "&7Ahora, ya no estás configurando &e"+ArraysUtil.arenaEdit.get(p).getName());
                            MenuListener.getPlayerMenus(p).remove("arenaEditor");
                            MenuListener.getPlayerMenus(p).remove("arenaEditorSpawns");
                            ArraysUtil.arenaEdit.remove(p);
                            ActionBar.clearActionBar(p);
                            return true;
                        }
                        Utils.sendMessage(p, true, "&eCreando menús...");
                        try {
                            ArraysUtil.arenaEdit.put(p, ArenaManager.getArena(str));
                            new ArenaEdit(p, ArenaManager.getArena(str));
                            new ArenaSpawns(p);
                            p.openInventory(MenuListener.getPlayerMenu(p, "arenaEditor").getInventory());
                            runTask(p);
                        } catch (Exception e) {
                            Utils.sendMessage(sender, true, "&cAlgo salió mal al abrir el menú para editar: "+str);
                            return true;
                        }
                    } else {
                        Utils.sendMessage(sender, true, "&cUsa: /skyffa arena edit <nombre>.");
                    }
                    break;
                }
                case "editor":{
                    if (!(sender instanceof Player)){
                        Utils.sendMessage(sender, true, "&c¡Este comando solo se puede llevar a cabo siendo un jugador!");
                        return true;
                    }
                    Player p = (Player)sender;
                    if (Args.length == 1){
                        if (!ArraysUtil.arenaEdit.containsKey(p)){
                            Utils.sendMessage(p, true, "&c¡No estás configurando ninguna arena!");
                            return true;
                        }
                        try {
                            new ArenaEdit(p, ArraysUtil.arenaEdit.get(p));
                            p.openInventory(MenuListener.getPlayerMenu(p, "arenaEditor").getInventory());
                        } catch (Exception e) {
                            Utils.sendMessage(sender, true, "&cAlgo salió mal al abrir el menú para editar");
                            return true;
                        }
                    } else {
                        Utils.sendMessage(sender, true, "&cUsa: /skyffa arena editor.");
                    }
                    break;
                }
                case "list":{
                    String[] list = arenasList().toArray(new String[0]);
                    StringBuilder msg = new StringBuilder();
                    for (int i = 0; i < list.length; i++) {
                        if (i == 0) {
                            msg = new StringBuilder(list[i]);
                        } else {
                            msg.append("&7, &a").append(list[i]);
                        }
                    }
                    Utils.sendMessage(sender, true, "&7Arenas disponibles: "+msg);
                    break;
                }
                default:{
                    sendHelp(sender);
                }
            }
        }
        return true;
    }

    private void runTask(Player p){
        timer = 0;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ArraysUtil.arenaEdit.get(p) == null){
                    timer++;
                    if (timer == 3){
                        cancel();
                    }
                } else {
                    if (!ArraysUtil.inDialog.contains(p)) {
                        ActionBar.sendActionBar(Main.getPlugin(), p, Utils.ct("&7Estás editando la arena &a&l" + ArraysUtil.arenaEdit.get(p).getName()), 20L);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.getPlugin(), 0L, 20L);
    }

    public List<String> arenasList(){
        File folder = Main.getArenasFolder;
        File[] files = folder.listFiles(File::isDirectory);
        List<String> list = new ArrayList<>();
        if (files == null){
            return null;
        } else {
            for (File f : files) {
                list.add(f.getName());
            }
            return list;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender paramCommandSender, String[] paramArrayOfString) {
        if (paramArrayOfString.length == 1){
            ArrayList arrayList2 = new ArrayList();
            StringUtil.copyPartialMatches(paramArrayOfString[0].toLowerCase(), Arrays.asList(args), arrayList2);
            Collections.sort(arrayList2);
            return arrayList2;
        }
        if (paramArrayOfString.length > 1){
            ArrayList arrayList1 = new ArrayList();
            switch (paramArrayOfString[0].toLowerCase()){
                case "edit":{
                    for (Arena arena : ArenaManager.getArenas().values()){
                        arrayList1.add(arena.getName());
                    }
                    break;
                }
                default:{
                    sendHelp(paramCommandSender);
                }
            }
            if (arrayList1.isEmpty()){
                return null;
            }
            ArrayList arrayList2 = new ArrayList();
            StringUtil.copyPartialMatches(paramArrayOfString[1], arrayList1, arrayList2);
            Collections.sort(arrayList2);
            return arrayList2;
        }
        return null;
    }

    private void sendHelp(CommandSender sender){
        String str = "&8&m =================&7 [&aSkyFFA &bv." + Main.getPlugin().getDescription().getVersion() + "&7] &8&m================= ";
        Utils.sendMessage(sender, false,
                str," ",
                "&8 - &7/skyffa arena &ecreate <nombre>&a - &7Crea una nueva arena.",
                "&8 - &7/skyffa arena &eedit <nombre>&a - &7Abre el menú para editar la arena.",
                "&8 - &7/skyffa arena &eeditor&a - &7Abre el editor de la arena.",
                "&8 - &7/skyffa arena &elist&a - &7Obtén una lista de las arenas disponibles.."
        );
    }

    @Override
    public String help(CommandSender paramCommandSender) {
        return "&8 - &7/skyffa &earena &a - &7Muestra más ayuda para este comando.";
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
