package me.thejokerdev.skyffa.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface BaseCommand {
    boolean onCommand(CommandSender paramCommandSender, String[] paramArrayOfString);

    List<String> onTabComplete(CommandSender paramCommandSender, String[] paramArrayOfString);

    String help(CommandSender paramCommandSender);

    String getPermission();

    boolean console();
}
