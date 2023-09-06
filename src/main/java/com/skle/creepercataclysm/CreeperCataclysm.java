package com.skle.creepercataclysm;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import com.skle.creepercataclysm.commands.LeaveCommand;
import com.skle.creepercataclysm.commands.PlayCommand;
import com.skle.creepercataclysm.commands.QueueCommand;
import com.skle.creepercataclysm.listeners.EntityDamageListener;
import com.skle.creepercataclysm.listeners.EntityDeathListener;
import com.skle.creepercataclysm.managers.GameManager;
import com.skle.creepercataclysm.managers.QueueManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CreeperCataclysm extends JavaPlugin implements CreeperCataclysmPlugin {

    private final GameManager gameManager = new GameManager(this);
    private final QueueManager queueManager = new QueueManager(this);
    @Override
    public void onEnable() {
        // Plugin startup logic
        // Register commands
        getCommand("play").setExecutor(new PlayCommand(this));
        getCommand("queue").setExecutor(new QueueCommand(this));
        getCommand("leave").setExecutor(new LeaveCommand(this));

        // Register listeners
        Bukkit.getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        
    }

    @Override
    public void onDisable() {
        if(gameManager.isGameStarted()) {
            gameManager.endGame(0);
        }
    }

    @Override
    public GameManager getGameManager() {
        return gameManager;
    }

    @Override
    public QueueManager getQueueManager() {
        return queueManager;
    }
}