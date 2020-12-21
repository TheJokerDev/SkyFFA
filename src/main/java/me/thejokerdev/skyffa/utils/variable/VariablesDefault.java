package me.thejokerdev.skyffa.utils.variable;

import me.thejokerdev.skyffa.player.SkyPlayer;
import me.thejokerdev.skyffa.utils.Utils;

public final class VariablesDefault implements VariableReplacer {
    public VariablesDefault() {
    }

    @Variable(
            vars = {"PLAYER", "USERNAME", "NAME"}
    )
    public String nameVar(SkyPlayer var1) {
        return var1.getName();
    }

    @Variable(
            vars = {"A-NAME", "A_NAME", "ARENA-NAME", "ARENA_NAME"}
    )
    public String arenaNameVar(SkyPlayer var1) {
        return var1.isInArena() ? var1.getArena().getName() : "NONE";
    }

    @Variable(
            vars = {"A-WINNER", "A_WINNER", "ARENA-WINNER", "ARENA_WINNER"}
    )
    public String arenaWinnerVar(SkyPlayer var1) {
        return var1.isInArena() ? var1.getArena().getWinner().getName() : "NONE";
    }

    @Variable(
            vars = {"A-COUNTDOWN", "A_COUNTDOWN", "ARENA-COUNTDOWN", "ARENA_COUNTDOWN"}
    )
    public String arenaStartCountdownVar(SkyPlayer var1) {
        return var1.isInArena() ? Utils.getTimePlayedInArena(var1.getArena().getStartCountdown()+1) : "NONE";
    }

    @Variable(
            vars = {"A-WINNER-KILLS", "A_WINNER_KILLS", "ARENA-WINNER_KILLS", "ARENA_WINNER_KILLS"}
    )
    public String winnerKillsVar(SkyPlayer var1) {
        return var1.isInArena() ? String.valueOf(var1.getArena().getKillStreak(var1.getArena().getWinner())) : "NONE";
    }

    @Variable(
            vars = {"A-MAX-P", "A_MAX_P", "ARENA-MAX-PLAYERS", "ARENA_MAX_PLAYERS"}
    )
    public String arenaMaxPlayersVar(SkyPlayer var1) {
        return var1.isInArena() ? String.valueOf(var1.getArena().getMaxPlayers()) : "NONE";
    }

    @Variable(
            vars = {"A-INGAME-P", "A_INGAME_P", "ARENA-INGAME-PLAYERS", "ARENA_INGAME_PLAYERS"}
    )
    public String arenaInGamePlayersVar(SkyPlayer var1) {
        return var1.isInArena() ? String.valueOf(var1.getArena().getAlivePlayers()) : "NONE";
    }

    @Variable(
            vars = {"SW-KILL-STREAK", "STATS_KILL-STREAK", "STATS-KILL-STREAK", "SW-KS"}
    )
    public String swKillStreakVar(SkyPlayer var1) {
        return var1.isInArena() ? String.valueOf(var1.getArena().getKillStreak(var1)) : "0";
    }

    @Variable(
            vars = {"EMPTY"}
    )
    public String emptyVar(SkyPlayer var1) {
        return " ";
    }
}

