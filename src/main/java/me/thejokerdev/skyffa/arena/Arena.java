package me.thejokerdev.skyffa.arena;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.thejokerdev.other.Titles;
import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.box.BoxManager;
import me.thejokerdev.skyffa.events.*;
import me.thejokerdev.skyffa.events.enums.ArenaJoinCause;
import me.thejokerdev.skyffa.events.enums.ArenaLeaveCause;
import me.thejokerdev.skyffa.events.enums.SkySignUpdateCause;
import me.thejokerdev.skyffa.language.MSG;
import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.utils.FileConfigurationUtil;
import me.thejokerdev.skyffa.utils.Fireworks;
import me.thejokerdev.skyffa.utils.LocationUtil;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Arena {
    private String name;
    private String displayName;
    private final File folder;
    private final FileConfigurationUtil config;
    private String schematic;
    private final File schematicFolder;
    private final LinkedHashMap<Location, Boolean> spawnPoints = new LinkedHashMap();
    private int minPlayers;
    private int maxPlayers;
    private int countdown;
    private int startCountdown;
    private boolean isFallDamage;
    private boolean isForceStarted;

    public boolean isForceStarted() {
        return isForceStarted;
    }

    public void setForceStarted(boolean forceStarted) {
        isForceStarted = forceStarted;
    }

    public boolean isFallDamage() {
        return isFallDamage;
    }

    public void setFallDamage(boolean fallDamage) {
        isFallDamage = fallDamage;
    }

    public void setEndCountdown(int endCountdown) {
        this.endCountdown = endCountdown;
    }

    private int endCountdown;
    private String broadcastCountdown;
    private ArenaState arenaState;
    private final HashMap<SkyPlayer, Integer> killStreak = new HashMap();
    private boolean hardReset;
    private Location spectatorSpawn;
    private Location higherLocation = null;
    private Location lowerLocation = null;
    private File worldFolder;

    public Location getHigherLocation() {
        return higherLocation;
    }

    public void setHigherLocation(Location higherLocation) {
        this.higherLocation = higherLocation;
    }

    public Location getLowerLocation() {
        return lowerLocation;
    }

    public void setLowerLocation(Location lowerLocation) {
        this.lowerLocation = lowerLocation;
    }

    public List<SkyPlayer> getPlayers() {
        return players;
    }

    private final List<SkyPlayer> players = new ArrayList<>();
    private final List<BukkitRunnable> tickers = new ArrayList();
    private BukkitRunnable ticks;
    private boolean disabled;
    private int alivePlayers;
    private final List<ArenaBox> glassBoxes = new ArrayList();

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setSchematic(String schematic) {
        this.schematic = schematic;
    }

    public LinkedHashMap<Location, Boolean> getSpawnPoints() {
        return spawnPoints;
    }

    public final void loadSpawnPoints() {
        this.spawnPoints.clear();
        for (Object object : config.getList("settings.spawnPoints")) {
            spawnPoints.put(LocationUtil.getLocation(object.toString()), false);
        }
    }

    public List<SkyPlayer> getAlivePlayer() {
        ArrayList arrayList = new ArrayList();
        for (SkyPlayer skyPlayer : this.players) {
            if (!skyPlayer.isSpectating()) {
                arrayList.add(skyPlayer);
            }
        }
        return arrayList;
    }

    public boolean isHardReset() { return this.hardReset; }

    public void setHardReset(boolean paramBoolean) { this.hardReset = paramBoolean; }

    public int getAlivePlayers() {
        byte b = 0;
        for (SkyPlayer skyPlayer : this.players) {
            if (skyPlayer.isSpectating()) {
                continue;
            }
            Player player = skyPlayer.getPlayer();
            if (player == null) {
                continue;
            }
            if ( player.getGameMode() == GameMode.SPECTATOR) {
                skyPlayer.setSpectating(true, SpectatorReason.DEATH);
                continue;
            }
            b++;
        }
        return b;
    }

    public final void loadGlassBoxes() {
        this.glassBoxes.clear();
        for (Location location : this.spawnPoints.keySet()) {
            ArenaBox arenaBox = new ArenaBox(location);
            arenaBox.setBox(BoxManager.getDefaultBox().getName());
            this.glassBoxes.add(arenaBox);
        }
    }
    public final void removeGlassBoxes() {
        for (Location location : this.spawnPoints.keySet()) {
            ArenaBox arenaBox = new ArenaBox(location);
            arenaBox.removeAll(BoxManager.getDefaultBox().getName());
        }
    }

    public void setUsed(Location paramLocation, boolean paramBoolean) {
        spawnPoints.put(paramLocation, Boolean.valueOf(paramBoolean));
    }

    public Location getSpectatorSpawn() {
        return spectatorSpawn;
    }

    public void setSpectatorSpawn(Location spectatorSpawn) {
        this.spectatorSpawn = spectatorSpawn;
        config.set("settings.spectatorSpawn", LocationUtil.getString(spectatorSpawn, true));
    }

    public Location getSpawnPoint() {
        if (Utils.getConfig().getBoolean("options.orderedSpawnPoints")) {
            for (Location location : this.spawnPoints.keySet()) {
                if (!this.spawnPoints.get(location).booleanValue()) {
                    return location;
                }
            }
        } else {
            ArrayList arrayList = new ArrayList(this.spawnPoints.keySet());
            Collections.shuffle(arrayList);
            for (Object location : arrayList) {
                if (!this.spawnPoints.get(location).booleanValue()) {
                    return (Location)location;
                }
            }
        }
        return null;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        getConfig().set("general.minPlayers", this.minPlayers);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        getConfig().set("general.maxPlayers", this.maxPlayers);
    }

    public boolean hasSpectSpawn() {
        return this.config.getString("settings.spectatorSpawn") != null || !this.config.getString("settings.spectatorSpawn").isEmpty();
    }

    public boolean isFull() {
        return getAlivePlayers() >= this.maxPlayers;
    }

    public void addPlayer(SkyPlayer paramSkyPlayer, ArenaJoinCause paramArenaJoinCause) {
        if (null == paramSkyPlayer) {
            Main.log(String.format("Arena.addPlayer - Trying to add a NULL Player"));
            return;
        }
        if (this.disabled) {
            if (paramSkyPlayer.getPlayer().hasPermission("skyffa.admin")) {
                paramSkyPlayer.teleport(getSpawn());
            } else {
                paramSkyPlayer.sendMessage("&cYou don't have permissions to enter to edit this game");
            }
            return;
        }
        if (arenaState == ArenaState.LOADING) {
            Main.log(String.format("Arena.addPlayer - Trying to join Player when game is Reloading"));
            paramSkyPlayer.sendMessage(MSG.GAME_LOADING.toString());

            return;
        }
        if (!paramSkyPlayer.getPlayer().hasPermission("skyffa.admin.spectate")) {
            if (this.arenaState == ArenaState.INGAME) {
                paramSkyPlayer.sendMessage(MSG.GAME_INGAME_MESSAGE.toString());
                return;
            }
            if (getAlivePlayers() >= this.maxPlayers) {
                paramSkyPlayer.sendMessage(MSG.GAME_FULL_MESSAGE.toString());
                return;
            }
        }
        ArenaJoinEvent arenaJoinEvent = new ArenaJoinEvent(paramSkyPlayer, this, paramArenaJoinCause);
        Bukkit.getServer().getPluginManager().callEvent(arenaJoinEvent);
        playSignUpdate(SkySignUpdateCause.PLAYERS);
    }

    public void removePlayer(SkyPlayer paramSkyPlayer, ArenaLeaveCause paramArenaLeaveCause) {
        ArenaLeaveEvent arenaLeaveEvent = new ArenaLeaveEvent(paramSkyPlayer, this, paramArenaLeaveCause);
        Bukkit.getServer().getPluginManager().callEvent(arenaLeaveEvent);
        if (paramArenaLeaveCause != ArenaLeaveCause.RESTART) {
            playSignUpdate(SkySignUpdateCause.PLAYERS);
        }
    }

    private void playSignUpdate(SkySignUpdateCause paramSkySignUpdateCause) { Bukkit.getServer().getPluginManager().callEvent(new SkySignUpdateEvent(this.name, paramSkySignUpdateCause)); }

    public final Location getSpawn() {
        Location location = null;
        if (hasSpectSpawn()) {
            location = spectatorSpawn;
        } else {
            try {
                throw new IllegalAccessException("Spectator spawn from (" + getName() + ") hasn't been found");
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
        return (location != null) ? location : getWorld().getSpawnLocation();
    }

    public final World getWorld() { return Bukkit.getWorld(this.name); }

    public void broadcast(String paramString) {
        for (SkyPlayer skyPlayer : this.players) {
            if (skyPlayer.getPlayer() == null || !skyPlayer.getPlayer().isOnline()) {
                continue;
            }
            skyPlayer.sendMessage(Utils.ct(paramString));
        }
    }
    public void addTimer(BukkitRunnable paramBukkitRunnable, long paramLong1, long paramLong2) {
        this.tickers.add(paramBukkitRunnable);
        paramBukkitRunnable.runTaskTimer(Main.getPlugin(), paramLong1, paramLong2);
    }


    public List<BukkitRunnable> getTimers() { return this.tickers; }



    public BukkitRunnable getTicks() { return this.ticks; }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public int getStartCountdown() {
        return startCountdown;
    }

    public void setStartCountdown(int startCountdown) {
        this.startCountdown = startCountdown;
    }

    public String getBroadcastCountdown() {
        return broadcastCountdown;
    }

    public void setBroadcastCountdown(String broadcastCountdown) {
        this.broadcastCountdown = broadcastCountdown;
    }

    public ArenaState getArenaState() {
        return arenaState;
    }

    public void setArenaState(ArenaState arenaState) {
        this.arenaState = arenaState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEndCountdown() {
        return endCountdown;
    }

    public File getWorldFolder() {
        return worldFolder;
    }

    public Arena(String name, boolean bool) {
        this.name = name;
        this.arenaState = ArenaState.LOADING;
        setForceStarted(false);
        folder = new File(Main.getArenasFolder + "/" + name);
        if (!folder.exists()) {
            folder.mkdir();
        }
        setupMap();
        worldFolder = getWorld().getWorldFolder();
        schematicFolder = new File(folder + "/" + "schematic");
        if (!schematicFolder.exists()) {
            schematicFolder.mkdir();
        }
        if (schematicFolder.listFiles().length == 0) {
            Utils.sendMessage(Utils.getConsole(), false, "&c&lATENCIÓN: &7La carpeta de esquemáticas está vacía.");
        } else{
            for (File file : schematicFolder.listFiles()){
                if (file.getName().contains(".schematic")){
                    schematic = file.getName().replaceAll(".schematic", "");
                }
            }
        }

        config = new FileConfigurationUtil(folder, "config.yml");
        setupConfig();
        spectatorSpawn = LocationUtil.getLocation(config.getString("settings.spectatorSpawn"));
        if (schematic != null) {
            pasteSchematic(getWorld().getSpawnLocation());
        }

        displayName = config.getString("general.displayName");
        if (config.get("settings.spawnPoints") != null) {
            loadSpawnPoints();
            if (bool){loadGlassBoxes();} else {
                removeGlassBoxes();
            }
        }
        minPlayers = config.getInt("general.minPlayers");
        maxPlayers = config.getInt("general.maxPlayers");
        countdown = config.getInt("general.countdown");
        startCountdown = config.getInt("general.startCountdown");
        endCountdown = config.getInt("general.endCountdown");
        broadcastCountdown = config.getString("general.broadcastCountdown");



        if (bool) {
            arenaState = ArenaState.WAITING;
        }
        startTicks();
        ArenaManager.arenas.put(name, this);
        playSignUpdate(SkySignUpdateCause.ALL);
    }

    public void restart() {
        for (BukkitRunnable bukkitRunnable : this.tickers) {
            bukkitRunnable.cancel();
        }
        setForceStarted(false);
        this.tickers.clear();
        this.arenaState = ArenaState.WAITING;

        for (Player player : getWorld().getPlayers()) {
            SkyPlayer skyPlayer = Main.getSkyPlayer(player);
            skyPlayer.teleport(Main.spawn);
            skyPlayer.getPlayer().setFallDistance(0.0F);
        }

        this.players.clear();
        this.glassBoxes.clear();
        this.killStreak.clear();
        setupConfig();
        spectatorSpawn = LocationUtil.getLocation(config.getString("settings.spectatorSpawn"));
        if (schematic != null) {
            pasteSchematic(getWorld().getSpawnLocation());
        }

        displayName = config.getString("general.displayName");
        if (config.get("settings.spawnPoints") != null) {
            loadSpawnPoints();
            loadGlassBoxes();
        }
        minPlayers = config.getInt("general.minPlayers");
        maxPlayers = config.getInt("general.maxPlayers");
        countdown = config.getInt("general.countdown");
        startCountdown = config.getInt("general.startCountdown");
        endCountdown = config.getInt("general.endCountdown");
        broadcastCountdown = config.getString("general.broadcastCountdown");
        pasteSchematic(getWorld().getSpawnLocation());

        playSignUpdate(SkySignUpdateCause.ALL);
    }

    public void pasteSchematic(Location loc) {
        Vector v = new Vector(loc.getX(), loc.getY(), loc.getZ());
        File file = new File(schematicFolder + File.separator + getName() + ".schematic");
        if (!file.exists()) {
            return;
        }
        BukkitWorld w = new BukkitWorld(getWorld());
        try {
            ClipboardFormat.SCHEMATIC.load(file).paste(w, v, false, true, null);
        } catch (IOException e1) {
        }
    }

    public void saveSchematic(Location pos1, Location pos2) {
        File file = new File(schematicFolder + File.separator + getName() + ".schematic");
        Vector bot = new Vector(pos1.getX(), pos1.getY(), pos1.getZ());
        Vector top = new Vector(pos2.getX(), pos2.getY(), pos2.getZ());
        CuboidRegion region = new CuboidRegion(new BukkitWorld(pos1.getWorld()), bot, top);
        Schematic schem = new Schematic(region);
        try {
            schem.save(file, ClipboardFormat.SCHEMATIC);
        } catch (IOException e) {
        }
    }
    public void saveSchematic(Vector bot, Vector top) {
        File file = new File(schematicFolder + File.separator + getName() + ".schematic");
        CuboidRegion region = new CuboidRegion(new BukkitWorld(getWorld()), bot, top);
        Schematic schem = new Schematic(region);
        try {
            schem.save(file, ClipboardFormat.SCHEMATIC);
        } catch (IOException e) {
        }
    }

    private void setupConfig(){
        config.add("general.displayName", name);
        config.add("general.minPlayers", 2);
        config.add("general.maxPlayers", 6);
        config.add("general.countdown", 60);
        config.add("general.startCountdown", 10);
        config.add("general.endCountdown", 10);
        config.add("general.broadcastCountdown", "60,45,30,20,10,5,3,2,1");


        config.add("settings.spawnPoints", new ArrayList<>());
        config.add("settings.spectatorSpawn", "");

    }

    private void setupMap(){
        Utils.createCreationWorld(null, getName());
    }

    private void startTicks() {
        this.ticks = new BukkitRunnable() {
            public void run() {
                Bukkit.getServer().getPluginManager().callEvent(new ArenaTickEvent(Arena.this));
            }
        };
        this.ticks.runTaskTimer(Main.getPlugin(), 0L, 20L);
    }

    public FileConfigurationUtil getConfig() {
        return config;
    }

    public List<ArenaBox> getGlassBoxes() {
        return this.glassBoxes;
    }

    public void resetPlayer(SkyPlayer paramSkyPlayer) {
        setUsed(paramSkyPlayer.getArenaSpawn(), false);
        paramSkyPlayer.setBox(null);
        paramSkyPlayer.setArenaSpawn(null);
        paramSkyPlayer.clearInventory(false);
        paramSkyPlayer.setArena(null);
        paramSkyPlayer.getPlayer().updateInventory();
    }

    public SkyPlayer getWinner() {
        return getAlivePlayer().get(0);
    }

    public int getKillStreak(SkyPlayer paramSkyPlayer) {
        if (this.killStreak.containsKey(paramSkyPlayer)) {
            return this.killStreak.get(paramSkyPlayer).intValue();
        }
        return 0;
    }


    public void addKillStreak(SkyPlayer paramSkyPlayer) {
        if (this.killStreak.containsKey(paramSkyPlayer)) {
            this.killStreak.put(paramSkyPlayer, Integer.valueOf(this.killStreak.get(paramSkyPlayer).intValue() + 1));
        } else {
            this.killStreak.put(paramSkyPlayer, Integer.valueOf(1));
        }
    }

    public void reloadSpawnPoints() {
        loadSpawnPoints();
    }

    public List<Integer> getStartingCounts() {
        List<Integer> list = new ArrayList<>();
        for (String i:getBroadcastCountdown().split(",")){
            list.add(Integer.valueOf(i));
        }
        return list;
    }

    public void start() {
        clearMobs();
        setArenaState(ArenaState.INGAME);
        Main.log("ARENA - Start #1");
        startGo();
        playSignUpdate(SkySignUpdateCause.STATE);
    }

    public void clearMobs() {
        for (Entity entity : getWorld().getEntities()) {
            if (entity instanceof org.bukkit.entity.Animals || entity instanceof org.bukkit.entity.Monster) {
                entity.remove();
            }
        }
    }

    public void startGo() {
        Main.log("ARENA - Start #2");
        broadcast(MSG.GAME_START_GO_ALERT_CHAT.toString());
        broadcast(MSG.GAME_START_GO.toString());
        //Utils.updateTabKill(this);
        Main.log("ARENA - Start #3");
        for (ArenaBox arenaBox : getGlassBoxes()) {
            arenaBox.removeAll(BoxManager.getDefaultBox().getName());
        }

        Main.log("ARENA - Start #4");

        setFallDamage(false);
        getWorld().setTime(0L);
        for (SkyPlayer skyPlayer : this.players) {
            Titles.sendTitle(skyPlayer.getPlayer(), 10, 40, 20, MSG.GAME_START_GO_ALERT.toString(), "");
            skyPlayer.getPlayer().getInventory().clear();
            skyPlayer.getPlayer().closeInventory();
        }

        Main.log("ARENA - Start #full");
    }

    public void end(boolean paramBoolean) {
        this.arenaState = ArenaState.ENDING;
        playSignUpdate(SkySignUpdateCause.STATE);
        for (Player player : getWorld().getPlayers()) {
            if (player.isDead()) {
                (new BukkitRunnable()
                {
                    public void run() {
                        player.spigot().respawn();
                    }
                }).runTaskLater(Main.getPlugin(), 10L);
            }
        }
        if (paramBoolean) {
            this.endCountdown = 2;
        }
    }
    public void clearItems() {
        for (Entity entity : getWorld().getEntities()) {
            if (entity instanceof org.bukkit.entity.Item) {
                entity.remove();
            }
        }
    }
    public void end(final SkyPlayer player) {
        if (getArenaState() == ArenaState.ENDING) {
            return;
        }
        clearItems();
        SkyPlayer paramSkyPlayer = player;
        broadcast(String.format(MSG.GAME_FINISH_BROADCAST_WINNER.toString(), paramSkyPlayer.getName(), this.name));
        paramSkyPlayer.clearInventory(false);
        if (player.isInArena() || player.getPlayer() != null && player.getPlayer().getWorld() != null && player.getPlayer().getWorld().equals(Arena.this.getWorld())) {
            new Fireworks(paramSkyPlayer, this);
        }
        end(false);
        Bukkit.getPluginManager().callEvent(new ArenaFinishEvent(this, paramSkyPlayer));
    }
}
