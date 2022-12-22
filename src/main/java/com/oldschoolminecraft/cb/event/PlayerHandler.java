package com.oldschoolminecraft.cb.event;

import com.oldschoolminecraft.cb.BukkitPlugin;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class PlayerHandler extends PlayerListener
{
    private BukkitPlugin plugin;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public PlayerHandler(BukkitPlugin plugin, Socket socket, DataInputStream dis, DataOutputStream dos)
    {
        this.plugin = plugin;
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;
    }

    public void onPlayerChat(PlayerChatEvent event)
    {
        try
        {
            JSONObject obj = new JSONObject();
            obj.put("secret", plugin.config.getStringOption("settings.chat.relaySecret"));
            obj.put("message", event.getMessage());
            dos.writeUTF(obj.toJSONString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
