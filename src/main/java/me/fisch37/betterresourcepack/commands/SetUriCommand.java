package me.fisch37.betterresourcepack.commands;

import me.fisch37.betterresourcepack.PackInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static me.fisch37.betterresourcepack.Utils.sendMessage;

public class SetUriCommand implements PermissibleCommand, CommandWithHelp{
    private final PackInfo packInfo;

    public SetUriCommand(PackInfo packInfo){
        this.packInfo = packInfo;
    }

    @Override
    public String getUsage() {
        return "/pack set [<url>]";
    }

    @Override
    public String getPurpose() {
        return "Set/reset the server resourcepack url";
    }

    @Override
    public String getPurposeLong() {
        return "Set/reset the server resourcepack url. Note that this does not reload the resourcepack.";
    }

    @Override
    public boolean hasPermission(CommandSender sender, Command command, String label, String[] args) {
        return sender.hasPermission("betterserverpack.set");
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args) {
        if (args.length > 1){
            sendMessage(sender,ChatColor.RED+"Invalid number of arguments. Expected 0-1, got "+args.length);
            return false;
        }
        try {
            this.packInfo.setUrl((args.length == 1) ? new URL(args[0]) : null);
            this.packInfo.saveURL();
        } catch (java.net.MalformedURLException e){
            sendMessage(sender,ChatColor.RED+"Invalid URL format.");
            return false;
        }
        sendMessage(sender,"Pack URL has been updated!");
        return true;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            String[] strings
    ) {
        return new ArrayList<>();
    }
}
