/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.io.ByteStreams
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.gmail.necnionch.myplugin.diamondcompressor.common;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitConfigDriver {
    private final JavaPlugin plugin;
    private final Logger logger;
    private String fileName = "config.yml";
    private String resourceFileName = "bukkit-config.yml";
    public FileConfiguration config = null;
    private String header = null;

    public BukkitConfigDriver(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public BukkitConfigDriver(JavaPlugin plugin, String fileName, String resourceFileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.resourceFileName = resourceFileName;
        this.logger = plugin.getLogger();
    }

    public Logger getLogger() {
        return this.logger;
    }

    public boolean isExistFile() {
        return new File(this.plugin.getDataFolder(), this.fileName).isFile();
    }

    public boolean load() {
        try {
            YamlConfiguration config;
            File file;
            if (!this.plugin.getDataFolder().exists()) {
                this.plugin.getDataFolder().mkdir();
            }
            if (!(file = new File(this.plugin.getDataFolder(), this.fileName)).exists()) {
                file.createNewFile();
                try (InputStream inputStream = this.plugin.getResource(this.resourceFileName);
                     FileOutputStream outputStream = new FileOutputStream(file);){
                    ByteStreams.copy((InputStream)inputStream, (OutputStream)outputStream);
                }
            }
            try (InputStreamReader stream = new InputStreamReader((InputStream)new FileInputStream(file), Charsets.UTF_8);){
                config = YamlConfiguration.loadConfiguration((Reader)stream);
            }
            this.config = config;
            return this.onLoaded((FileConfiguration)config);
        }
        catch (Exception e) {
            this.logger.severe("Could not load \"" + this.fileName + "\".");
            this.logger.severe(e.getClass().getName() + ": " + e.getLocalizedMessage());
            return false;
        }
    }

    public boolean save() {
        boolean bl;
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdir();
        }
        File file = new File(this.plugin.getDataFolder(), this.fileName);
        if (this.config == null) {
            return false;
        }
        OutputStreamWriter stream = new OutputStreamWriter((OutputStream)new FileOutputStream(file), Charsets.UTF_8);
        try {
            stream.write(this.config.saveToString());
            bl = true;
        }
        catch (Throwable throwable) {
            try {
                try {
                    stream.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Exception e) {
                this.logger.severe("Could not save \"" + this.fileName + "\".");
                this.logger.severe(e.getClass().getName() + ": " + e.getLocalizedMessage());
                return false;
            }
        }
        stream.close();
        return bl;
    }

    public boolean onLoaded(FileConfiguration config) {
        return true;
    }

    public void header(String text) {
        this.header = text;
    }

    public String header() {
        return this.header;
    }

    public void addHeaderText(String title, String ... comments) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n");
        for (String c : comments) {
            sb.append("  ").append(c).append("\n");
        }
        sb.append("\n");
        Object header = this.header;
        header = header == null ? "\n" + String.valueOf(sb) : (!((String)header).endsWith("\n") ? (String)header + "\n" + String.valueOf(sb) : (String)header + sb.toString());
        this.header = header;
    }

    public void saveHeaderIfNotContains(boolean save) {
        String header;
        if (!(this.config == null || this.header == null || (header = this.config.options().header()) != null && header.contains(this.header))) {
            this.config.options().header(this.header);
            if (save) {
                this.save();
            }
        }
    }
}
