package com.oldschoolminecraft.cb.net.proxy;

import com.oldschoolminecraft.cb.BungeePlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ProxyBridgeThread extends Thread
{
    private BungeePlugin plugin;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean loggedIn = false;

    public ProxyBridgeThread(BungeePlugin plugin, Socket socket)
    {
        try
        {
            this.plugin = plugin;
            this.socket = socket;
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run()
    {
        try
        {
            while (socket.isConnected())
            {
                String rawMessage = dis.readUTF();
                String[] split = rawMessage.split(" ");
                switch (split[0])
                {
                    default:
                        dos.writeUTF("ERROR Unknown command");
                        break;
                    case "PING":
                        dos.writeUTF("PONG");
                        break;
                    case "CHAT":
                        if (!loggedIn)
                        {
                            dos.writeUTF("ERROR Not logged in");
                            break;
                        }

                        if (split.length < 2)
                        {
                            dos.writeUTF("ERROR Missing arguments");
                            break;
                        }

                        StringBuilder message = new StringBuilder();
                        for (int i = 1; i < split.length; i++)
                            message.append(split[i]).append(" ");

                        plugin.getProxy().getServers().values().forEach(server ->
                                server.getPlayers().forEach(player ->
                                        player.sendMessage(message.toString())));
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
