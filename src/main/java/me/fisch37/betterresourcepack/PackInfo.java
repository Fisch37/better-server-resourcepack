package me.fisch37.betterresourcepack;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class PackInfo {
    private URL url;
    private byte[] sha1;

    public PackInfo(YamlConfiguration config, boolean setHash) throws MalformedURLException, IOException {
        String urlString = config.getString("pack-uri");
        this.url = (urlString==null) ? null : new URL(urlString);
        if (setHash && this.url != null) this.updateSha1();
    }


    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public byte[] getSha1() {
        return sha1;
    }

    public void setSha1(byte[] sha1) {
        this.sha1 = sha1;
    }

    public boolean verifySha1() throws IOException{
        return Arrays.equals(this.fetchSha1(),this.getSha1());
    }

    public byte[] updateSha1() throws IOException{
        this.setSha1(this.fetchSha1());
        return getSha1();
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
        // Hash mismatch, Received data appears correct
        byte[] data = packStream.readAllBytes();


        return hashObject.digest(data);
    }

    public boolean isConfigured(){
        return this.getUrl() != null;
    }
}
