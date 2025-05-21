package com.skle.creepercataclysm.api;

import com.skle.creepercataclysm.managers.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public interface CreeperCataclysmPlugin extends Plugin {
    GameManager getGameManager();
    QueueManager getQueueManager();
    GoldManager getGoldManager();
    ShopManager getShopManager();
    ZoneManager getZoneManager();
    StatueManager getStatueManager();

    FileConfiguration getPluginConfig();
    void reloadPluginConfig();
    void reloadConfigFromDisk();

    int getMaxPlayers();
    void setMaxPlayers(int maxPlayers);

}
