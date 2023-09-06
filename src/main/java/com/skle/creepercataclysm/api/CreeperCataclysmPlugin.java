package com.skle.creepercataclysm.api;

import com.skle.creepercataclysm.managers.GameManager;
import com.skle.creepercataclysm.managers.QueueManager;
import org.bukkit.plugin.Plugin;

public interface CreeperCataclysmPlugin extends Plugin {
    int MAX_PLAYERS = 2;

    GameManager getGameManager();
    QueueManager getQueueManager();
}
