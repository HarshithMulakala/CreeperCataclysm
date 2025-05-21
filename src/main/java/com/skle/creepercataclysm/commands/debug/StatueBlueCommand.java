package com.skle.creepercataclysm.commands.debug;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatueBlueCommand implements CommandExecutor {
    private final CreeperCataclysmPlugin plugin;

    public StatueBlueCommand(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }
        plugin.getStatueManager().statueToBlue();
        sender.sendMessage(ChatColor.RED + "[DEBUG] GAME PLAYERS SET TO " + args[0]);
        return true;
    }
}
