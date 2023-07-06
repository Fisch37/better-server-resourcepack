package me.fisch37.betterresourcepack.commands;

import me.fisch37.betterresourcepack.BetterServerResourcepack;
import me.fisch37.betterresourcepack.PackInfo;

import java.util.Map;
import java.util.TreeMap;

public class PackCommand extends SubcommandGroup {

    public PackCommand(PackInfo packInfo, BetterServerResourcepack plugin){
        super();
        subcommands = new TreeMap<>(Map.of(
                "set", new SetUriCommand(packInfo),
                "reload", new ReloadCommand(packInfo,plugin)
        ));
    }
}
