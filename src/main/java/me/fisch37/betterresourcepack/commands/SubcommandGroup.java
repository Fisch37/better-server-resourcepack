package me.fisch37.betterresourcepack.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SubcommandGroup implements TabExecutor {
    public static Map<String,TabExecutor> subcommands;

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args
    ) {
        if (args.length == 0) return false;
        for (String key : subcommands.keySet()){
            if (key.equals(args[0])){
                CommandExecutor commandExecutor = subcommands.get(key);
                if (checkHasNoCommandPermissions(commandExecutor, sender, command, label, args)){
                    sender.sendMessage(ChatColor.RED+"You do not have permission to execute this command");
                    return true;
                }
                boolean commandResult = commandExecutor.onCommand(
                        sender,
                        command,
                        label + " " + key,
                        Arrays.copyOfRange(args,1,args.length)
                );
                if (commandExecutor instanceof  CommandWithHelp){
                    if (!commandResult) sender.sendMessage(((CommandWithHelp) commandExecutor).getUsage());
                    return true;
                }
                return commandResult; // Legacy behaviour
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args
    ) {
        if (args.length == 1){
            List<String> possibilities = new ArrayList<>();
            for (Map.Entry<String,TabExecutor> item : subcommands.entrySet()){
                if (checkHasNoCommandPermissions(item.getValue(), sender, command, label, args))
                    continue;
                possibilities.add(item.getKey());
            }
            return possibilities;
        }
        for (String key : subcommands.keySet()){
            if (key.equals(args[0])){
                TabExecutor executor = subcommands.get(key);
                if (checkHasNoCommandPermissions(executor, sender, command, label, args))
                    return new ArrayList<>();
                return executor.onTabComplete(
                        sender,
                        command,
                        label + " " + key,
                        Arrays.copyOfRange(args,1,args.length)
                );
            }
        }
        return new ArrayList<>();
    }

    private boolean checkHasNoCommandPermissions(
            CommandExecutor executor,
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ){
        return executor instanceof PermissibleCommand withPermissions &&
                !withPermissions.hasPermission(sender, command, label, args);
    }
}
