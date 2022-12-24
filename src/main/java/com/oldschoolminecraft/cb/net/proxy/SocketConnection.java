package com.oldschoolminecraft.cb.net.proxy;

import java.net.Socket;

public interface SocketConnection
{
    void socketConnected(Socket socket, ProxyBridgeThread handlerThread);
}
