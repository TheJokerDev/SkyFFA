package me.thejokerdev.skyffa.language;

import me.thejokerdev.skyffa.utils.Utils;

public enum MSG {
    NOPERMISSIONS("general.noPermission"),
    GAME_FULL_MESSAGE("game.full.message"),
    GAME_INGAME_MESSAGE("game.ingame.message"),
    GAME_LOADING("game.loading"),
    GAME_RESTART("game.restart"),
    GAME_SPAWN_USED("game.spawnUsed"),
    GAME_PLAYER_JOIN("game.player.join"),
    GAME_PLAYER_QUIT("game.player.quit"),
    VARIABLE_TIMEPLAYED("variable.time"),
    VARIABLE_TIMEPLAYED_SECOND("variable.time-played.second"),
    VARIABLE_TIMEPLAYED_SECONDS("variable.time-played.seconds"),
    VARIABLE_TIMEPLAYED_MINUTE("variable.time-played.minute"),
    VARIABLE_TIMEPLAYED_MINUTES("variable.time-played.minutes"),
    VARIABLE_TIMEPLAYED_HOUR("variable.time-played.hour"),
    VARIABLE_TIMEPLAYED_HOURS("variable.time-played.hours"),
    VARIABLE_TIMEPLAYED_DAY("variable.time-played.day"),
    VARIABLE_TIMEPLAYED_DAYS("variable.time-played.days"),
    GAME_FINISH_BROADCAST_WINNER("game.broadcastWinner"),
    GAME_TIME_LIMIT("game.time.limit"),
    GAME_TIME_LEFT_MINUTES("game.time.left.minutes"),
    GAME_TIME_LEFT_SECONDS("game.time.left.seconds"),
    GAME_START_NOWFULL("game.start.nowfull"),
    GAME_START_COUNTDOWN("game.start.countdown.msg"),
    GAME_START_COUNTDOWN_ALERT_TITLE("game.start.countdown.alert.title"),
    GAME_START_COUNTDOWN_ALERT_SUBTITLE("game.start.countdown.alert.subtitle"),
    GAME_START_COUNTDOWN_ONE("game.start.countdown.one"),
    GAME_START_COUNTDOWN_MINUTES("game.start.countdown.minutes"),
    GAME_START_COUNTDOWN_MINUTES_SECONDS("game.start.countdown.minutes-seconds"),
    GAME_START_NOREQUIREDPLAYERS("game.start.norequiredplayers"),
    GAME_START_DONE("game.start.done"),
    GAME_START_RELEASED("game.start.released"),
    GAME_START_GO("game.start.go.msg"),
    GAME_START_GO_ALERT("game.start.go.alert"),
    GAME_START_GO_ALERT_CHAT("game.start.go.alert-chat"),
    GAME_FORCESTART("game.forcestart"),
    MOTD_WAITING("motd.waiting"),
    MOTD_STARTING("motd.starting"),
    MOTD_INGAME("motd.ingame"),
    MOTD_ENDING("motd.ending"),
    MOTD_LOADING("motd.loading"),
    MOTD_FULL("motd.full");

    private String text;

    private MSG (String msg){
        text = msg;
    }

    @Override
    public String toString() {
        return Utils.getLanguageFile().getString(text);
    }
}
