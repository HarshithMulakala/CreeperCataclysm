package com.skle.creepercataclysm.commands.debug;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AbortCommand implements CommandExecutor {
    private final CreeperCataclysmPlugin plugin;

    public AbortCommand(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }
        if(!plugin.getGameManager().isGameStarted()) {
            sender.sendMessage(ChatColor.RED + "[DEBUG] GAME NOT STARTED");
            return true;
        }
        plugin.getGameManager().endGame(0);
        sender.sendMessage(ChatColor.RED + "[DEBUG] GAME ABORTED");
        return true;
    }
}
