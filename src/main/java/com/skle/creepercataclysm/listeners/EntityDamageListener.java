package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDamageListener implements Listener {
    private final CreeperCataclysmPlugin plugin;

    public EntityDamageListener(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if(!plugin.getGameManager().isGameStarted()) return;

        if(event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }
        if(event.getEntity().equals(plugin.getGameManager().getCreeper())) {
            if(event.getDamager() instanceof Player player) {
                if(plugin.getGameManager().getAttackers().contains(player)) {
                    plugin.getGameManager().notifyCreeperHit();
                    return;
                }
            }
            else if(event.getDamager() instanceof Arrow arrow) {
                if(!(arrow.getShooter() instanceof Player player)) return;
                if(!plugin.getGameManager().getDefenders().contains(player)) return;
            }
        }

        if(event.getEntity() instanceof Player attacked) {
            Player attacker;
            if(event.getDamager() instanceof Player) {
                attacker = (Player) event.getDamager();
            }
            else if(event.getDamager() instanceof Arrow arrow) {
                if(!(arrow.getShooter() instanceof Player player)) return;
                attacker = player;
                if((!(plugin.getGameManager().getAttackers().contains(attacker) && plugin.getGameManager().getAttackers().contains(attacked))) && (!(plugin.getGameManager().getDefenders().contains(attacker) && plugin.getGameManager().getDefenders().contains(attacked))) ){
                    attacker.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                }

            }
            else if(event.getDamager() instanceof Fireball) {
                Bukkit.getLogger().info("Fireball damage");
                if(((Fireball) event.getDamager()).getShooter() == attacked) {
                    event.setDamage(1.0);
                }
                else {
                    event.setDamage(event.getDamage()/2.5);
                }
                return;
            }
            else return;
            if(plugin.getGameManager().getDefenders().contains(attacked) && plugin.getGameManager().getAttackers().contains(attacker)) return;
            if(plugin.getGameManager().getAttackers().contains(attacked) && plugin.getGameManager().getDefenders().contains(attacker)) return;

        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event)
    {
        Bukkit.getLogger().info("Projectile hit");
        Projectile p = event.getEntity();
        if(p instanceof Arrow) {
            p.remove();
        }
        if(p instanceof Fireball fireball) {
            Bukkit.getLogger().info("Fireball hit");
            Block blockhit = event.getHitBlock();
            if(blockhit != null && blockhit.getType() == Material.POWDER_SNOW) {
                event.setCancelled(true);
                Bukkit.getLogger().info("Fireball hit powder snow");
            }
        }
        return;
    }
}
