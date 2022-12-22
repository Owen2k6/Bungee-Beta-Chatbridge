package com.oldschoolminecraft.cb;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class BukkitPlugin extends JavaPlugin
{
    public PLConfig config;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    @Override
    public void onEnable()
    {
        try
        {
            config = new PLConfig();
            socket = new Socket("localhost", 8182);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            getServer().getScheduler().scheduleSyncRepeatingTask(this, () ->
            {
                try
                {
                    if (socket.isConnected()) dos.writeUTF("!ping");
                } catch (Exception ignored) {}
            }, 0,  6000); // 6000 ticks = 5 minutes

            System.out.println("Chat bridge connected to relay @ " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        } catch (Exception ex) {
            System.out.println("Chat bridge failed to connect to relay @ " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            System.out.println("Chat bridge has been disabled due to an error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisable()
    {

        try
        {
            socket.close();
            System.out.println("Chat bridge has disconnected from relay @ " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
