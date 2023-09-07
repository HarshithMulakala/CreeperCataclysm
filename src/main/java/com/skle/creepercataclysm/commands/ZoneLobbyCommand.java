package com.skle.creepercataclysm.commands;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZoneLobbyCommand implements CommandExecutor {
    private final CreeperCataclysmPlugin plugin;

    public ZoneLobbyCommand(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }
        player.sendMessage(ChatColor.AQUA + "You have been given a lobby zoning stick! Click when you are standing in the correct position.");
        player.getInventory().addItem(plugin.getZoneManager().getLobbyZoneWand());
        return true;
    }
}
