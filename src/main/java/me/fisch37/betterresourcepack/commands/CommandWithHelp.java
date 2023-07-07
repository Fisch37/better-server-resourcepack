package me.fisch37.betterresourcepack.commands;

import org.bukkit.command.TabExecutor;

@SuppressWarnings("unused")
public interface CommandWithHelp extends TabExecutor {
    String getUsage();

    String getPurpose();

    String getPurposeLong();
}
