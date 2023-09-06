package com.skle.creepercataclysm;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import com.skle.creepercataclysm.commands.LeaveCommand;
import com.skle.creepercataclysm.commands.PlayCommand;
import com.skle.creepercataclysm.commands.QueueCommand;
import com.skle.creepercataclysm.commands.debug.Abort;
import com.skle.creepercataclysm.commands.debug.ForceStart;
import com.skle.creepercataclysm.commands.debug.SetPlayers;
import com.skle.creepercataclysm.commands.debug.SetTime;
import com.skle.creepercataclysm.listeners.EntityDamageListener;
import com.skle.creepercataclysm.listeners.EntityDeathListener;
import com.skle.creepercataclysm.managers.GameManager;
import com.skle.creepercataclysm.managers.QueueManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

//TODO: Add a shop system, swapteam debug (tentative), permissions, give arrows & steak back on death, and a config file

public final class CreeperCataclysm extends JavaPlugin implements CreeperCataclysmPlugin {

    private final GameManager gameManager = new GameManager(this);
    private final QueueManager queueManager = new QueueManager(this);
    private int MAX_PLAYERS = 2;
    @Override
    public void onEnable() {
        // Plugin startup logic
        // Register commands
        getCommand("play").setExecutor(new PlayCommand(this));
        getCommand("queue").setExecutor(new QueueCommand(this));
        getCommand("leave").setExecutor(new LeaveCommand(this));
        getCommand("setplayers").setExecutor(new SetPlayers(this));
        getCommand("settime").setExecutor(new SetTime(this));
        getCommand("abort").setExecutor(new Abort(this));
        getCommand("forcestart").setExecutor(new ForceStart(this));

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

    @Override
    public int getMaxPlayers() {
        return MAX_PLAYERS;
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        this.MAX_PLAYERS = maxPlayers;
    }
}