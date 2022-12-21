package com.oldschoolminecraft.cb;

import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatEvent;

public class BungeePlugin extends Plugin implements Listener
{
    @Override
    public void onEnable()
    {
        getProxy().getPluginManager().registerListener(this, this);
    }

//    public void onChat(ChatEvent event)
//    {
//        getProxy().getServers().values().forEach(server ->
//                server.getPlayers().forEach(player ->
//                        player.sendMessage(event.getMessage())));
//    }
    @EventHandler
    public void onMessageSend(PlayerChatEvent event){
        getProxy().getServers().values().forEach(server ->
                server.getPlayers().forEach(player ->
                        player.sendMessage(event.getPlayer().getName()+event.getMessage())));
    }

    @Override
    public void onDisable()
    {
        //
    }
}
