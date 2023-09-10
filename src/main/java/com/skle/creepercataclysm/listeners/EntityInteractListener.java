package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class EntityInteractListener implements Listener {
    private final CreeperCataclysmPlugin plugin;

    public EntityInteractListener(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void PlayerInteractWithEntity(PlayerInteractEntityEvent event){
        if(!(plugin.getGameManager().isGameStarted())) return;
        if(!(event.getRightClicked() instanceof Villager villager)) return;
        if(villager.equals(plugin.getShopManager().getDefenderVillager())) {
            event.getPlayer().openInventory(plugin.getShopManager().getDefenderShop());
        }
        else if(villager.equals(plugin.getShopManager().getAttackerVillager())) {
            event.getPlayer().openInventory(plugin.getShopManager().getAttackerShop());
        }
        else return;
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {
        //See if the player is zoning
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getItem() == null) return;
            Player player = event.getPlayer();
            if(event.getItem().equals(plugin.getZoneManager().getLobbyZoneWand())) {
                plugin.getZoneManager().setLobbyZone(player);
            }
            else if (event.getItem().equals(plugin.getZoneManager().getMapZoneWand())) {
                plugin.getZoneManager().setMapZone(player);
            }
            else if(plugin.getGameManager().isGameStarted() && plugin.getGameManager().getPlayers().contains(player) && event.getItem().getType().equals(Material.FIRE_CHARGE)) {
                Fireball fireball = player.launchProjectile(Fireball.class);
                fireball.setYield(2.5f);
                Vector direction = player.getLocation().getDirection();
                fireball.setVelocity(direction.multiply(1.75));
                fireball.setIsIncendiary(false);
                event.getItem().setAmount(event.getItem().getAmount() - 1);
            }
         }
    }
}
