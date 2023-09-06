package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageListener implements Listener {
    private final CreeperCataclysmPlugin plugin;

    public EntityDamageListener(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if(!plugin.getGameManager().isGameStarted()) return;
        if(!event.getEntity().equals(plugin.getGameManager().getCreeper())) return;
        if(!plugin.getGameManager().getDefenders().contains(event.getDamager())) return;
        if(event.getDamager() instanceof Arrow arrow) {
            if(!(arrow.getShooter() instanceof Player player)) return;
            if(!plugin.getGameManager().getDefenders().contains(player)) return;
        }
        event.setCancelled(true);
    }
}
