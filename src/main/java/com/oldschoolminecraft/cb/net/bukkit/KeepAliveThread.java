package com.oldschoolminecraft.cb.net.bukkit;

import com.oldschoolminecraft.cb.BukkitPlugin;

import java.io.DataOutputStream;
import java.net.Socket;

public class KeepAliveThread extends Thread
{
    private BukkitPlugin plugin;
    private Socket socket;
    private DataOutputStream dos;

    public KeepAliveThread(BukkitPlugin plugin, Socket socket, DataOutputStream dos)
    {
        this.plugin = plugin;
        this.socket = socket;
        this.dos = dos;
    }

    public void run()
    {
        try
        {
            while (socket.isConnected())
            {
                dos.writeUTF("!ping");
                Thread.sleep(1000 * 60 * 5); // 5 minutes
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
