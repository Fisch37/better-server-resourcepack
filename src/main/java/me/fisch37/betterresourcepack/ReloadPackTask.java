package me.fisch37.betterresourcepack;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

import static me.fisch37.betterresourcepack.Utils.sendMessage;

public class ReloadPackTask extends BukkitRunnable {
    private final Plugin plugin;
    private final PackInfo packInfo;
    private final boolean sync;
    private final boolean push;
    private final CommandSender taskAuthor;

    private FetchTask executingTask;

    public ReloadPackTask(Plugin plugin, CommandSender taskAuthor, PackInfo packInfo, boolean sync, boolean push){
        super();
        this.plugin = plugin;
        this.taskAuthor = taskAuthor;
        this.packInfo = packInfo;
        this.sync = sync;
        this.push = push;
    }

    private static class FetchTask extends BukkitRunnable{
        private final PackInfo packInfo;
        // Using Boolean for trinary false-null-true state
        private Boolean isSuccessful;

        public FetchTask(PackInfo packInfo){
            this.packInfo = packInfo;
        }

        @Override
        public void run() {
            try {
                this.packInfo.updateSha1();
                this.isSuccessful = true;
            } catch (IOException e) {
                this.isSuccessful = false;
            }
        }

        public Boolean getSuccessState(){
            return this.isSuccessful;
        }
    }

    public void start(){
        this.runTaskTimer(this.plugin,0L,2L);
    }


    @Override
    public void run() {
        if (this.executingTask == null) startFetchTask();
        // Success state is null when task still running
        if (this.executingTask.getSuccessState() == null) return;

        boolean op_success = this.executingTask.getSuccessState();
        if (!op_success) {
            sendToAuthor(ChatColor.RED + "Could not fetch resource pack!");
            // Logging sync allows me to essentially debug the situation. Intention is that only /reload executes with sync
            Bukkit.getLogger().warning("[BSP] Could not fetch resource pack in reload task! Sync: " + this.sync);
        } else if (saveHash()){
            sendToAuthor("Updated pack hash!");
            Bukkit.getLogger().info("[BSP] Updated pack hash");
            if (this.push) pushPackToPlayers();
        }
        cancel();
    }

    private void startFetchTask(){
        this.executingTask = new FetchTask(this.packInfo);
        if (this.sync) this.executingTask.runTask(this.plugin);
        else this.executingTask.runTaskAsynchronously(this.plugin);
    }

    private boolean saveHash(){
        try {
            this.packInfo.saveHash();
            return true;
        } catch (IOException e) {
            sendToAuthor(ChatColor.RED + "Could not save hash! The hash is still updated, but will reset on the next restart.");
            Bukkit.getLogger().warning("Could not save hash to cache file in reload task. Sync: " + this.sync);
            return false;
        }
    }

    private void pushPackToPlayers(){
        sendToAuthor("Pushing update to all players");
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            player.setResourcePack(
                    this.packInfo.getUrl().toString(),
                    this.packInfo.getSha1()
            );
        }
    }


    private void sendToAuthor(String message){
        if (this.taskAuthor != null) sendMessage(this.taskAuthor,message);
    }
}
