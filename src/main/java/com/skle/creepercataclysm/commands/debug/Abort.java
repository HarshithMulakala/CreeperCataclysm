package com.skle.creepercataclysm.commands.debug;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Abort implements CommandExecutor {
    private final CreeperCataclysmPlugin plugin;

    public Abort(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }
        plugin.getGameManager().endGame(0);
        sender.sendMessage(ChatColor.RED + "[DEBUG] GAME ABORTED");
        return true;
    }
}
