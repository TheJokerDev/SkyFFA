package me.thejokerdev.skyffa.listeners.skyffa;

import me.thejokerdev.other.Titles;
import me.thejokerdev.other.XSound;
import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaBox;
import me.thejokerdev.skyffa.arena.ArenaState;
import me.thejokerdev.skyffa.arena.SpectatorReason;
import me.thejokerdev.skyffa.events.ArenaJoinEvent;
import me.thejokerdev.skyffa.events.ArenaLeaveEvent;
import me.thejokerdev.skyffa.events.ArenaTickEvent;
import me.thejokerdev.skyffa.events.enums.ArenaLeaveCause;
import me.thejokerdev.skyffa.language.MSG;
import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.utils.ItemsUtil;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArenaListener implements Listener {

    @EventHandler
    public void onJoin(ArenaJoinEvent e) {
        SkyPlayer skyPlayer = e.getPlayer();
        Arena arena = e.getGame();
        if (arena.getArenaState() == ArenaState.INGAME && skyPlayer.getPlayer().hasPermission("skyffa.admin.spectate")) {
            skyPlayer.clearInventory(true);
            skyPlayer.setArena(arena);
            skyPlayer.teleport(arena.getSpawn());
            skyPlayer.setSpectating(true, SpectatorReason.JOIN);
            return;
        }
        Location location = arena.getSpawnPoint();
        Main.log("Arena.addPlayer - Get Spawn Point " + location);
        if (location == null) {
            Main.log("Arena.addPlayer - Trying to add a Player in a spawn point used");
            skyPlayer.sendMessage(MSG.GAME_SPAWN_USED.toString());
            return;
        }
        for (ArenaBox arenaBox : arena.getGlassBoxes()) {
            Location location1 = arenaBox.getLocation();
            if (location1.equals(location)) {
                Main.log("Arena.addPlayer - Selected box - " + location1);
                skyPlayer.setBox(arenaBox);
            }
        }

        Main.log("Arena.addPlayer - " + skyPlayer.getName() + " is teleporting to " + location.toString());
        arena.setUsed(location, true);
        skyPlayer.setArenaSpawn(location);
        skyPlayer.teleport(location);

        /*String str = skyPlayer.getSelectedBox().getName();
        if (skyPlayer.getSelectedBox() != null && !str.equalsIgnoreCase(SkyWars.boxes.getString("default"))) {
            if (skyPlayer.getBoxItem(skyPlayer.getSelectedBox().getName()) != 0) {
                String str1 = skyPlayer.getSelectedBox().getName();
                int i = skyPlayer.getBoxItem(str1);
                int j = skyPlayer.getBoxData(str1);
                ArenaBox arenaBox = skyPlayer.getBox();
                Main.log("Arena.addPlayer - Box Section=" + str1 + ", Box Item=" + i + ", Box Data=" + j + ", Box=" + arenaBox);
                arenaBox.setBox(str1);
                Location newLoc = location.clone().add(0, BoxManager.getBox(str1).getyPos(), 0);
                skyPlayer.setArenaSpawn(newLoc);
                skyPlayer.teleport(newLoc);
            } else {
                skyPlayer.getPlayer().setMetadata("upload_me", new FixedMetadataValue(SkyWars.getPlugin(), Boolean.valueOf(true)));
                String str1 = BoxManager.getDefaultBox().getName();
                skyPlayer.setSelectedBox(BoxManager.getDefaultBox());
                int i = skyPlayer.getBoxItem(str1);
                int j = skyPlayer.getBoxData(str1);
                ArenaBox arenaBox = skyPlayer.getBox();
                SkyWars.log("Arena.addPlayer - Box Section=" + str1 + ", Box Item=" + i + ", Box Data=" + j + ", Box=" + arenaBox);
                arenaBox.setBox(str1);
                Location newLoc = location.clone().add(0, BoxManager.getBox(str1).getyPos(), 0);
                skyPlayer.setArenaSpawn(newLoc);
                skyPlayer.teleport(newLoc);
            }
        }*/
        Titles.sendTitle(skyPlayer.getPlayer(), 0, 80, 0, Utils.ct("&b&l" + arena.getName().toUpperCase()), Utils.ct("&eSkyFFA Normal"));
        skyPlayer.clearInventory(true);
        skyPlayer.setArena(arena);

        Main.log("Arena.addPlayer - Player already in list: " + arena.getPlayers().contains(skyPlayer));
        if (!arena.getPlayers().contains(skyPlayer)) {
            arena.getPlayers().add(skyPlayer);
            Main.log("Arena.addPlayer - Player add in list");
        }

        if (skyPlayer.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            skyPlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
        for (SkyPlayer skyPlayer1 : arena.getPlayers()) {
            Player player1 = skyPlayer1.getPlayer();
            Player player2 = skyPlayer.getPlayer();
            if (player1 == null || player2 == null || skyPlayer1 == skyPlayer) {
                continue;
            }
            player1.showPlayer(player2);
        }

        skyPlayer.getPlayer().getInventory().setItem(8, ItemsUtil.getLeaveItem());

        Main.log("Arena.addPlayer - Successfull add " + skyPlayer.getName() + " to " + skyPlayer.getArena().getName());

        arena.broadcast(String.format(MSG.GAME_PLAYER_JOIN.toString(), skyPlayer.getName(), arena.getAlivePlayers(), arena.getMaxPlayers()));

        skyPlayer.setSpectating(false, SpectatorReason.JOIN);
    }

    @EventHandler
    public void onLeave(ArenaLeaveEvent e) {
        SkyPlayer skyPlayer = e.getPlayer();
        Arena arena = e.getGame();
        Player player = skyPlayer.getPlayer();
        if (!skyPlayer.isSpectating() && e.getCause() != ArenaLeaveCause.RESTART && (arena.getArenaState() == ArenaState.INGAME || arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING)) {
            if ((arena.getAlivePlayers() - 1) != 0) {
                arena.broadcast(String.format(MSG.GAME_PLAYER_QUIT.toString(), skyPlayer.getName(), arena.getAlivePlayers() - 1, arena.getMaxPlayers()));
            }
        }
        skyPlayer.setSpectating(false, SpectatorReason.LEAVE);
        Main.log("Arena.removePlayer - Removing to " + skyPlayer.getName() + " from " + arena.getName() + " cause: " + e.getCause());

        if (player.isOnline()) {
            arena.getPlayers().remove(skyPlayer);
            Main.log("Arena.removePlayer - Successful remove to " + skyPlayer.getName() + " from " + arena.getName());
        }

        arena.resetPlayer(skyPlayer);
        skyPlayer.teleport(Main.spawn);
        skyPlayer.setArena(null);
        player.setFallDistance(0.0F);
    }

    public static boolean checkWinner(Arena paramArena) {
        if (paramArena.getAlivePlayers() <= 1) {

            if (paramArena.getAlivePlayers() == 0) {
                paramArena.end(true);
                return true;
            }

            SkyPlayer skyPlayer = paramArena.getAlivePlayer().get(0);

            if (skyPlayer != null) {
                Player player = skyPlayer.getPlayer();
                if (player != null) {
                    Location location = player.getLocation();
                    if (location != null) {
                        player.playSound(location, XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
                    }
                }

                paramArena.end(skyPlayer);
            } else {
                paramArena.end(true);
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onArenaTick(ArenaTickEvent paramArenaTickEvent) {
        Arena arena = paramArenaTickEvent.getArena();
        if (arena.getArenaState() == ArenaState.INGAME) {

            if (checkWinner(arena)) {
                return;
            }

            if (!arena.isFallDamage()) {
                if (arena.getStartCountdown() == -5) {
                    arena.setFallDamage(true);
                }

                arena.setCountdown(arena.getCountdown() - 1);
            }
        }

        if (arena.getArenaState() == ArenaState.WAITING || arena.getArenaState() == ArenaState.STARTING) {

            int i = arena.getCountdown();


            if (checkEmpty(arena)) {
                arena.getPlayers().forEach(skyPlayer -> skyPlayer.getPlayer().setExp(0));
                return;
            }


            if (i == 0) {
                if (arena.getPlayers().size() < arena.getMinPlayers()) {
                    arena.setCountdown(arena.getStartCountdown());
                    arena.broadcast(MSG.GAME_START_NOREQUIREDPLAYERS.toString());
                    return;
                }
                if (arena.getArenaState() == ArenaState.STARTING) {
                    Main.log(arena.getArenaState().name());
                    arena.start();
                    for (SkyPlayer skyPlayer : arena.getPlayers()) {
                        skyPlayer.getPlayer().setLevel(0);
                    }
                    return;
                }
            }
            if (arena.getArenaState() == ArenaState.WAITING){
                if (arena.getPlayers().size() >= arena.getMinPlayers()) {
                    if (arena.getPlayers().size() >= arena.getMaxPlayers() || arena.isForceStarted()) {
                        if (i > arena.getStartCountdown()) {
                            arena.setCountdown(arena.getStartCountdown());
                            i = arena.getStartCountdown();
                        }
                        arena.broadcast(String.format(MSG.GAME_START_NOWFULL.toString(), Integer.valueOf(i)));
                    }
                    if (i <= arena.getStartCountdown()) {
                        arena.setArenaState(ArenaState.STARTING);
                    }
                } else {
                    return;
                }
            }
            countStart(arena);
        }

        if (arena.getArenaState() == ArenaState.ENDING)
        {
            countEnd(arena);
        }
    }
    private void countStart(Arena paramArena) {
        int i = paramArena.getCountdown();

        for (SkyPlayer skyPlayer : paramArena.getPlayers()) {
            if (i >= 0) {
                Player player = skyPlayer.getPlayer();
                if (player == null) {
                    continue;
                }
                player.setLevel(i);
            }
        }

        if (paramArena.getStartingCounts().contains(i)) {
            if (i >= 60) {
                int j = i % 3600 / 60;
                int k = i % 60;

                if (k == 0) {
                    paramArena.broadcast(String.format(MSG.GAME_START_COUNTDOWN_MINUTES.toString(), Integer.valueOf(j)));
                } else {
                    paramArena.broadcast(String.format(MSG.GAME_START_COUNTDOWN_MINUTES_SECONDS.toString(), Integer.valueOf(j), Integer.valueOf(k)));
                }
            } else {
                paramArena.getPlayers().forEach(paramSkyPlayer -> {
                    Player player = paramSkyPlayer.getPlayer();
                    if (player == null) {
                        return;
                    }
                    player.playSound(player.getLocation(), XSound.UI_BUTTON_CLICK.parseSound(), 1.0F, 1.0F);
                    if (i <= 5) {
                        Titles.sendTitle(player, 0, 25, 0, String.format(MSG.GAME_START_COUNTDOWN_ALERT_TITLE.toString(), i), MSG.GAME_START_COUNTDOWN_ALERT_SUBTITLE.toString());
                    }
                });
                if (i == 1) {
                    paramArena.broadcast(String.format(MSG.GAME_START_COUNTDOWN_ONE.toString(), Integer.valueOf(i)));
                } else {
                    paramArena.broadcast(String.format(MSG.GAME_START_COUNTDOWN.toString(), Integer.valueOf(i)));
                }
            }
        }

        paramArena.setCountdown(i - 1);
    }
    private void countEnd(Arena paramArena) {
        int i = paramArena.getEndCountdown();
        if (i == 0) {
            paramArena.restart();
        }
        paramArena.setEndCountdown(paramArena.getEndCountdown() - 1);
    }
    private boolean checkEmpty(Arena paramArena) {
        if (paramArena.getPlayers().size() <= 1) {
            paramArena.setCountdown(paramArena.getConfig().getInt("general.countdown"));
            for (SkyPlayer skyPlayer : paramArena.getPlayers()){
                skyPlayer.getPlayer().setLevel(0);
            }
            if (paramArena.getArenaState() != ArenaState.WAITING) {
                paramArena.setArenaState(ArenaState.WAITING);
            }
            return true;
        }
        return false;
    }
}
