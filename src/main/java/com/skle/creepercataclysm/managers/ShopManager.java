package com.skle.creepercataclysm.managers;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Dictionary;
import java.util.Map;

public class ShopManager {
    private final CreeperCataclysmPlugin plugin;
    private final Inventory defenderShop;
    private final Inventory attackerShop;
    private final Location defenderShopLocation = new Location(Bukkit.getWorlds().get(0), 21.5, -59, 83.5, -90, 0);
    private final Location attackerShopLocation = new Location(Bukkit.getWorlds().get(0), 21.5, -59, 57.5, -90, 0);
    private Villager defenderVillager;
    private Villager attackerVillager;

    private final ShopItem[] defenderShopItems = {
            new ShopItem(new ItemStack(Material.STONE_SWORD),
                    5, 0,
                    new Material[]{ // Items to override
                            Material.WOODEN_SWORD
            }),
            new ShopItem(new ItemStack(Material.IRON_SWORD),
                    10, 1,
                    new Material[]{ // Items to override
                        Material.WOODEN_SWORD,
                        Material.STONE_SWORD
            })
    };

    private final ShopItem[] attackerShopItems = {
            new ShopItem(new ItemStack(Material.DIAMOND_SWORD), 5, 0),
            new ShopItem(new ItemStack(Material.IRON_SWORD), 10, 1)};

    private static class ShopItem {
        public ItemStack item;
        public int cost;
        public int slot;
        public Material[] itemToReplace;

        public ShopItem(ItemStack item, int cost, int slot) {
            this.item = item;
            this.cost = cost;
            this.slot = slot;
            itemToReplace = null;
        }

        public ShopItem(ItemStack item, int cost, int slot, Material[] itemToReplace) {
            this.item = item;
            this.cost = cost;
            this.slot = slot;
            this.itemToReplace = itemToReplace;
        }
    }

    public ShopManager(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;

        defenderShop = plugin.getServer().createInventory(null, 9, ChatColor.BLUE + "Defender Shop");
        for(ShopItem item : defenderShopItems) {
            defenderShop.setItem(item.slot, item.item);
        }

        attackerShop = plugin.getServer().createInventory(null, 9, ChatColor.RED + "Attacker Shop");
        for(ShopItem item : attackerShopItems) {
            attackerShop.setItem(item.slot, item.item);
        }
    }

    public void initShop() {
        defenderVillager = defenderShopLocation.getWorld().spawn(defenderShopLocation, Villager.class);
        defenderVillager.setAI(false);
        defenderVillager.setInvulnerable(true);

        attackerVillager = attackerShopLocation.getWorld().spawn(attackerShopLocation, Villager.class);
        attackerVillager.setAI(false);
        attackerVillager.setInvulnerable(true);
    }

    public void attemptBuy(Player player, Inventory inventory, int slot) {
        ShopItem[] shopItems = inventory == defenderShop ? defenderShopItems : attackerShopItems;
        ShopItem item = null;
        for(ShopItem shopItem : shopItems) {
            if(shopItem.slot == slot) {
                item = shopItem;
                break;
            }
        }
        if(item == null) { return; }
        if (plugin.getGoldManager().getGoldInInventory(player) >= item.cost) {
            plugin.getGoldManager().removeGold(player, item.cost);
            if(item.itemToReplace != null) {
                for(Material material : item.itemToReplace) {
                    player.getInventory().remove(material);
                }
            }
            player.getInventory().addItem(item.item);
            player.sendMessage(ChatColor.GREEN + "You bought " + item.item.getType().toString() + " for " + item.cost + " gold!");
        }
        else {
            player.sendMessage(ChatColor.RED + "You don't have enough gold to buy " + item.item.getType().toString() + "! You need " + (item.cost - plugin.getGoldManager().getGoldInInventory(player)) + " more gold!");
        }
    }

    public void resetShop() {
        defenderVillager.remove();
        attackerVillager.remove();
    }

    public Villager getDefenderVillager() {
        return defenderVillager;
    }

    public Villager getAttackerVillager() {
        return attackerVillager;
    }

    public Inventory getDefenderShop() {
        return defenderShop;
    }

    public Inventory getAttackerShop() {
        return attackerShop;
    }
}
