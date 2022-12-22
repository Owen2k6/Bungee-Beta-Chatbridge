package com.oldschoolminecraft.cb.net.proxy;

import com.oldschoolminecraft.cb.BungeePlugin;

import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandlerThread extends Thread
{
    private BungeePlugin plugin;
    private ServerSocket serverSocket;

    public ConnectionHandlerThread(BungeePlugin plugin, ServerSocket serverSocket)
    {
        this.plugin = plugin;
        this.serverSocket = serverSocket;
    }

    public void run()
    {
        try
        {
            while (!serverSocket.isClosed())
            {
                Socket socket = serverSocket.accept();
                new ProxyBridgeThread(plugin, socket).start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
