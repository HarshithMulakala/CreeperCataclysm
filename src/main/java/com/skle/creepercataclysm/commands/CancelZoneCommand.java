package com.skle.creepercataclysm.commands;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelZoneCommand implements CommandExecutor {
    private final CreeperCataclysmPlugin plugin;

    public CancelZoneCommand(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }
        if(plugin.getZoneManager().isZoningMap(player)) {
            plugin.getZoneManager().cancelMapZone(player);
            player.sendMessage(ChatColor.AQUA + "Map zoning cancelled.");
        } else {
            player.sendMessage(ChatColor.RED + "You are not zoning a map.");
        }
        return true;
    }
}
