package me.fisch37.betterresourcepack;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Utils {
    public final static void sendMessage(CommandSender sender, String message){
        sender.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"[BSP] "+ChatColor.RESET+message);
    }
}
