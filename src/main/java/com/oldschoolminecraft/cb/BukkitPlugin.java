package com.oldschoolminecraft.cb;

import com.oldschoolminecraft.cb.event.PlayerHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
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

            loginRelay();




        } catch (Exception ex) {
            System.out.println("Chat bridge failed to connect to relay @ " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            System.out.println("Chat bridge has been disabled due to an error: " + ex.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            ex.printStackTrace();
        }
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
                sender.sendMessage(ChatColor.DARK_GRAY + "Host: " + ChatColor.GRAY + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                sender.sendMessage(ChatColor.BLUE + "Status: " + (socket.isConnected() ? ChatColor.GREEN + "Connected" : ChatColor.RED + "Disconnected"));
                return true;
            }

            if (args[0].equalsIgnoreCase("reload"))
            {
                logoutRelay();
                config.reload();
                loginRelay();
                sender.sendMessage("Chat bridge config reloaded");
                return true;
            }

            if (args[0].equalsIgnoreCase("disable"))
            {
                getServer().getPluginManager().disablePlugin(this);
                sender.sendMessage("Chat bridge disabled");
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
            logoutRelay();
            System.out.println("Chat bridge has disconnected from relay @ " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loginRelay()
    {
        try
        {
            if (socket == null || dis == null || dos == null)
            {
                socket = new Socket(config.getStringOption("settings.chat.relayHost"), (int) config.getConfigOption("settings.chat.relayPort"));
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
            } else {
                socket.connect(new InetSocketAddress(config.getStringOption("settings.chat.relayHost"), (int) config.getConfigOption("settings.chat.relayPort")));
            }
            System.out.println("Chat bridge connected to relay @ " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

            dos.writeUTF("LOGIN " + config.getStringOption("settings.chat.relaySecret"));

            String auth_response = dis.readUTF();
            if (auth_response.equals("LOGIN_SUCCESS"))
            {
                getServer().getScheduler().scheduleSyncRepeatingTask(this, () ->
                {
                    try
                    {
                        if (socket.isConnected()) dos.writeUTF("PING");
                    } catch (Exception ignored) {}
                }, 0,  1000); // 1000 ticks

                getServer().getPluginManager().registerEvents(new PlayerHandler(this, socket, dis, dos), this);
                System.out.println("Chat bridge authenticated with relay successfully");
            } else {
                System.out.println("Chat bridge failed to authenticate with relay");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void logoutRelay()
    {
        try
        {
            if (socket.isConnected())
            {
                dos.writeUTF("LOGOUT");
                socket.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
