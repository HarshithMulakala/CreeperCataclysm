package com.skle.creepercataclysm.commands.debug;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatueCommand implements CommandExecutor {
    private final CreeperCataclysmPlugin plugin;

    public StatueCommand(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }
        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "[DEBUG] Usage: /statue <red/white/blue>");
            return false;
        }

        if(args[0].equalsIgnoreCase("red")) {
            plugin.getStatueManager().statueToRed();
        }
        else if(args[0].equalsIgnoreCase("white")) {
            plugin.getStatueManager().statueToWhite();
        }
        else if(args[0].equalsIgnoreCase("blue")) {
            plugin.getStatueManager().statueToBlue();
        }
        else {
            sender.sendMessage(ChatColor.RED + "[DEBUG] Usage: /statue <red/white/blue>");
            return false;
        }
        sender.sendMessage(ChatColor.RED + "[DEBUG] STATUES SET TO " + args[0].toUpperCase());
        return true;
    }
}
