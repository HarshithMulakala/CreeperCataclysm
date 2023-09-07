package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

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
}
