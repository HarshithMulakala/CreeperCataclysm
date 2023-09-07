package com.skle.creepercataclysm.api;

import com.skle.creepercataclysm.managers.GameManager;
import com.skle.creepercataclysm.managers.GoldManager;
import com.skle.creepercataclysm.managers.QueueManager;
import com.skle.creepercataclysm.managers.ShopManager;
import org.bukkit.plugin.Plugin;

public interface CreeperCataclysmPlugin extends Plugin {
    GameManager getGameManager();
    QueueManager getQueueManager();
    GoldManager getGoldManager();
    ShopManager getShopManager();

    int getMaxPlayers();
    void setMaxPlayers(int maxPlayers);
}
