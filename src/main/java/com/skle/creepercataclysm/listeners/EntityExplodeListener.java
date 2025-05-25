package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.util.Vector;

import java.util.Random;

public class EntityExplodeListener implements Listener {
    private final CreeperCataclysmPlugin plugin;

    public EntityExplodeListener(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void ExplosionPrime(ExplosionPrimeEvent event) {
        if(event.getEntity() instanceof Creeper creeper) {
            for (Entity entity : creeper.getNearbyEntities(100, 100, 100)) {
                if(entity instanceof Player player) {
                    Vector direction = player.getEyeLocation().toVector().subtract(creeper.getLocation().toVector());
                    double launchStrength = 20;
                    player.setVelocity(direction.normalize().multiply(launchStrength));
                }
            }
        }
        if(!(event.getEntity() instanceof Fireball fireball)) return;
        if(!(plugin.getGameManager().isGameStarted())) return;
        for (Entity entity : fireball.getNearbyEntities(fireball.getYield(), fireball.getYield(), fireball.getYield())) {
            if(entity instanceof Player player) {
                Vector direction = player.getLocation().toVector().subtract(fireball.getLocation().toVector());
                double launchStrength = 1.4;
                player.setVelocity(direction.normalize().multiply(launchStrength));
            }
        }
    }

    @EventHandler
    public void EntityExplode(EntityExplodeEvent event){
        if(event.getEntity() instanceof TNTPrimed){
            event.blockList().clear();
        }
        if(event.getEntity() instanceof Creeper creeper) {
            event.blockList().clear();
            for (Player player : plugin.getGameManager().getPlayers()) {
                var list = creeper.getNearbyEntities(100, 100, 100);
                player.sendMessage("Nearby: ");
                player.sendMessage(list.toString());
            }
            for (Entity entity : creeper.getNearbyEntities(100, 100, 100)) {
                if(entity instanceof Player player) {
                    player.sendMessage(ChatColor.GREEN + "YOU ARE BEING EXPLODED BY THE CREEPER!!!!");
                    Vector direction = player.getLocation().toVector().subtract(creeper.getLocation().toVector());
                    double launchStrength = 100;
                    player.setVelocity(direction.normalize().multiply(launchStrength));
                }
            }
        }
        if(!(event.getEntity() instanceof Fireball fireball)) return;
        event.blockList().clear();
        if(!(plugin.getGameManager().isGameStarted())) return;
        for (Entity entity : fireball.getNearbyEntities(fireball.getYield(), fireball.getYield(), fireball.getYield())) {
            if(entity instanceof Player player) {
                Vector direction = player.getLocation().toVector().subtract(fireball.getLocation().toVector());
                double launchStrength = 1.4;
                player.setVelocity(direction.normalize().multiply(launchStrength));
            }
        }
    }

    @EventHandler
    public void BlockChangedEvent(EntityChangeBlockEvent event) {
        if(!(plugin.getGameManager().isGameStarted())) return;
        if(event.getBlock().getType() == Material.POWDER_SNOW && event.getTo() == Material.AIR) {
            event.setCancelled(true);
        }
        if(event.getBlock().getType() == Material.ITEM_FRAME && event.getTo() == Material.AIR) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void noFireballFire(BlockIgniteEvent event){
        if(event.getCause().equals(BlockIgniteEvent.IgniteCause.FIREBALL)) event.setCancelled(true);
    }
}
