package com.skle.creepercataclysm.listeners;

import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import com.skle.creepercataclysm.managers.GameManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class ProjectileListener implements Listener {
    private final CreeperCataclysmPlugin plugin;

    private static Egg currentEgg;
    private static final Map<Location, BlockData> originalBlocks = new HashMap<>();
    private static BukkitRunnable colorTask;
    private static boolean isEggActive = false;
    private static Player eggPlayer;
    private static Player isoPlayer1;
    private static Player isoPlayer2;
    private static Location isoPlayer1Location;
    private static Location isoPlayer2Location;
    private static Block isoPlayer1Block;
    private static Block isoPlayer2Block;
    private static BlockData isoPlayer1BlockData;
    private static BlockData isoPlayer2BlockData;
    private static boolean isoUltInProgress = false;

    private final int effectRadius = 10; // Radius for the perpendicular effect

    public ProjectileListener(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    public static void onEndGame() {
        if(isoUltInProgress) {
            handleIsoEnd(isoPlayer1, isoPlayer1Location);
        }
        currentEgg = null;
        for (Map.Entry<Location, BlockData> entry : originalBlocks.entrySet()) {
            Block block = entry.getKey().getBlock();
            if (block.getWorld().isChunkLoaded(block.getX() >> 4, block.getZ() >> 4)) {
                block.setBlockData(entry.getValue());
            }
        }
        originalBlocks.clear();
        colorTask = null;
        isEggActive = false;
        eggPlayer = null;
        isoPlayer1 = null;
        isoPlayer2 = null;
        isoPlayer1Block = null;
        isoPlayer2Block = null;
        isoPlayer1BlockData = null;
        isoPlayer2BlockData = null;
        isoPlayer1Location = null;
        isoPlayer2Location = null;
        isoUltInProgress = false;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!plugin.getGameManager().isGameStarted()) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity().getShooter();
        if (!plugin.getGameManager().getPlayers().contains(player)) return;
        if (!(event.getEntity() instanceof Egg)) return;

        Egg egg = (Egg) event.getEntity();

        if (isEggActive || isoUltInProgress || (currentEgg != null && currentEgg.isValid() && !currentEgg.isOnGround())) {
            player.sendMessage(ChatColor.DARK_PURPLE + "There is another fight in progress!");
            event.setCancelled(true);
            return;
        }

        currentEgg = egg;
        eggPlayer = player;
        isEggActive = true;
        originalBlocks.clear();

        for (Player p : plugin.getGameManager().getPlayers()) {
            p.playSound(p.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 3.0F, 1.0F);
        }

        colorTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (currentEgg == null || !currentEgg.isValid() || currentEgg.isOnGround()) {
                    this.cancel();
                    if (currentEgg != null && !currentEgg.isValid() && !currentEgg.isOnGround()) {
                        isEggActive = false;
                    }
                    return;
                }

                Location eggLoc = currentEgg.getLocation();
                World world = eggLoc.getWorld();
                if (world == null) return;

                // 1. Color 3x3 area directly under the egg
                Location underLocCenter = getHighestSolidBlockAtXZ(world, eggLoc.getX(), eggLoc.getY() - 1.0, eggLoc.getZ());
                if (underLocCenter != null) {
                    // Pass the location of the initially found center block
                    apply3x3Effect(underLocCenter, Material.PURPLE_CONCRETE, originalBlocks);
                }

                // 2. Color 3x3 areas to the sides
                Vector velocity = currentEgg.getVelocity();
                double vx = velocity.getX();
                double vz = velocity.getZ();
                double horizontalSpeedSq = vx * vx + vz * vz;

                if (horizontalSpeedSq > (0.05 * 0.05)) {
                    double horizontalSpeed = Math.sqrt(horizontalSpeedSq);
                    double perpDx = -vz / horizontalSpeed;
                    double perpDz = vx / horizontalSpeed;
                    double scanStartYForSides = eggLoc.getY();

                    for (int i = 1; i <= effectRadius; i++) {
                        // Side 1
                        double targetX1 = eggLoc.getX() + i * perpDx;
                        double targetZ1 = eggLoc.getZ() + i * perpDz;
                        Location sideLoc1Center = getHighestSolidBlockAtXZ(world, targetX1, scanStartYForSides, targetZ1);
                        if (sideLoc1Center != null) {
                            apply3x3Effect(sideLoc1Center, Material.PURPLE_CONCRETE, originalBlocks);
                        }

                        // Side 2
                        double targetX2 = eggLoc.getX() - i * perpDx;
                        double targetZ2 = eggLoc.getZ() - i * perpDz;
                        Location sideLoc2Center = getHighestSolidBlockAtXZ(world, targetX2, scanStartYForSides, targetZ2);
                        if (sideLoc2Center != null) {
                            apply3x3Effect(sideLoc2Center, Material.PURPLE_CONCRETE, originalBlocks);
                        }
                    }
                }
            }
        };
        colorTask.runTaskTimer(plugin, 0L, 1L);
    }

    public Player getClosestPlayerOnPurple(Player player) {
        Player closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;
        for (Player otherPlayer : plugin.getGameManager().getPlayers()) {
            if(arePlayersOnSameTeam(player, otherPlayer)) continue;
            if (!isPlayerStandingOnPurple(otherPlayer)) continue;
            double distance = getDistanceBetweenPlayers(player, otherPlayer);
            if (distance < closestDistance) {
                closestPlayer = otherPlayer;
                closestDistance = distance;
            }
        }
        return closestPlayer;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Egg)) return;
        Egg egg = (Egg) event.getEntity();
        if(egg != currentEgg) return;

        if (colorTask != null) {
            colorTask.cancel();
        }
        event.setCancelled(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                Player closestPlayer = getClosestPlayerOnPurple(eggPlayer);
                for (Map.Entry<Location, BlockData> entry : originalBlocks.entrySet()) {
                    Block block = entry.getKey().getBlock();
                    if (block.getWorld().isChunkLoaded(block.getX() >> 4, block.getZ() >> 4)) {
                        block.setBlockData(entry.getValue());
                    }
                }
                if (closestPlayer != null) {
                    GameManager.sendCenteredMessage(closestPlayer, ChatColor.LIGHT_PURPLE + "§l============================================");
                    closestPlayer.sendMessage("");
                    GameManager.sendCenteredMessage(closestPlayer,ChatColor.DARK_PURPLE + "It's you and §l" + eggPlayer.getName() + "§r.");
                    closestPlayer.sendMessage("");
                    GameManager.sendCenteredMessage(closestPlayer,ChatColor.LIGHT_PURPLE + "§l============================================");
                    GameManager.sendCenteredMessage(eggPlayer, ChatColor.LIGHT_PURPLE + "§l============================================");
                    eggPlayer.sendMessage("");
                    GameManager.sendCenteredMessage(eggPlayer, ChatColor.DARK_PURPLE + "§lNo distractions.");
                    eggPlayer.sendMessage("");
                    GameManager.sendCenteredMessage(eggPlayer, ChatColor.LIGHT_PURPLE + "§l============================================");
                    teleportToIsoUlt(eggPlayer, closestPlayer);
                } else {
//                    eggPlayer.sendMessage(ChatColor.DARK_PURPLE + "You didn't hit anyone with your ult!");
                }
                originalBlocks.clear();
                currentEgg = null;
                eggPlayer = null;
                isEggActive = false;

            }
        }.runTaskLater(plugin, 20L);
    }

    public double getDistanceBetweenPlayers(Player player1, Player player2) {
        if (player1 == null || player2 == null) {
            return Double.MAX_VALUE;
        }

        Location loc1 = player1.getLocation();
        Location loc2 = player2.getLocation();

        return loc1.distance(loc2);
    }

    public boolean isPlayerStandingOnPurple(Player player) {
        if (player == null) {
            return false; // Or throw an IllegalArgumentException, depending on desired handling
        }

        Location playerLocation = player.getLocation();
        World world = player.getWorld();
        int playerX = playerLocation.getBlockX();
        int playerZ = playerLocation.getBlockZ();

        // Iterate vertically from the bottom of the world to the top
        for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
            Block block = world.getBlockAt(playerX, y, playerZ);
            if (block.getType() == Material.PURPLE_CONCRETE) {
                return true; // Purple concrete found
            }
        }

        return false; // No purple concrete found in the column
    }

    public void teleportToIsoUlt(Player player1, Player player2) {
        //store previous locations, set blocks below players to budding amethyst, teleport players to markers, set max health to 20, give darkness, start timer
        isoPlayer1 = player1;
        isoPlayer2 = player2;
        this.isoPlayer1Location = player1.getLocation();
        this.isoPlayer2Location = player2.getLocation();
        this.isoPlayer1Block = new Location(isoPlayer1Location.getWorld(), isoPlayer1Location.getX(), isoPlayer1Location.getY() - 1, isoPlayer1Location.getZ()).getBlock();
        this.isoPlayer2Block = new Location(isoPlayer2Location.getWorld(), isoPlayer2Location.getX(), isoPlayer2Location.getY() - 1, isoPlayer2Location.getZ()).getBlock();
        this.isoPlayer1BlockData = this.isoPlayer1Block.getBlockData();
        this.isoPlayer2BlockData = this.isoPlayer2Block.getBlockData();
        isoPlayer1Block.setType(Material.BUDDING_AMETHYST);
        isoPlayer2Block.setType(Material.BUDDING_AMETHYST);

        player1.teleport(new Location(isoPlayer1Location.getWorld(), -104, -45, -245, 180, 0));
        player2.teleport(new Location(isoPlayer2Location.getWorld(), -104, -45, -275, 0, 0));
        player1.setHealth(20);
        player2.setHealth(20);

        isoPlayer1.playSound(isoPlayer1.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1.0F, 1.0F);
        isoPlayer2.playSound(isoPlayer2.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1.0F, 1.0F);

        player1.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, Integer.MAX_VALUE, 1, false, false));
        player1.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 0, false, false));
        player2.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, Integer.MAX_VALUE, 1, false, false));
        player1.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 2, 255, false, false));
        player2.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 2, 255, false, false));

        this.isoUltInProgress = true;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if(player.equals(isoPlayer1) && isoUltInProgress) {
            for (Player p : plugin.getGameManager().getPlayers()) {
                if(arePlayersOnSameTeam(p, isoPlayer2)) {
                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.840896f);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.681793f);
                            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.840896f);
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.681793f);
                                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.943874f);
                                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.887749f);
                                        }
                                    }, 3L);
                                }
                            }, 6L);
                        }
                    }, 3L);
                } else {
                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.667420f);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.334840f);
                            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.629961f);
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.259921f);
                                }
                            }, 3L);
                        }
                    }, 3L);
                }
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    handleIsoEnd(isoPlayer2, isoPlayer2Location);
                }
            }.runTaskLater(plugin, 60L);
            return;
        }
        if (player.equals(isoPlayer2) && isoUltInProgress) {
            for (Player p : plugin.getGameManager().getPlayers()) {
                if(arePlayersOnSameTeam(p, isoPlayer1)) {
                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.840896f);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.681793f);
                            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.840896f);
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.681793f);
                                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.943874f);
                                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.887749f);
                                        }
                                    }, 3L);
                                }
                            }, 6L);
                        }
                    }, 3L);
                } else {
                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.667420f);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.334840f);
                            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.629961f);
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.259921f);
                                }
                            }, 3L);
                        }
                    }, 3L);
                }
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    handleIsoEnd(isoPlayer1, isoPlayer1Location);
                }
            }.runTaskLater(plugin, 60L);
            return;
        }
    }

    public static void handleIsoEnd(Player alivePlayer, Location teleportLocation) {
        alivePlayer.teleport(teleportLocation);
        alivePlayer.playSound(alivePlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        isoPlayer1Block.setBlockData(isoPlayer1BlockData);
        isoPlayer2Block.setBlockData(isoPlayer2BlockData);
        alivePlayer.setHealth(20);
        for (PotionEffect effect : alivePlayer.getActivePotionEffects()) {
            alivePlayer.removePotionEffect(effect.getType());
        }
        isoUltInProgress = false;
    }

    public boolean arePlayersOnSameTeam(Player player1, Player player2) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        Team team1 = scoreboard.getEntryTeam(player1.getName());
        Team team2 = scoreboard.getEntryTeam(player2.getName());

        // Check if both players are on a team and it's the same team
        return team1 != null && team1.equals(team2);
    }

    /**
     * Applies the given material to a 3x3 area of blocks. For each X,Z coordinate in the 3x3 grid
     * centered around the initialCenterOfEffect's X,Z, it finds the highest solid block
     * (scanning downwards from initialCenterOfEffect's Y level) and changes that block.
     * Stores the original BlockData in originalBlocksMap before changing.
     *
     * @param initialCenterOfEffect The location of the block that triggered this 3x3 effect.
     * Its X,Z define the center of the 3x3 grid, and its Y is used
     * as the starting height for downward scans for neighboring blocks.
     * @param newMaterial The material to set the blocks to.
     * @param originalBlocksMap The map to store original block data.
     */
    private void apply3x3Effect(Location initialCenterOfEffect, Material newMaterial, Map<Location, BlockData> originalBlocksMap) {
        World world = initialCenterOfEffect.getWorld();
        if (world == null) return;

        // Base X,Z from the initially identified center block of this 3x3 effect
        int baseX = initialCenterOfEffect.getBlockX();
        int baseZ = initialCenterOfEffect.getBlockZ();
        // Use the Y-level of this initial center block as the starting point for scanning downwards for its neighbors
        double yScanReference = initialCenterOfEffect.getY();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                // Calculate the X,Z for the current block in the 3x3 grid
                double currentScanX = baseX + dx;
                double currentScanZ = baseZ + dz;

                // Find the actual highest solid block at this (currentScanX, currentScanZ),
                // starting the scan from yScanReference.
                Location surfaceBlockLocationAtThisXZ = getHighestSolidBlockAtXZ(world, currentScanX, yScanReference, currentScanZ);

                if (surfaceBlockLocationAtThisXZ != null) {
                    // This is the actual surface block to change at this specific X,Z in the grid
                    Block blockToChange = surfaceBlockLocationAtThisXZ.getBlock();

                    // Store original block data if not already stored for this specific block's location
                    // The key is the actual location of the block being modified.
                    if (!originalBlocksMap.containsKey(surfaceBlockLocationAtThisXZ)) {
                        originalBlocksMap.put(surfaceBlockLocationAtThisXZ, blockToChange.getBlockData().clone());
                    }

                    // Set the new material (only if it's different to avoid redundant updates)
                    if (blockToChange.getType() != newMaterial) {
                        blockToChange.setType(newMaterial);
                    }
                }
            }
        }
    }

    private static Location getHighestSolidBlockAtXZ(World world, double x, double yScanStart, double z) {
        if (world == null) {
            return null;
        }

        int startY = (int) Math.floor(yScanStart); // Start scanning from the block at or just below yScanStart
        int minHeight = world.getMinHeight();

        for (int currentY = startY; currentY >= minHeight; currentY--) {
            // Check chunk loading using the integer block coordinates
            if (!world.isChunkLoaded((int)Math.floor(x) >> 4, (int)Math.floor(z) >> 4)) {
                // If chunk is not loaded, we can't determine the block.
                // Depending on desired behavior, you could return null or skip. Here, we skip this Y level.
                // However, if the chunk for the target X,Z is never loaded in the scan range, no block will be found.
                continue;
            }

            Block block = world.getBlockAt((int)Math.floor(x), currentY, (int)Math.floor(z));
            if (block.getType().isSolid()) {
                return block.getLocation(); // Return the location of the found solid block
            }
        }
        return null; // No solid block found
    }
}