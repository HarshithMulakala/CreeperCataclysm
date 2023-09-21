package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {
    private final CreeperCataclysmPlugin plugin;

    private InventoryAction[] actions = {
        InventoryAction.HOTBAR_MOVE_AND_READD,
        InventoryAction.HOTBAR_SWAP,
        InventoryAction.MOVE_TO_OTHER_INVENTORY,
        InventoryAction.PICKUP_ALL,
        InventoryAction.PICKUP_HALF,
        InventoryAction.PICKUP_ONE,
        InventoryAction.PICKUP_SOME,
        InventoryAction.PLACE_ALL,
        InventoryAction.PLACE_ONE,
        InventoryAction.PLACE_SOME,
        InventoryAction.SWAP_WITH_CURSOR
    };

    public InventoryListener(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void InventoryAction(InventoryClickEvent event){
        if(!(plugin.getGameManager().isGameStarted())) return;
        if(!(event.getWhoClicked() instanceof Player player)) return;
        if(!(plugin.getGameManager().getPlayers().contains(player))) return;
        Inventory actionInventory = event.getClickedInventory();
        if(actionInventory == null) return;
        if(!(actionInventory.equals(plugin.getShopManager().getDefenderShop())) && !(actionInventory.equals(plugin.getShopManager().getAttackerShop()))) return;
        for(InventoryAction action : actions) {
            if(event.getAction().equals(action)) {
                event.setCancelled(true);
                plugin.getShopManager().attemptBuy(player, actionInventory, event.getSlot());
                return;
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getType().equals(Material.GRAY_CONCRETE) && event.getItemInHand().getItemMeta().getDisplayName().equals("Healing Station Block")) {
            event.getBlock().setType(Material.LIME_CONCRETE);
            plugin.getGameManager().getSpecialBlocks().put(event.getBlock(), true);
        }
    }

    @EventHandler
    public void onBlockDestroy(BlockBreakEvent event){
        if(plugin.getGameManager().getSpecialBlocks().containsKey(event.getBlock())){
            plugin.getGameManager().getSpecialBlocks().remove(event.getBlock());
        }
    }
}
