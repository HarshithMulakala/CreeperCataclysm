package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemListener implements Listener {
    private final CreeperCataclysmPlugin plugin;

    public ItemListener(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void ItemDropped(PlayerDropItemEvent event){
        if(!(plugin.getGameManager().isGameStarted())) return;
        if(!(plugin.getGameManager().getPlayers().contains(event.getPlayer()))) return;
        event.setCancelled(true);
    }
}
