package me.fisch37.betterresourcepack;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.MalformedURLException;
import java.util.HexFormat;

public final class BetterServerResourcepack extends JavaPlugin {
    private final static String configName = "config.yaml";
    private final static String hashCacheName = "pack.sha1";
    private YamlConfiguration config;
    private PackInfo packInfo;

    private final static java.util.logging.Logger logger = Bukkit.getLogger();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.config = loadConfig();
        boolean forceHashUpdate = this.config.getBoolean("force-hash-update-on-start");
        File hashCache = new File(getDataFolder(), hashCacheName);
        boolean shouldUpdateHash = forceHashUpdate || !hashCache.exists();
        final HexFormat hexFormatter = HexFormat.of();

        boolean loadSuccessful = false;
        try{
            this.packInfo = new PackInfo(this.config,shouldUpdateHash);
            loadSuccessful = true;
        } catch (MalformedURLException e){
            logger.warning("[BetterServerResourcepack] Resourcepack URL has an invalid format");
        } catch (IOException e){
            logger.warning("[BetterServerResourcepack] Resourepack could not be accessed. This plugin will not work unless you reload it");
        }
        if (loadSuccessful && this.packInfo.isConfigured()) {
            if (shouldUpdateHash) {
                // No need to create parents, should already be handled by loadConfig
                try (FileWriter hashWriter = new FileWriter(hashCache)) {
                    hashWriter.write(hexFormatter.formatHex(this.packInfo.getSha1()));
                } catch (IOException e) {
                    logger.warning("[BetterServerResourcepack] Could not update cached hash!");
                }
            } else{
                try (BufferedReader hashReader = new BufferedReader(new FileReader(hashCache))){
                    String hexHash = hashReader.readLine();
                    this.packInfo.setSha1(hexFormatter.parseHex(hexHash));
                } catch (java.io.FileNotFoundException e){
                    // I have done stuff like this in other parts of this plugin as well.
                    // Basically: We don't ever want this error to occur.
                    // => If it occurs, its real bad and should appear as a full-blown error.
                    throw new RuntimeException(e);
                } catch (IOException e){
                    logger.severe("Could not load cached hash! The plugin will not work unless you reload it.");
                }
            }
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
