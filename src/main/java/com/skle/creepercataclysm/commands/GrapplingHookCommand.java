package com.skle.creepercataclysm.commands;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrapplingHookCommand implements CommandExecutor {
    private final CreeperCataclysmPlugin plugin;

    public GrapplingHookCommand(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }
        player.sendMessage(ChatColor.AQUA + "You have been given a grappling hook!");
        player.getInventory().addItem(plugin.getZoneManager().getGrapplingHook());
        return true;
    }
}