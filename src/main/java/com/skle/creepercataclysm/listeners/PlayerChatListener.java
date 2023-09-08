package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerChatListener implements Listener {
    private final CreeperCataclysmPlugin plugin;

    public PlayerChatListener(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void PlayerChat(AsyncPlayerChatEvent event){
        if(!(plugin.getZoneManager().isChoosingName(event.getPlayer()))) return;
        plugin.getZoneManager().setMapName(event.getPlayer(), event.getMessage());
        event.setCancelled(true);
    }
}
