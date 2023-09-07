package com.skle.creepercataclysm;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import com.skle.creepercataclysm.commands.LeaveCommand;
import com.skle.creepercataclysm.commands.PlayCommand;
import com.skle.creepercataclysm.commands.QueueCommand;
import com.skle.creepercataclysm.commands.debug.*;
import com.skle.creepercataclysm.listeners.*;
import com.skle.creepercataclysm.managers.GameManager;
import com.skle.creepercataclysm.managers.GoldManager;
import com.skle.creepercataclysm.managers.QueueManager;
import com.skle.creepercataclysm.managers.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

//TODO: config file, add difference between shop display and given item (and name and lore obv), add shop items, custom enchants

public final class CreeperCataclysm extends JavaPlugin implements CreeperCataclysmPlugin {

    private final GameManager gameManager = new GameManager(this);
    private final QueueManager queueManager = new QueueManager(this);
    private final GoldManager goldManager = new GoldManager(this);
    private final ShopManager shopManager = new ShopManager(this);

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
        getCommand("forcestart").setExecutor(new ForceStart(  this));
        getCommand("addgold").setExecutor(new AddGold(  this));

        // Register listeners
        Bukkit.getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ItemListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EntityInteractListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        
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
    public GoldManager getGoldManager() { return goldManager;}

    @Override
    public ShopManager getShopManager() { return shopManager; }

    @Override
    public int getMaxPlayers() {
        return MAX_PLAYERS;
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        this.MAX_PLAYERS = maxPlayers;
    }
}