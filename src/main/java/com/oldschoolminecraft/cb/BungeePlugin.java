package com.oldschoolminecraft.cb;

import com.google.common.eventbus.Subscribe;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin implements Listener
{
    @Override
    public void onEnable()
    {
        getProxy().getPluginManager().registerListener(this, this);
    }

    @Subscribe
    public void onChat(ChatEvent event)
    {
        getProxy().getServers().values().forEach(server ->
                server.getPlayers().forEach(player ->
                        player.sendMessage(event.getMessage())));
    }

    @Override
    public void onDisable()
    {
        //
    }
}
