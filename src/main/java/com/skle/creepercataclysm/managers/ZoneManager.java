package com.skle.creepercataclysm.managers;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class ZoneManager {
    private final CreeperCataclysmPlugin plugin;
    private ItemStack lobbyZoneWand;
    private ItemStack mapZoneWand;
    private ItemStack grapplingHook;

    public ZoneManager(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
        zoningState = new HashMap<>();
        initWands();
    }

    private Map<Player, Integer> zoningState; // 0 - Choosing Name, 1 - Attacker spawn, 2 - Attacker Villager, 3 - Defender Spawn, 4 - Defender Villager, 5 - Creeper
    private Map<Player, String> zoningNames = new HashMap<>();

    public void initWands() {
        lobbyZoneWand = new ItemStack(Material.STICK);
        ItemMeta zoningStickMeta = lobbyZoneWand.getItemMeta();
        zoningStickMeta.setDisplayName(ChatColor.AQUA + "Lobby Zoning Stick");
        zoningStickMeta.addEnchant(Enchantment.UNBREAKING, 100, true);
        lobbyZoneWand.setItemMeta(zoningStickMeta);

        mapZoneWand = new ItemStack(Material.STICK);
        ItemMeta mapZoningStickMeta = mapZoneWand.getItemMeta();
        mapZoningStickMeta.setDisplayName(ChatColor.AQUA + "Map Zoning Stick");
        mapZoningStickMeta.addEnchant(Enchantment.UNBREAKING, 100, true);
        mapZoneWand.setItemMeta(mapZoningStickMeta);

        grapplingHook = new ItemStack(Material.FISHING_ROD);
        ItemMeta grapplingHookMeta = grapplingHook.getItemMeta();
        grapplingHookMeta.setDisplayName(ChatColor.AQUA + "Grappling Hook");
        grapplingHookMeta.addEnchant(Enchantment.UNBREAKING, 100, true);
        grapplingHook.setItemMeta(grapplingHookMeta);
    }

    public void setLobbyZone(Player player) {
        saveConfigLocation("lobby", player.getLocation());
        player.sendMessage(ChatColor.AQUA + "Lobby zone set to " + player.getLocation().getWorld().getName() + " at " + player.getLocation().getX() + ", " + player.getLocation().getY() + ", " + player.getLocation().getZ() + ".");
        reloadConfigs();
    }

    public boolean isChoosingName(Player player) {
        return zoningState.containsKey(player) && zoningState.get(player) == 0;
    }

    public void setMapName(Player player, String name) {
        if(isChoosingName(player)) {
            zoningNames.put(player, name);
            player.sendMessage(ChatColor.AQUA + "Map name set to " + name + ".");
            setMapZone(player);
        } else {
            player.sendMessage(ChatColor.RED + "You are not choosing a name!");
        }
    }

    public boolean isZoningMap(Player player) {
        return zoningState.containsKey(player);
    }

    public void cancelMapZone(Player player) {
        zoningState.remove(player);
        zoningNames.remove(player);
    }

    public void setMapZone(Player player) {
        int playerZoningState; // 0 - Choosing Name, 1 - Attacker spawn, 2 - Attacker Villager, 3 - Defender Spawn, 4 - Defender Villager, 5 - Creeper
        if(zoningState.containsKey(player)) {
            playerZoningState = zoningState.get(player);
        } else {
            playerZoningState = -1;
        }
        playerZoningState++;
        zoningState.put(player, playerZoningState);
        switch (playerZoningState) {
            case 0: // Choosing Name
                player.sendMessage(ChatColor.AQUA + "Please enter the name of the map.");
                break;
            case 1: // Choosing attacker spawn, should've chosen name
                if(!zoningNames.containsKey(player)) {
                    player.sendMessage(ChatColor.RED + "You have not chosen a name!");
                    zoningState.put(player, 0);
                    return;
                }
                player.sendMessage(ChatColor.AQUA + "Please click on the attacker spawn.");
                break;
            case 2: // Choosing attacker villager, chosen attacker spawn
                saveConfigLocation("maps." + zoningNames.get(player) + ".attackerspawn", player.getLocation());
                player.sendMessage(ChatColor.AQUA + "Please click on the attacker villager.");
                break;
            case 3: // Choosing defender spawn, chosen attacker villager
                saveConfigLocation("maps." + zoningNames.get(player) + ".attackervillager", player.getLocation());
                player.sendMessage(ChatColor.AQUA + "Please click on the defender spawn.");
                break;
            case 4: // Choosing defender villager, chosen defender spawn
                saveConfigLocation("maps." + zoningNames.get(player) + ".defenderspawn", player.getLocation());
                player.sendMessage(ChatColor.AQUA + "Please click on the defender villager.");
                break;
            case 5: // Choosing creeper, chosen defender villager
                saveConfigLocation("maps." + zoningNames.get(player) + ".defendervillager", player.getLocation());
                player.sendMessage(ChatColor.AQUA + "Please click on the creeper.");
                break;
            case 6: // Chosen creeper
                saveConfigLocation("maps." + zoningNames.get(player) + ".creeper", player.getLocation());
                player.sendMessage(ChatColor.AQUA + "Map " + zoningNames.get(player) + " successfuly created!");
                zoningState.remove(player);
                zoningNames.remove(player);
                reloadConfigs();
                break;
        }

    }

    public void saveConfigLocation(String key, Location loc) {
        Bukkit.getLogger().info("saving config with key: " + key);
        plugin.getConfig().set(key + ".world", loc.getWorld().getName());
        plugin.getConfig().set(key + ".x", loc.getX());
        plugin.getConfig().set(key + ".y", loc.getY());
        plugin.getConfig().set(key + ".z", loc.getZ());
        plugin.getConfig().set(key + ".yaw", loc.getYaw());
        plugin.getConfig().set(key + ".pitch", loc.getPitch());
    }

    public ItemStack getLobbyZoneWand() {
        return lobbyZoneWand;
    }

    public ItemStack getMapZoneWand() {
        return mapZoneWand;
    }

    public ItemStack getGrapplingHook() {return grapplingHook; }

    public void reloadConfigs() {
        plugin.reloadPluginConfig();
    }
}
