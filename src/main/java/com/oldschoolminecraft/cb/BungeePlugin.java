package com.oldschoolminecraft.cb;

import com.google.common.eventbus.Subscribe;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

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

            socketReadThread = new ConnectionHandlerThread(serverSocket);
            socketReadThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getProxy().getPluginManager().registerListener(this, this);
    }

    @Override
    public void onDisable()
    {
        //
    }

    public String generatePluginMessage(String message)
    {
        JSONObject obj = new JSONObject();
        obj.put("secret", config.getStringOption("settings.chat.relaySecret"));
        obj.put("message", message);
        return obj.toJSONString();
    }
}
