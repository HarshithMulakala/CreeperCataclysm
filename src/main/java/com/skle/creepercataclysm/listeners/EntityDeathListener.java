package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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

import java.util.*;

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
    public void onPlayerDeathForSounds(PlayerDeathEvent event){
        Player attacker = event.getEntity().getKiller();
        Player victim = event.getEntity();
        if(attacker == null) return;
        if(attacker.equals(victim)) return;
        if(plugin.getGameManager().getPlayerKillMap().get(attacker) == null){
            plugin.getGameManager().getPlayerKillMap().put(attacker, 0);
        }
        plugin.getGameManager().getPlayerKillMap().put(attacker, plugin.getGameManager().getPlayerKillMap().get(attacker) + 1);

        if(plugin.getGameManager().getPlayerKillMap().get(attacker) == 1){
            Bukkit.getLogger().info("Kills: 1");
            attacker.playSound(attacker.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.26f);
        }
        else if (plugin.getGameManager().getPlayerKillMap().get(attacker) == 2){
            Bukkit.getLogger().info("Kills: 2");
            attacker.playSound(attacker.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.414f);
        }
        else if (plugin.getGameManager().getPlayerKillMap().get(attacker) == 3){
            Bukkit.getLogger().info("Kills: 3");
            attacker.playSound(attacker.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.498f);
        }
        else if (plugin.getGameManager().getPlayerKillMap().get(attacker) == 4){
            Bukkit.getLogger().info("Kills: 4");
            attacker.playSound(attacker.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.682f);
        }
        else if (plugin.getGameManager().getPlayerKillMap().get(attacker) >= 5){
            Bukkit.getLogger().info("Kills: 5");
            attacker.playSound(attacker.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.26f);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    attacker.playSound(attacker.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.414f);
                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            attacker.playSound(attacker.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.498f);
                            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    attacker.playSound(attacker.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.682f);
                                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            attacker.playSound(attacker.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.414f);
                                            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                                @Override
                                                public void run() {
                                                    attacker.playSound(attacker.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.122f);
                                                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            attacker.playSound(attacker.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.26f);
                                                        }
                                                    }, 3L);
                                                }
                                            }, 6L);
                                        }
                                    }, 3L);
                                }
                            }, 3L);
                        }
                    }, 3L);
                }
            }, 3L);





        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(!plugin.getGameManager().isGameStarted()) return;
        if(event.getEntity().getKiller() == null){
            HashMap<Player, Double> damageMap = plugin.getGameManager().getDamageMap().get(event.getEntity());
            if(damageMap == null || damageMap.isEmpty()) return;
            for (Map.Entry<Player, Double> entry : damageMap.entrySet()) {
                plugin.getGoldManager().addGoldNug(entry.getKey(), 1);
            }
            return;
        }
        Player attacker = event.getEntity().getKiller();
        Player victim = event.getEntity();
        if(attacker.equals(victim)){
            return;
        }
        if(!(plugin.getGameManager().getPlayers().contains(attacker) && plugin.getGameManager().getPlayers().contains(victim))) return;
        if(plugin.getGameManager().getDefenders().contains(attacker)){
            if(plugin.getGameManager().getKillMap().get(attacker) < plugin.getGameManager().getDefenderGoldStart() + 3){
                plugin.getGameManager().getKillMap().put(attacker, plugin.getGameManager().getKillMap().get(attacker) + 1);

            }
        }
        else{
            if(plugin.getGameManager().getKillMap().get(attacker) < plugin.getGameManager().getAttackerGoldStart() + 3){
                plugin.getGameManager().getKillMap().put(attacker, plugin.getGameManager().getKillMap().get(attacker) + 1);
            }
        }
        double health = attacker.getHealth() + 4 < 20 ? 4 : 20 - attacker.getHealth();
        attacker.setHealth(attacker.getHealth() + health);
        plugin.getGoldManager().addGold(attacker, plugin.getGameManager().getKillMap().get(attacker));
        plugin.getGameManager().getDamageMap().get(victim).remove(attacker);
        HashMap<Player, Double> damageMap = plugin.getGameManager().getDamageMap().get(event.getEntity());
        if(damageMap != null && !damageMap.isEmpty()){
            double max = Collections.max(plugin.getGameManager().getDamageMap().get(victim).values());
            Bukkit.getLogger().info("Second Damager: " + max);
            if(max >= 11.5){
                for (Map.Entry<Player, Double> entry : plugin.getGameManager().getDamageMap().get(victim).entrySet()) {
                    if (entry.getValue()==max) {
                        plugin.getGoldManager().addGoldNug(entry.getKey(), 1);
                    }
                }
            }
        }


    }
}
