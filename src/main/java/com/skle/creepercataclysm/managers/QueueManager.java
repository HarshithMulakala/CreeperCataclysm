package com.skle.creepercataclysm.managers;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QueueManager {
    private List<Player> queue;
    private final CreeperCataclysmPlugin plugin;

    public QueueManager(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
        this.queue = new ArrayList<>();
    }

    public void addToQueue(Player player) {
        if (queue.size() >= plugin.getMaxPlayers()) {
            player.sendMessage(ChatColor.RED + "The queue is full!");
            return;
        }
        queue.add(player);
        for (Player p : queue) {
            p.sendMessage(ChatColor.AQUA + player.getName() + " has joined the queue! (" + queue.size() + "/" + plugin.getMaxPlayers() + ")");
        }

        if(queue.size() == plugin.getMaxPlayers()) {
            plugin.getGameManager().startGame();
        }
    }

    public void removeFromQueue(Player player) {
        if(!queue.contains(player)) {
            player.sendMessage(ChatColor.RED + "You are not in the queue!");
            return;
        }
        for (Player p : queue) {
            p.sendMessage(ChatColor.AQUA + player.getName() + " has left the queue! (" + queue.size() + "/" + plugin.getMaxPlayers() + ")");
        }
        queue.remove(player);
    }

    public void notifyGameStart() {
        for (Player p : queue) {
            p.sendMessage(ChatColor.AQUA + "The game has started!");
        }
    }

    public void notifyGameEnd(int winner) { // 0 - defender, 1 - attacker
        for (Player p : queue) {
            p.sendMessage(ChatColor.AQUA + "The " + (winner == 0 ? "defenders" : "attackers") + " have won!");
        }
    }

    public List<Player> getQueue() {
        return queue;
    }

    public void resetQueue() {
        queue = new ArrayList<>();
    }
}
