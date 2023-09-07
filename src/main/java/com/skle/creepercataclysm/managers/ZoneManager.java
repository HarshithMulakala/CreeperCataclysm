package com.skle.creepercataclysm.managers;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ZoneManager {
    private final CreeperCataclysmPlugin plugin;
    private ItemStack lobbyZoneWand;
    private ItemStack mapZoneWand;

    public ZoneManager(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
        initWands();
    }

    public void initWands() {
        lobbyZoneWand = new ItemStack(Material.STICK);
        ItemMeta zoningStickMeta = lobbyZoneWand.getItemMeta();
        zoningStickMeta.setDisplayName(ChatColor.AQUA + "Lobby Zoning Stick");
        zoningStickMeta.addEnchant(Enchantment.DURABILITY, 100, true);
        lobbyZoneWand.setItemMeta(zoningStickMeta);

        mapZoneWand = new ItemStack(Material.STICK);
        ItemMeta mapZoningStickMeta = mapZoneWand.getItemMeta();
        mapZoningStickMeta.setDisplayName(ChatColor.AQUA + "Map Zoning Stick");
        mapZoningStickMeta.addEnchant(Enchantment.DURABILITY, 100, true);
        mapZoneWand.setItemMeta(mapZoningStickMeta);
    }

    public void setLobbyZone(Player player) {
        plugin.getConfig().set("lobby.world", player.getLocation().getWorld().getName());
        plugin.getConfig().set("lobby.x", player.getLocation().getX());
        plugin.getConfig().set("lobby.y", player.getLocation().getY());
        plugin.getConfig().set("lobby.z", player.getLocation().getZ());
        plugin.getConfig().set("lobby.yaw", player.getLocation().getYaw());
        plugin.getConfig().set("lobby.pitch", player.getLocation().getPitch());
        player.sendMessage(ChatColor.AQUA + "Lobby zone set to " + player.getLocation().getWorld().getName() + " at " + player.getLocation().getX() + ", " + player.getLocation().getY() + ", " + player.getLocation().getZ() + ".");
        reloadConfigs();
    }

    public ItemStack getLobbyZoneWand() {
        return lobbyZoneWand;
    }

    public ItemStack getMapZoneWand() {
        return mapZoneWand;
    }

    public void reloadConfigs() {
        plugin.reloadPluginConfig();
    }
}
