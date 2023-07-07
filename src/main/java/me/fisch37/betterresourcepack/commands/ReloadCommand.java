package me.fisch37.betterresourcepack.commands;

import me.fisch37.betterresourcepack.BetterServerResourcepack;
import me.fisch37.betterresourcepack.PackInfo;
import me.fisch37.betterresourcepack.ReloadPackTask;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;

import static me.fisch37.betterresourcepack.Utils.sendMessage;

public class ReloadCommand implements PermissibleCommand, CommandWithHelp{
    private final PackInfo packInfo;
    private final BetterServerResourcepack plugin;

    public ReloadCommand(PackInfo packInfo, BetterServerResourcepack plugin){
        super();
        this.packInfo = packInfo;
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "/pack reload [push]";
    }

    @Override
    public String getPurpose() {
        return "Reloads the resource pack hash.";
    }

    @Override
    public String getPurposeLong() {
        return "Reloads the resource pack hash. If argument \"push\" is supplied, re-prompts resource pack to all players.";
    }

    @Override
    public boolean hasPermission(CommandSender sender, Command command, String label, String[] args) {
        return sender.hasPermission("betterserverpack.reload");
    }



    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args
    ) {
        if (args.length > 1){
            sendMessage(sender, ChatColor.RED+"Too many arguments supplied. Expected 0-1, got "+args.length);
            return false;
        }
        if (args.length == 1 && !args[0].equals("push")){
            sendMessage(sender, ChatColor.RED+"Invalid argument \""+args[0]+"\" supplied. Can only be \"push\"");
            return false;
        }
        boolean push = args.length == 1;
        if (!this.packInfo.isConfigured()){
            sendMessage(sender, ChatColor.RED+"Cannot reload. No resource pack is configured.");
            return true;
        }
        sendMessage(sender,"Reloading resource pack. This may take a while.");
        new ReloadPackTask(
                this.plugin,
                sender,
                this.packInfo,
                false,
                push
        ).start();


        return true;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args
    ) {
        if (args.length == 1) return List.of("push");
        return new ArrayList<>();
    }
}
