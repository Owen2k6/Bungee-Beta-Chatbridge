package com.oldschoolminecraft.cb;

import com.oldschoolminecraft.cb.config.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class PLConfig extends Configuration
{
    public PLConfig()
    {
        super(new File("plugins/Bungee-Beta-Chatbridge/config.yml"));
        this.reload();
    }

    public void reload()
    {
        this.load();
        this.write();
        this.save();
    }

    private void write()
    {
        generateConfigOption("settings.chat.relayHost", "0.0.0.0");
        generateConfigOption("settings.chat.relayPort", 8182);
        generateConfigOption("settings.chat.relaySecret", Util.generateRandomString(32));
        generateConfigOption("settings.chat.chatFormat", "§7[§b{server}§7] §f{player}§7: §f{message}");
        generateConfigOption("settings.server.serverName", "A Minecraft server");
    }

    private void generateConfigOption(String key, Object defaultValue)
    {
        if (this.getProperty(key) == null) this.setProperty(key, defaultValue);
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    public String getStringOption(String key)
    {
        return String.valueOf(getConfigOption(key));
    }

    public String getStringOption(String key, String defaultValue)
    {
        return String.valueOf(getConfigOption(key, defaultValue));
    }

    public Object getConfigOption(String key)
    {
        return this.getProperty(key);
    }

    public Object getConfigOption(String key, Object defaultValue)
    {
        Object value = getConfigOption(key);
        if (value == null) value = defaultValue;
        return value;
    }

    public List<String> getConfigList(String key)
    {
        return Arrays.asList(String.valueOf(getConfigOption(key, "")).trim().split(","));
    }
}