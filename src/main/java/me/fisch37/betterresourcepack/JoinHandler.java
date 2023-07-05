package me.fisch37.betterresourcepack;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinHandler implements Listener {
    private final PackInfo packInfo;

    public JoinHandler(PackInfo packInfo){
        this.packInfo = packInfo;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event){
        if (this.packInfo.getUrl() == null) return;
        event.getPlayer().setResourcePack(
                this.packInfo.getUrl().toString(),
                this.packInfo.getSha1()
        );
    }
}
