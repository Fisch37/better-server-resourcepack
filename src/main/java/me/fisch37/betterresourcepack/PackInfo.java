package me.fisch37.betterresourcepack;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class PackInfo {
    private final BetterServerResourcepack plugin;
    private final File cachePath;
    private URL url;
    private byte[] sha1;

    public PackInfo(
            BetterServerResourcepack plugin,
            boolean setHash,
            File cachePath
    ) throws MalformedURLException {
        this.plugin = plugin;
        this.cachePath = cachePath;
        String urlString = this.plugin.getConfig().getString("pack-uri");
        if (urlString == null){
            Bukkit.getLogger().warning("[BSP] Missing config key \"pack-uri\"");
            urlString = "";
        }
        this.url = (urlString.isEmpty()) ? null : new URL(urlString);
        if (setHash && this.url != null){
            try{
                this.updateSha1();
            } catch (IOException e){
                Bukkit.getLogger()
                        .warning("[BSP] Resourcepack inacessible. You will need to reload the hash using /pack reload");
            }
        }
    }


    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
        this.plugin.getConfig().set("pack-uri",url == null ? "" : url.toString());
    }

    public synchronized byte[] getSha1() {
        return sha1;
    }

    public synchronized void setSha1(byte[] sha1) {
        this.sha1 = sha1;
    }

    public synchronized void updateSha1() throws IOException{
        this.setSha1(this.fetchSha1());
    }

    public InputStream getPack() throws IOException {
        return this.getUrl().openStream();
    }

    private byte[] fetchSha1() throws IOException{
        Bukkit.getLogger().info("Fetching resource pack hash");
        MessageDigest hashObject;
        try{
            hashObject = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e){
            // By the Java Docs: Every JVM has to support SHA1 hashes. There is no reason for this error to even occur
            // (so I changed it into a RuntimeException, which doesn't require catching)
            // Is this good programming? No
            throw new RuntimeException("No sha1 algorithm found for this java implementation. How did we get here?",e);
        }
        Bukkit.getLogger().config("Downloading pack from "+this.getUrl());
        InputStream packStream = this.getPack();
        Bukkit.getLogger().config("Pack downloaded, assembling hash and exiting");
        byte[] packBuffer = new byte[this.plugin.getConfig().getInt("pack-buffer-size",8192)];
        int lastReadSize = 0;
        while (lastReadSize != -1){
            lastReadSize = packStream.read(packBuffer);
            hashObject.update(packBuffer);
        }


        return hashObject.digest();
    }

    public void saveURL(){
        this.plugin.saveConfig();
    }
    public void saveHash() throws IOException{
        // No need to create parents, should already be handled by loadConfig
        try (FileWriter hashWriter = new FileWriter(this.cachePath)) {
            hashWriter.write(HexFormat.of().formatHex(this.getSha1()));
        }
    }

    public boolean isConfigured(){
        return this.getUrl() != null && this.getSha1() != null;
    }
}
