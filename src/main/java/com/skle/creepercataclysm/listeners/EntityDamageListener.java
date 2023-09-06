package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.Bukkit;
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
        if(event.getEntity().equals(plugin.getGameManager().getCreeper())) {
            if(event.getDamager() instanceof Player player) {
                if(plugin.getGameManager().getAttackers().contains(player)) return;
            }
            else if(event.getDamager() instanceof Arrow arrow) {
                if(!(arrow.getShooter() instanceof Player player)) return;
                if(!plugin.getGameManager().getDefenders().contains(player)) return;
            }
        }
        if(event.getEntity() instanceof Player attacked) {
            if(plugin.getGameManager().getDefenders().contains(attacked) && plugin.getGameManager().getAttackers().contains((Player)event.getDamager())) return;
            if(plugin.getGameManager().getAttackers().contains(attacked) && plugin.getGameManager().getDefenders().contains((Player)event.getDamager())) return;
        }
        event.setCancelled(true);
    }
}
