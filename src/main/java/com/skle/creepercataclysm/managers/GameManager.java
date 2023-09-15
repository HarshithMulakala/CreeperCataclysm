package com.skle.creepercataclysm.managers;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameManager {
    private final CreeperCataclysmPlugin plugin;

//    private final Location defenderSpawn = new Location(Bukkit.getWorlds().get(0), 24.5, -59, 83.5, 180, 0);
//    private final Location attackerSpawn = new Location(Bukkit.getWorlds().get(0), 24.5, -59, 57.5, 0, 0);
//    private final Location creeperSpawn = new Location(Bukkit.getWorlds().get(0), 24.5, -59, 70.5, -90, 0);
    private Location lobbySpawn;

    private boolean gameStarted = false;

    private List<Player> players = new ArrayList<>();
    private List<Player> defenders = new ArrayList<>();
    private List<Player> attackers = new ArrayList<>();

    private Creeper creeper;
    private BossBar bossBar;

    private int timeLeft;
    private List<GameMap> maps;
    private GameMap currentMap;

    public GameManager(CreeperCataclysmPlugin plugin){
        this.plugin = plugin;
        initConfig();
    }

    public class GameMap {
        public final String name;
        public final Location attackerspawn;
        public final Location attackervillagerspawn;
        public final Location defenderspawn;
        public final Location defendervillagerspawn;
        public final Location creeperspawn;

        public GameMap(String name, Location attackerspawn, Location attackervillager, Location defenderspawn, Location defendervillager, Location creeper) {
            this.name = name;
            this.attackerspawn = attackerspawn;
            this.attackervillagerspawn = attackervillager;
            this.defenderspawn = defenderspawn;
            this.defendervillagerspawn = defendervillager;
            this.creeperspawn = creeper;
        }
    }

    public void initConfig() {
        maps = new ArrayList<>();
        FileConfiguration config = plugin.getPluginConfig();
        ConfigurationSection lobby = config.getConfigurationSection("lobby");
        ConfigurationSection maps = config.getConfigurationSection("maps");
        if(maps == null || lobby == null) {
            Bukkit.getLogger().severe("No maps found in config!");
            Bukkit.getLogger().severe("No maps found in config!");
            Bukkit.getLogger().severe("No maps found in config!");
            return;
        }
        lobbySpawn = new Location(Bukkit.getWorld(lobby.getString("world")), lobby.getDouble("x"), lobby.getDouble("y"), lobby.getDouble("z"), (float)lobby.getDouble("yaw"), (float)lobby.getDouble("pitch"));
        Bukkit.getLogger().info("Lobby spawn set to " + lobbySpawn.toString());
        for(String mapKey : maps.getKeys(false)) { // HOLY SHIT THIS IS AWFUL PLEASE PLEASE PLEASE FIGURE OUT A BETTER WAY
            ConfigurationSection mapData = maps.getConfigurationSection(mapKey);
            String mapName = mapKey;
            Location attackerspawn = new Location(Bukkit.getWorld(mapData.getString("attackerspawn.world")), mapData.getDouble("attackerspawn.x"), mapData.getDouble("attackerspawn.y"), mapData.getDouble("attackerspawn.z"), (float)mapData.getDouble("attackerspawn.yaw"), (float)mapData.getDouble("attackerspawn.pitch"));
            Location attackervillager = new Location(Bukkit.getWorld(mapData.getString("attackervillager.world")), mapData.getDouble("attackervillager.x"), mapData.getDouble("attackervillager.y"), mapData.getDouble("attackervillager.z"), (float)mapData.getDouble("attackervillager.yaw"), (float)mapData.getDouble("attackervillager.pitch"));
            Location defenderspawn = new Location(Bukkit.getWorld(mapData.getString("defenderspawn.world")), mapData.getDouble("defenderspawn.x"), mapData.getDouble("defenderspawn.y"), mapData.getDouble("defenderspawn.z"), (float)mapData.getDouble("defenderspawn.yaw"), (float)mapData.getDouble("defenderspawn.pitch"));
            Location defendervillager = new Location(Bukkit.getWorld(mapData.getString("defendervillager.world")), mapData.getDouble("defendervillager.x"), mapData.getDouble("defendervillager.y"), mapData.getDouble("defendervillager.z"), (float)mapData.getDouble("defendervillager.yaw"), (float)mapData.getDouble("defendervillager.pitch"));
            Location creeper = new Location(Bukkit.getWorld(mapData.getString("creeper.world")), mapData.getDouble("creeper.x"), mapData.getDouble("creeper.y"), mapData.getDouble("creeper.z"), (float)mapData.getDouble("creeper.yaw"), (float)mapData.getDouble("creeper.pitch"));
            GameMap gameMap = new GameMap(mapName, attackerspawn, attackervillager, defenderspawn, defendervillager, creeper);
            this.maps.add(gameMap);
        }
    }

    public void startGame() {
        Bukkit.getLogger().info("Amount of maps: " + maps.size());
        if(maps.size() <= 0) {
            plugin.getQueueManager().getQueue().forEach(player -> player.sendMessage(ChatColor.RED + "No maps found in config!"));
            Bukkit.getLogger().severe("No maps found in config!");
            return;
        }
        currentMap = maps.get(new Random().nextInt(maps.size()));
        Bukkit.getLogger().info("Game has begun with map " + currentMap.name + "!");
        timeLeft = 130;
        this.gameStarted = true;
        initPlayers();
        initBossBar();
        initTimer();
        initCreeper();
        initGold();
        initShop();
    }

    private void initGold() {
        plugin.getGoldManager().initGame();
    }

    private void initShop() {
        plugin.getShopManager().initShop();
    }

    private void initCreeper() {
        creeper = currentMap.creeperspawn.getWorld().spawn(currentMap.creeperspawn, Creeper.class);
        creeper.setPowered(true);
        creeper.setAI(false);
        creeper.setMaxHealth(1000);
        creeper.setHealth(1000);
        creeper.setCustomName(ChatColor.GREEN + "CORE");
    }

    private void initPlayers() {
        plugin.getQueueManager().notifyGameStart();
        players.clear();
        players.addAll(plugin.getQueueManager().getQueue());
        Collections.shuffle(players);
        for (int i = 0; i < players.size(); i++) {
            if (i % 2 == 0) {
                defenders.add(players.get(i));
                players.get(i).setBedSpawnLocation(currentMap.defenderspawn, true);
                players.get(i).teleport(currentMap.defenderspawn);
                players.get(i).sendTitle(ChatColor.BLUE + "You are a defender!", "", 10, 40, 10);
                setDefaultInventory(players.get(i), 0);
            } else {
                attackers.add(players.get(i));
                players.get(i).setBedSpawnLocation(currentMap.attackerspawn, true);
                players.get(i).teleport(currentMap.attackerspawn);
                players.get(i).sendTitle(ChatColor.RED + "You are an attacker!", "", 10, 40, 10);
                setDefaultInventory(players.get(i), 1);
            }
            for (PotionEffect effect : players.get(i).getActivePotionEffects()) {
                players.get(i).removePotionEffect(effect.getType());
            }
            players.get(i).setGameMode(GameMode.ADVENTURE);
            players.get(i).setFoodLevel(20);
            players.get(i).setHealth(20);
            players.get(i).setSaturation(0);
        }
    }

    private void setDefaultInventory(Player player, int team) { // 0 - Defender, 1 - Attacker
        player.getInventory().clear();
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
        player.getInventory().setItem(0, new ItemStack(Material.WOODEN_SWORD));
        player.getInventory().setItem(1, new ItemStack(Material.BOW));
        player.getInventory().setItem(2, new ItemStack(Material.FISHING_ROD));
        player.getInventory().setItem(3, new ItemStack(Material.COOKED_BEEF, 8));
        player.getInventory().setItem(4, new ItemStack(Material.ARROW, 5));

        ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT, 1); // TODO: This doesn't work, from looks of it not possible anymore
        goldIngot.setAmount(0);
        player.getInventory().addItem(goldIngot);

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta helmetItemMeta = (LeatherArmorMeta) helmet.getItemMeta();
        helmetItemMeta.setColor(team == 0 ? Color.BLUE : Color.RED);
        helmet.setItemMeta(helmetItemMeta);
        LeatherArmorMeta chestplateItemMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        chestplateItemMeta.setColor(team == 0 ? Color.BLUE : Color.RED);
        chestplate.setItemMeta(chestplateItemMeta);
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);

        for(ItemStack item : player.getInventory().getContents()) {
            if(item != null && item.getData() != null) {
                ItemMeta meta = item.getItemMeta();
                meta.setUnbreakable(true);
                item.setItemMeta(meta);
            }
        }
    }

    private void initBossBar() {
        bossBar = Bukkit.createBossBar(ChatColor.RED + "Creeper Health", BarColor.RED, BarStyle.SOLID);
        new BukkitRunnable() {
            @Override
            public void run() {
                if(creeper.isDead()) {
                    bossBar.setVisible(false);
                    bossBar.removeAll();
                    cancel();
                    endGame(1);
                    return;
                }
                bossBar.setProgress(creeper.getHealth() / creeper.getMaxHealth());
                DecimalFormat df = new DecimalFormat("#.0");
                bossBar.setTitle(ChatColor.RED + "Creeper Health: " + df.format(creeper.getHealth()) + "/" + df.format(creeper.getHealth()));
                bossBar.setVisible(true);
                for (Player p : players) {
                    bossBar.addPlayer(p);
                }
            }
        }.runTaskTimer(plugin,0, 3);
    }

    private void initTimer() {
        //Show the current time as the XP bar
        new BukkitRunnable() {
            @Override
            public void run() {
                checkPowerups();
                notifyTimeLeft();
                if(timeLeft == 0 || !isGameStarted()) {
                    endGame(0);
                    cancel();
                    return;
                }
                for(Player p : players) {
                    p.setLevel(timeLeft);
                    p.setExp(0);
                }
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private void checkPowerups() {
        if(timeLeft == 120){
            for(Player p : attackers) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            }
        }
    }

    private void notifyTimeLeft() {
        if(timeLeft == 60){
            for(Player p : players) {
                p.sendTitle(ChatColor.RED + "1 Minute Remaining!", "", 10, 40, 10);
            }
        }
    }

    public void endGame(int winner) { // 0 - Defenders, 1 - Attackers
        gameStarted = false;
        for(Player p : players) {
            p.setLevel(0);
            p.setExp(0);
            p.getInventory().clear();
            p.teleport(lobbySpawn);
            p.setBedSpawnLocation(lobbySpawn, true);
            for(PotionEffect effect : p.getActivePotionEffects()) {
                p.removePotionEffect(effect.getType());
            }
            p.setGameMode(GameMode.ADVENTURE);
        }
        players.clear();
        defenders.clear();
        attackers.clear();
        creeper.remove();
        bossBar.setVisible(false);
        bossBar.removeAll();
        plugin.getQueueManager().notifyGameEnd(winner);
        plugin.getQueueManager().resetQueue();
        plugin.getGoldManager().resetGame();
        plugin.getShopManager().resetShop();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public Creeper getCreeper() {
        return creeper;
    }

    public List<Player> getDefenders() {
        return defenders;
    }

    public List<Player> getAttackers() {
        return attackers;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }
}
