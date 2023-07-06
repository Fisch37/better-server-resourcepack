package me.fisch37.betterresourcepack.commands;

import org.bukkit.command.TabExecutor;

public interface CommandWithHelp extends TabExecutor {
    public String getUsage();

    public String getPurpose();

    public String getPurposeLong();
}
