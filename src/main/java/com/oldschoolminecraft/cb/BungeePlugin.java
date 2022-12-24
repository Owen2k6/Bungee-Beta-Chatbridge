package com.oldschoolminecraft.cb;

import com.oldschoolminecraft.cb.net.proxy.ConnectionHandlerThread;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
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

        System.out.println("Chat bridge relay listening on " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (cmd.getName().equalsIgnoreCase("chatbridge"))
        {
            if (!sender.hasPermission("chatbridge.admin"))
            {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return true;
            }

            if (args.length == 0)
            {
                sender.sendMessage(ChatColor.DARK_GRAY + "Host: " + ChatColor.GRAY + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());
                sender.sendMessage(ChatColor.BLUE + "Status: " + (serverSocket.isClosed() ? ChatColor.RED + "Closed" : ChatColor.GREEN + "Open"));
                return true;
            }

            if (args[0].equalsIgnoreCase("reload"))
            {
                config.reload();
                sender.sendMessage("Chat bridge config reloaded");
                return true;
            }

            if (args[0].equalsIgnoreCase("disable"))
            {
                //getProxy().getPluginManager().disablePlugin(this);
                //sender.sendMessage("Chat bridge disabled");
                return true;
            }
        }
        return true;
    }

    @Override
    public void onDisable()
    {
        try
        {
            serverSocket.close();
            System.out.println("Chat bridge relay closed!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String generatePluginMessage(String message)
    {
        JSONObject obj = new JSONObject();
        obj.put("secret", config.getStringOption("settings.chat.relaySecret"));
        obj.put("message", message);
        return obj.toJSONString();
    }
}
