package me.fisch37.betterresourcepack;

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
        if (setHash) this.updateSha1();
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

    private InputStream getPack() throws IOException {
        return this.getUrl().openStream();
    }

    private byte[] fetchSha1() throws IOException{
        MessageDigest hashObject;
        try{
            hashObject = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e){
            // By the Java Docs: Every JVM has to support SHA1 hashes. There is no reason for this error to even occur
            // (so I changed it into a RuntimeException, which doesn't require catching)
            // Is this good programming? No
            throw new RuntimeException("No sha1 algorithm found for this java implementation. How did we get here?",e);
        }
        InputStream packStream = this.getPack();
        // Reading like this to save memory for potentially large packs
        byte[] singleByte = new byte[1];
        int bytesRead = 0;
        while (bytesRead != -1){
            bytesRead = packStream.read(singleByte);
            hashObject.update(singleByte);
        }
        return hashObject.digest();
    }
}
