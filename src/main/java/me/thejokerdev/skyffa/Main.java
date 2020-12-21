package me.thejokerdev.skyffa;

import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaManager;
import me.thejokerdev.skyffa.box.BoxManager;
import me.thejokerdev.skyffa.commands.CMDExecutor;
import me.thejokerdev.skyffa.commands.user.CmdOthers;
import me.thejokerdev.skyffa.events.EventsManager;
import me.thejokerdev.skyffa.language.LanguageManager;
import me.thejokerdev.skyffa.listeners.*;
import me.thejokerdev.skyffa.listeners.skyffa.ArenaListener;
import me.thejokerdev.skyffa.listeners.skyffa.DeathListener;
import me.thejokerdev.skyffa.listeners.skyffa.SpectateListener;
import me.thejokerdev.skyffa.menus.MenuListener;
import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.sign.SignManager;
import me.thejokerdev.skyffa.utils.LocationUtil;
import me.thejokerdev.skyffa.utils.Utils;
import me.thejokerdev.skyffa.utils.sky.SkyScoreboard;
import me.thejokerdev.skyffa.utils.variable.VariableManager;
import me.thejokerdev.skyffa.utils.variable.VariablesDefault;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public final class Main extends JavaPlugin {
    public static String prefix = "&7[&aSkyFFA&7] &r";
    private static Main plugin;
    public boolean isStarted = false;
    public static File getTranslationsFolder;
    public static File getArenasFolder;
    public static File getKitsFolder;
    public static File getMenusFolder;
    public static Location spawn;
    public static HashMap<String, SkyPlayer> skyPlayersData = new HashMap<>();
    public static HashMap<UUID, SkyPlayer> skyPlayersUUIDData = new HashMap<>();
    public static boolean firstJoin;
    public static VariableManager variableManager;


    @Override
    public void onEnable() {
        plugin = this;
        Utils.sendMessage(Utils.getConsole(), true, "&eComprobando complementos...");
        if (!Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")){
            try {
                Utils.sendMessage(Utils.getConsole(), true, "&cEl complemento &7FastAsyncWorldEdit &cno se encontró en los plugins del servidor.");
                Bukkit.getPluginManager().disablePlugin(this);
            } catch (Exception e) {}
            return;
        }
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            try {
                Utils.sendMessage(Utils.getConsole(), true, "&cEl complemento &7PlaceholderAPI &cno se encontró en los plugins del servidor.");
                Bukkit.getPluginManager().disablePlugin(this);
            } catch (Exception e) {}
            return;
        }
        Utils.sendMessage(Utils.getConsole(), true, "&aComplementos verificados. &eIniciando...");
        saveDefaultConfig();

        Utils.sendMessage(Utils.getConsole(), true, "&eCargando comandos, archivos y eventos...");

        if (getConfig().get("spawn") != null){
            spawn = LocationUtil.getLocation(getConfig().getString("spawn"));
        }

        getCommand("skyffa").setExecutor(new CMDExecutor());
        getCommand("skyffa").setTabCompleter(new CMDExecutor());
        getCommand("leave").setExecutor(new CmdOthers());

        getTranslationsFolder = new File(getPlugin().getDataFolder()+"/translations");
        getArenasFolder = new File(getPlugin().getDataFolder()+"/arenas");
        if (getArenasFolder.exists() ? getArenasFolder.canRead(): getArenasFolder.mkdir());
        getKitsFolder = new File(getPlugin().getDataFolder()+"/kits");
        if (getKitsFolder.exists() ? getKitsFolder.canRead(): getKitsFolder.mkdir());
        getMenusFolder = new File(getPlugin().getDataFolder()+"/menus");
        if (getMenusFolder.exists() ? getMenusFolder.canRead(): getMenusFolder.mkdir());
        LanguageManager.loadTranslations(this);
        File signs = new File(getDataFolder(), "signs.yml");
        if (!signs.exists()){
            saveResource("signs.yml", false);
        }
        File boxes = new File(getDataFolder(), "boxes.yml");
        if (!boxes.exists()){
            saveResource("boxes.yml", false);
        }
        File board = new File(getDataFolder(), "board.yml");
        if (!board.exists()){
            saveResource("board.yml", false);
        }
        File arenasFile = new File(getDataFolder()+"/menus", "Arenas.yml");
        if (!arenasFile.exists()){
            saveResource("menus/Arenas.yml", false);
        }
        SignManager.loadSigns();
        BoxManager.initBoxes();
        firstJoin = false;
        variableManager = new VariableManager();
        variableManager.registerVariableReplacer(new VariablesDefault());
        new BukkitRunnable() {
            public void run() {
                Iterator var1 = Bukkit.getOnlinePlayers().iterator();

                while(var1.hasNext()) {
                    Player var2 = (Player)var1.next();
                    SkyPlayer var3 = getSkyPlayer(var2);
                    if (var3 != null) {
                        SkyScoreboard.contentBoard(var3);
                    }
                }

            }
        }.runTaskTimerAsynchronously(this, 0L, 15L);

        listener(new MenuListener(), new JoinListeners(), new InteractListener(), new SignManager()
        , new ArenaListener(), new WorldTabListener(), new WorldListener(), new EventsManager(), new DeathListener()
        , new SpectateListener(), new DamageListener());
        Utils.sendMessage(Utils.getConsole(), true, "&aComandos, archivos y eventos fueron cargados correctamente.");


        Utils.sendMessage(Utils.getConsole(), true, "&eIniciando arenas...");
        ArenaManager.initArenas();
        Utils.sendMessage(Utils.getConsole(), true, "&e"+ArenaManager.getArenas().values().size()+"&a arenas iniciadas.");

        isStarted = true;
    }

    public static void log(String var0) {
        if (isDebug()) {
            System.out.println("[SkyFFA] " + var0);
        }

    }

    public static boolean isDebug() {
        return Utils.getConfig().getBoolean("debug");
    }

    public static SkyPlayer getSkyPlayer(Player var0) {
        if (var0 == null) {
            Utils.sendMessage(Utils.getConsole(), true, "&cTrying to get null player");
            return null;
        } else {
            return skyPlayersUUIDData.getOrDefault(var0.getUniqueId(), skyPlayersData.getOrDefault(var0.getName(), null));
        }
    }

    public static Main getPlugin(){
        return plugin;
    }

    public void listener(Listener... listeners) {
        Arrays.stream(listeners).forEach((l) -> {
            this.getServer().getPluginManager().registerEvents(l, this);
        });
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()){
            p.kickPlayer(Utils.ct("&c¡Reiniciando!"));
        }
        for (Arena arena: ArenaManager.getArenas().values()){
            Bukkit.unloadWorld(arena.getWorld(), false);
            deleteWorld(arena.getWorldFolder());
        }
    }

    public boolean deleteWorld(File path) {
        if(path.exists()) {
            File files[] = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return(path.delete());
    }
}
