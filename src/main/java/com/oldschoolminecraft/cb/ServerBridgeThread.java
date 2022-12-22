package com.oldschoolminecraft.cb;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ServerBridgeThread extends Thread
{
    private BungeePlugin plugin;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public ServerBridgeThread(BungeePlugin plugin, Socket socket)
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
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(rawMessage);
                String secret = String.valueOf(obj.get("secret"));
                String message = String.valueOf(obj.get("message"));
                if (!secret.equals(plugin.config.getStringOption("settings.chat.relaySecret")))
                {
                    dos.writeUTF(plugin.generatePluginMessage("Invalid secret"));
                    socket.close();
                    return;
                }

                plugin.getProxy().getServers().values().forEach(server ->
                        server.getPlayers().forEach(player ->
                                player.sendMessage(message)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
