package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.util.Vector;

public class EntityExplodeListener implements Listener {
    private final CreeperCataclysmPlugin plugin;

    public EntityExplodeListener(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void EntityExplode(EntityExplodeEvent event){
        if(!(plugin.getGameManager().isGameStarted())) return;
        if(!(event.getEntity() instanceof Fireball fireball)) return;
        event.blockList().clear();
        for (Entity entity : fireball.getNearbyEntities(fireball.getYield(), fireball.getYield(), fireball.getYield())) {
            if(entity instanceof Player player) {
                Vector direction = player.getLocation().toVector().subtract(fireball.getLocation().toVector());
                double launchStrength = 1.4;
                player.setVelocity(direction.normalize().multiply(launchStrength));
            }
        }
    }
}
