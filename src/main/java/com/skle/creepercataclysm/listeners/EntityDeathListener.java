package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDeathListener implements Listener {
    private final CreeperCataclysmPlugin plugin;

    public EntityDeathListener(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(!plugin.getGameManager().isGameStarted()) return;
        if(event.getEntity() == plugin.getGameManager().getCreeper()) {
            event.getDrops().clear(); // ending game is handled elsewhere
            return;
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(!plugin.getGameManager().isGameStarted()) return;
        EntityDamageEvent e = event.getEntity().getLastDamageCause();
        if(e == null) return;
        if(!(e instanceof EntityDamageByEntityEvent damageEvent)) return;
        Object damager = damageEvent.getDamager();
        if(damager instanceof Arrow arrow) damager = arrow.getShooter();
        if(!(damager instanceof Player attacker)) return;
        Player victim = event.getEntity();
        if(!(plugin.getGameManager().getPlayers().contains(attacker) && plugin.getGameManager().getPlayers().contains(victim))) return;
        plugin.getGoldManager().addGold(attacker, 1);

        //Set the victim's steak to 8 and arrows to 5
        ItemStack steak = new ItemStack(Material.COOKED_BEEF, 8);
        ItemStack arrows = new ItemStack(Material.ARROW, 5);
        for(ItemStack item : victim.getInventory().getContents()){
            if(item == null) continue;
            if(item.getType() == Material.COOKED_BEEF) {
                steak.setAmount(steak.getAmount() - item.getAmount());
            }
            else if(item.getType() == Material.ARROW) {
                arrows.setAmount(arrows.getAmount() - item.getAmount());
            }
        }
        victim.getInventory().addItem(steak);
        victim.getInventory().addItem(arrows);
    }
}
