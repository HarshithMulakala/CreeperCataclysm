package com.skle.creepercataclysm.commands;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayCommand implements CommandExecutor {
    private final CreeperCataclysmPlugin plugin;

    public PlayCommand(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }
        if(args.length == 0) {
            plugin.getQueueManager().addToQueue(player);
        }
        else{
            plugin.getQueueManager().addToQueue(Bukkit.getPlayer(args[0]));
        }

        return true;
    }
}
