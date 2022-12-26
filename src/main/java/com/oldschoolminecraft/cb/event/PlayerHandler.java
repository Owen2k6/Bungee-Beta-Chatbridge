package com.oldschoolminecraft.cb.event;

import com.oldschoolminecraft.cb.BukkitPlugin;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class PlayerHandler extends PlayerListener
{
    private BukkitPlugin plugin;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public PlayerHandler(BukkitPlugin plugin)
    {
        this.plugin = plugin;
        this.socket = plugin.getSocket();
        this.dis = plugin.getDIS();
        this.dos = plugin.getDOS();
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event)
    {
        try
        {
            if (socket.isClosed())
            {
                plugin.loginRelay();
                socket = plugin.getSocket();
                dis = plugin.getDIS();
                dos = plugin.getDOS();
            }

            plugin.getDOS().writeUTF(String.format("CHAT %s %s %s", plugin.config.getStringOption("settings.server.serverName"), event.getPlayer().getDisplayName(), event.getMessage()));
        } catch (SocketException ex) {
            plugin.loginRelay();
            socket = plugin.getSocket();
            dis = plugin.getDIS();
            dos = plugin.getDOS();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
