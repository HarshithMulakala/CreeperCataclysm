package com.skle.creepercataclysm.commands;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QueueCommand implements CommandExecutor {
    private final CreeperCataclysmPlugin plugin;

    public QueueCommand(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }
        String print = ChatColor.AQUA + "Current players in queue: ";
        for(Player p : plugin.getQueueManager().getQueue()) {
            print += p.getName() + ", ";
        }
        print = print.substring(0, print.length() - 2);
        player.sendMessage(print);
        return true;
    }
}
