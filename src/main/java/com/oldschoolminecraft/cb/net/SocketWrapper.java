package com.oldschoolminecraft.cb.net;

import com.oldschoolminecraft.cb.PLConfig;
import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class SocketWrapper
{
    private PLConfig config;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public SocketWrapper(PLConfig config, Socket socket, DataInputStream dis, DataOutputStream dos)
    {
        this.config = config;
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;
    }

    public void sendPluginMessage(String message)
    {
        try
        {
            JSONObject obj = new JSONObject();
            obj.put("secret", config.getStringOption("settings.chat.relaySecret"));
            obj.put("message", message);
            dos.writeUTF(obj.toJSONString() + "\n");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Socket getSocket()
    {
        return socket;
    }

    public DataInputStream getDis()
    {
        return dis;
    }

    public DataOutputStream getDos()
    {
        return dos;
    }
}
