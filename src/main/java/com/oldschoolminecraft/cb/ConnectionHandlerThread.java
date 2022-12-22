package com.oldschoolminecraft.cb;

import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandlerThread extends Thread
{
    private ServerSocket serverSocket;

    public ConnectionHandlerThread(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    public void run()
    {
        try
        {
            while (!serverSocket.isClosed())
            {
                Socket socket = serverSocket.accept();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
