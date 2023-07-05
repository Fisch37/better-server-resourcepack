package me.fisch37.betterresourcepack;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public final class BetterServerResourcepack extends JavaPlugin {
    private final static String configName = "betterresourcepack.yaml";
    private YamlConfiguration config;
    private PackInfo packInfo;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.config = loadConfig();
        try{
            this.packInfo = new PackInfo(this.config,true);
        } catch (MalformedURLException e){
            Bukkit.getLogger().warning("Resourcepack URL has an invalid format");
        } catch (IOException e){
            Bukkit.getLogger().warning("Resourepack could not be accessed. This plugin will not work unless you reload it");
        }
        getServer().getPluginManager().registerEvents(new JoinHandler(this.packInfo),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    private YamlConfiguration loadConfig(){
        File path = new File(getDataFolder(),configName);
        if (!path.exists()){
            path.getParentFile().mkdirs();
            saveResource(configName,false);
        }
        return YamlConfiguration.loadConfiguration(path);
    }

    public YamlConfiguration getConfig(){
        return this.config;
    }
}
