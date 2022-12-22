package com.oldschoolminecraft.cb;

import com.oldschoolminecraft.cb.net.proxy.ConnectionHandlerThread;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class BungeePlugin extends Plugin implements Listener
{
    public PLConfig config;
    private ServerSocket serverSocket;
    private Thread socketReadThread;

    @Override
    public void onEnable()
    {
        try
        {
            config = new PLConfig();
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(config.getStringOption("settings.chat.relayHost", "0.0.0.0"), (int) config.getConfigOption("settings.chat.relayPort", 8182)));

            socketReadThread = new ConnectionHandlerThread(this, serverSocket);
            socketReadThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getProxy().getPluginManager().registerListener(this, this);

        System.out.println("Bungee-Beta-Chatbridge enabled!");
    }

    @Override
    public void onDisable()
    {
        System.out.println("Bungee-Beta-Chatbridge disabled!");
    }

    public String generatePluginMessage(String message)
    {
        JSONObject obj = new JSONObject();
        obj.put("secret", config.getStringOption("settings.chat.relaySecret"));
        obj.put("message", message);
        return obj.toJSONString();
    }
}
