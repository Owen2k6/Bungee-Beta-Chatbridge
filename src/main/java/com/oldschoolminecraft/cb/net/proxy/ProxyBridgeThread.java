package com.oldschoolminecraft.cb.net.proxy;

import com.oldschoolminecraft.cb.BungeePlugin;
import com.oldschoolminecraft.cb.Util;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class ProxyBridgeThread extends Thread {
    private BungeePlugin plugin;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean loggedIn = false;

    public ProxyBridgeThread(BungeePlugin plugin, Socket socket) {
        try {
            this.plugin = plugin;
            this.socket = socket;
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        try {
            while (socket.isConnected()) {
                String rawMessage = dis.readUTF();
                System.out.println("Relay Inbound RAW: <" + rawMessage + ">");
                String[] split = rawMessage.split(" ");
                switch (split[0]) {
                    default:
                        dos.writeUTF("ERROR Unknown command");
                        break;
                    case "PING":
                        dos.writeUTF("PONG");
                        break;
                    case "LOGIN":
                        String secret = split[1];

                        if (secret.equals(plugin.config.getStringOption("settings.chat.relaySecret"))) {
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
                        if (!loggedIn) {
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

                        for (ServerInfo server : plugin.getProxy().getServers().values()) {
                            if (server.getName().equalsIgnoreCase(serverName)) continue;
                            for (ProxiedPlayer player : server.getPlayers()) {
                                if (player.getDisplayName().equals(displayName)) continue;
                                sendMultiline(player, message);
                            }
                        }
                        break;
                    case "PJOIN":
                        if (!loggedIn) {
                            dos.writeUTF("ERROR Not logged in");
                            break;
                        }

                        String sername = split[2];
                        String disName = split[1];
                        StringBuilder stb = new StringBuilder();
                        for (int i = 3; i < split.length; i++)
                            stb.append(split[i]).append(" ");

                        System.out.println(String.format("Relay PJOIN: %s %s ", socket.getInetAddress().getHostAddress(), disName, stb));

                        String joinFormat = plugin.config.getStringOption("settings.chat.pjoinFormat");
                        String msg = joinFormat
                                .replace("{server}", sername)
                                .replace("{player}", disName);


                        for (ServerInfo server : plugin.getProxy().getServers().values()) {
                            if (server.getName().equalsIgnoreCase(sername)) continue;
                            for (ProxiedPlayer player : server.getPlayers()) {
                                if (player.getDisplayName().equals(disName)) continue;
                                sendMultiline(player, msg);
                            }
                        }
                        break;
                        case "PQUIT":
                        if (!loggedIn) {
                            dos.writeUTF("ERROR Not logged in");
                            break;
                        }

                        String seraaname = split[2];
                        String disaaName = split[1];
                        StringBuilder staab = new StringBuilder();
                        for (int i = 3; i < split.length; i++)
                            staab.append(split[i]).append(" ");

                        System.out.println(String.format("Relay PQUIT: %s %s ", socket.getInetAddress().getHostAddress(), disaaName, staab));

                        String quitFormat = plugin.config.getStringOption("settings.chat.pquitFormat");
                        String msaag = quitFormat
                                .replace("{server}", seraaname)
                                .replace("{player}", disaaName);


                        for (ServerInfo server : plugin.getProxy().getServers().values()) {
                            if (server.getName().equalsIgnoreCase(seraaname)) continue;
                            for (ProxiedPlayer player : server.getPlayers()) {
                                if (player.getDisplayName().equals(disaaName)) continue;
                                sendMultiline(player, msaag);
                            }
                        }
                        break;
                }
            }
        } catch (SocketException ex) {
            if (!ex.getMessage().contains("Socket closed") && !ex.getMessage().contains("Broken pipe"))
                ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendMultiline(ProxiedPlayer player, String message) {
        List<String> lines = Util.splitIntoChunks(message, 119);
        for (String line : lines)
            player.sendMessage(line);
    }
}
