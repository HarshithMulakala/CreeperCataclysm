package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {
    private final CreeperCataclysmPlugin plugin;

    public EntityDeathListener(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onMobDeath(EntityDeathEvent event){
        if(event.getEntity().getType() != EntityType.CREEPER) return;
        event.getDrops().clear();
    }
}
