package com.oldschoolminecraft.cb.net.proxy;

import com.oldschoolminecraft.cb.BungeePlugin;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
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
                    case "LOGIN":
                        String secret = split[1];

                        if (secret.equals(plugin.config.getStringOption("settings.chat.relaySecret")))
                        {
                            System.out.println("Relay LOGIN: " + socket.getInetAddress().getHostAddress());
                            loggedIn = true;
                            dos.writeUTF("LOGIN_SUCCESS");
                        } else {
                            System.out.println("Received bad login request from " + socket.getInetAddress().getHostAddress() + " with secret <" + secret + ">");
                            System.out.println("Expected secret <" + plugin.config.getStringOption("settings.chat.relaySecret") + ">");
                            dos.writeUTF("LOGIN_FAILED");
                        }
                        break;
                    case "LOGOUT":
                        socket.close();
                        System.out.println("Relay LOGOUT: " + socket.getInetAddress().getHostAddress());
                        break;
                    case "CHAT":
                        if (!loggedIn)
                        {
                            dos.writeUTF("ERROR Not logged in");
                            break;
                        }

                        String serverName = split[1];
                        String displayName = split[2];
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i < split.length; i++)
                            sb.append(split[i]).append(" ");

                        System.out.println(String.format("Relay CHAT: [%s] <%s> %s", socket.getInetAddress().getHostAddress(), displayName, sb));

                        String chatFormat = plugin.config.getStringOption("settings.chat.chatFormat");
                        String message = chatFormat
                                .replace("{server}", serverName)
                                .replace("{player}", displayName)
                                .replace("{message}", sb.toString().trim().replace("\n", ""));

                        for (ServerInfo server : plugin.getProxy().getServers().values())
                            if (!server.getAddress().getAddress().getHostAddress().equals(socket.getInetAddress().getHostAddress()))
                                server.getPlayers().forEach(player -> player.sendMessage(message));
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
