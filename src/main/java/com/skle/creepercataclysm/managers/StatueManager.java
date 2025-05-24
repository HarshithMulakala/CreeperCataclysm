package com.skle.creepercataclysm.managers;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.skle.creepercataclysm.api.CreeperCataclysmPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

import static com.sk89q.worldedit.world.block.BlockTypes.*;

public class StatueManager {
    private final CreeperCataclysmPlugin plugin;
    private final ArrayList<BaseBlock> quartzAllStairs;
    private final ArrayList<BaseBlock> waxedOxidizedStairs;
    private final ArrayList<BaseBlock> cutCopperStairs;
    public int[] progressPoints;
    public boolean[][] captured;


    public final Region[] regions = new CuboidRegion[] {
            new CuboidRegion(BlockVector3.at(1020, -54, 1028), BlockVector3.at(1016, -46, 1023)),
            new CuboidRegion(BlockVector3.at(1117, -54, 1028), BlockVector3.at(1113, -46, 1023)),
            new CuboidRegion(BlockVector3.at(1117, -54, 1161), BlockVector3.at(1113, -46, 1156)),
            new CuboidRegion(BlockVector3.at(1020, -54, 1161), BlockVector3.at(1016, -46, 1156))
    };
    List<Map<BlockState, BlockState>> whiteToBlueReplacements = new ArrayList<Map<BlockState, BlockState>>();

    public StatueManager(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
        quartzAllStairs = new ArrayList<>();
        waxedOxidizedStairs = new ArrayList<>();
        cutCopperStairs = new ArrayList<>();
        for(com.sk89q.worldedit.world.block.BlockState state: QUARTZ_STAIRS.getAllStates()){
            quartzAllStairs.add(state.toBaseBlock());
        }
        for(com.sk89q.worldedit.world.block.BlockState state: WAXED_OXIDIZED_CUT_COPPER_STAIRS.getAllStates()){
            waxedOxidizedStairs.add(state.toBaseBlock());
        }
        for(com.sk89q.worldedit.world.block.BlockState state: WAXED_CUT_COPPER_STAIRS.getAllStates()){
            cutCopperStairs.add(state.toBaseBlock());
        }
        progressPoints = new int[4];
        captured = new boolean[4][2];// 0 is attacker || 1 is defender
        for (int i = 0; i < 4; i++) {
            for(int j = 0; j < 2; j++) {
                captured[i][j] = false;
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!plugin.getGameManager().isGameStarted()){return;}
                updateProgressPoints();
                drawProgressCircle();
            }
        }.runTaskTimer(plugin,0, 2);

    }

    public void resetStatues(){
        for(int i = 0; i < regions.length; i++){
            progressPoints[i] = 0;
            captured[i][0] = false;
            captured[i][1] = false;
            statueToWhite(i);
        }
    }



    public void drawProgressCircle() {

        org.bukkit.World world = plugin.getGameManager().getCurrentMap().creeperspawn.getWorld();
        for(int index = 0; index < regions.length; index++) {
            Particle.DustOptions dustBlue = new Particle.DustOptions(Color.BLUE, 1.0f);
            Particle.DustOptions dustRed = new Particle.DustOptions(Color.RED, 1.0f);
            Particle.DustOptions dustWhite = new Particle.DustOptions(Color.WHITE, 1.0f);
            double y = regions[index].getCenter().getY() - 6;
            int totalPoints = 100;
            int radius = 8;

            for (int i = 0; i < totalPoints; i++) {


                double angle = (2 * Math.PI * i / totalPoints) * (progressPoints[index] < 0 ? -1 : 1);

                double x = regions[index].getCenter().getX() + radius * Math.cos(angle);
                x+=1;
                double z = regions[index].getCenter().getZ() + radius * Math.sin(angle);
                Location loc = new Location(world, x, y, z);

                if (i < Math.abs(progressPoints[index])) {
                    world.spawnParticle(Particle.DUST, loc, 1, progressPoints[index] < 0 ? dustBlue : dustRed);
                } else {
                    world.spawnParticle(Particle.DUST, loc, 1, dustWhite);
                }
            }
        }

    }

    public void updateProgressPoints() {
        org.bukkit.World world = plugin.getGameManager().getCurrentMap().creeperspawn.getWorld();
        for (int index = 0; index < regions.length; index++) {

            Location center = new Location(world, regions[index].getCenter().getX(), regions[index].getCenter().getY() - 6, regions[index].getCenter().getZ());;
            List<Player> redPlayers = plugin.getGameManager().getAttackers();
            List<Player> bluePlayers = plugin.getGameManager().getDefenders();

            boolean redInRegion = false;
            boolean blueInRegion = false;

            for (Player red : redPlayers) {
                if (red.getWorld().equals(world) && red.getLocation().distance(center) <= 8) {
                    redInRegion = true;
                    break;
                }
            }

            for (Player blue : bluePlayers) {
                if (blue.getWorld().equals(world) && blue.getLocation().distance(center) <= 8) {
                    blueInRegion = true;
                    break;
                }
            }

            if (redInRegion && !blueInRegion) {
                progressPoints[index] = Math.min(100, progressPoints[index] + 1);
                if(!captured[index][0] && progressPoints[index] == 100){
                    captured[index][0] = true;
                    captured[index][1] = false;
                    statueToRed(index);
                    for(Player player : plugin.getGameManager().getPlayers()) {
                        player.sendMessage(ChatColor.RED + "Attacked captured statue " + (index + 1));
                    }
                }
                if(!captured[index][0] && (progressPoints[index] == 0 || progressPoints[index] == 1)){
                    captured[index][1] = false;
                    captured[index][0] = false;
                    statueToWhite(index);
                }
            } else if (blueInRegion && !redInRegion) {
                progressPoints[index] = Math.max(-100, progressPoints[index] - 1);
                if(!captured[index][1] && progressPoints[index] == -100){
                    captured[index][1] = true;
                    captured[index][0] = false;
                    statueToBlue(index);
                    for(Player player : plugin.getGameManager().getPlayers()) {
                        player.sendMessage(ChatColor.BLUE + "Defenders captured statue " + (index + 1));
                    }
                }
                if(!captured[index][1] && (progressPoints[index] == 0 || progressPoints[index] == -1)){
                    captured[index][1] = false;
                    captured[index][0] = false;
                    statueToWhite(index);
                }
            }
        }
    }

    public void statueToBlue(int index) {
        World world = BukkitAdapter.adapt(plugin.getGameManager().getCurrentMap().creeperspawn.getWorld());

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            Region region = regions[index];
            editSession.replaceBlocks(region, Set.of(QUARTZ_BLOCK.getDefaultState().toBaseBlock()), (Pattern)WAXED_OXIDIZED_CUT_COPPER.getDefaultState().toBaseBlock());
            editSession.replaceBlocks(region, Set.of(QUARTZ_SLAB.getDefaultState().toBaseBlock()), (Pattern)WAXED_OXIDIZED_CUT_COPPER_SLAB.getDefaultState().toBaseBlock());
            for(int i = 0; i < quartzAllStairs.size(); i++){
                editSession.replaceBlocks(region, Set.of(quartzAllStairs.get(i).toBaseBlock()), (Pattern)waxedOxidizedStairs.get(i).toBaseBlock());
            }

            editSession.replaceBlocks(region, Set.of(WAXED_CUT_COPPER.getDefaultState().toBaseBlock()), (Pattern)WAXED_OXIDIZED_CUT_COPPER.getDefaultState().toBaseBlock());
            editSession.replaceBlocks(region, Set.of(WAXED_CUT_COPPER_SLAB.getDefaultState().toBaseBlock()), (Pattern)WAXED_OXIDIZED_CUT_COPPER_SLAB.getDefaultState().toBaseBlock());

            for(int i = 0; i < quartzAllStairs.size(); i++){
                editSession.replaceBlocks(region, Set.of(cutCopperStairs.get(i).toBaseBlock()), (Pattern)waxedOxidizedStairs.get(i).toBaseBlock());
            }

           editSession.setBlock(BlockVector3.at(region.getCenter().getX(), region.getCenter().getY() + 38, region.getCenter().getZ()), BLUE_STAINED_GLASS.getDefaultState());
        } catch (MaxChangedBlocksException e) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(ChatColor.RED + "Max blocks reached");
            }
        }
    }

    public void statueToRed(int index) {
        World world = BukkitAdapter.adapt(plugin.getGameManager().getCurrentMap().creeperspawn.getWorld());

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            Region region = regions[index];
            editSession.replaceBlocks(region, Set.of(QUARTZ_BLOCK.getDefaultState().toBaseBlock()), (Pattern)WAXED_CUT_COPPER.getDefaultState().toBaseBlock());
            editSession.replaceBlocks(region, Set.of(QUARTZ_SLAB.getDefaultState().toBaseBlock()), (Pattern)WAXED_CUT_COPPER_SLAB.getDefaultState().toBaseBlock());
            for(int i = 0; i < quartzAllStairs.size(); i++){
                editSession.replaceBlocks(region, Set.of(quartzAllStairs.get(i).toBaseBlock()), (Pattern)cutCopperStairs.get(i).toBaseBlock());
            }

            editSession.replaceBlocks(region, Set.of(WAXED_OXIDIZED_CUT_COPPER.getDefaultState().toBaseBlock()), (Pattern)WAXED_CUT_COPPER.getDefaultState().toBaseBlock());
            editSession.replaceBlocks(region, Set.of(WAXED_OXIDIZED_CUT_COPPER_SLAB.getDefaultState().toBaseBlock()), (Pattern)WAXED_CUT_COPPER_SLAB.getDefaultState().toBaseBlock());

            for(int i = 0; i < quartzAllStairs.size(); i++){
                editSession.replaceBlocks(region, Set.of(waxedOxidizedStairs.get(i).toBaseBlock()), (Pattern)cutCopperStairs.get(i).toBaseBlock());
            }

            editSession.setBlock(BlockVector3.at(region.getCenter().getX(), region.getCenter().getY() + 38, region.getCenter().getZ()), RED_STAINED_GLASS.getDefaultState());
        } catch (MaxChangedBlocksException e) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(ChatColor.RED + "Max blocks reached");
            }
        }
    }

    public void statueToWhite(int index) {
        World world = BukkitAdapter.adapt(plugin.getGameManager().getCurrentMap().creeperspawn.getWorld());

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            Region region = regions[index];
            editSession.replaceBlocks(region, Set.of(WAXED_CUT_COPPER.getDefaultState().toBaseBlock()), (Pattern)QUARTZ_BLOCK.getDefaultState().toBaseBlock());
            editSession.replaceBlocks(region, Set.of(WAXED_CUT_COPPER_SLAB.getDefaultState().toBaseBlock()), (Pattern)QUARTZ_SLAB.getDefaultState().toBaseBlock());
            for(int i = 0; i < quartzAllStairs.size(); i++){
                editSession.replaceBlocks(region, Set.of(cutCopperStairs.get(i).toBaseBlock()), (Pattern)quartzAllStairs.get(i).toBaseBlock());
            }

            editSession.replaceBlocks(region, Set.of(WAXED_OXIDIZED_CUT_COPPER.getDefaultState().toBaseBlock()), (Pattern)QUARTZ_BLOCK.getDefaultState().toBaseBlock());
            editSession.replaceBlocks(region, Set.of(WAXED_OXIDIZED_CUT_COPPER_SLAB.getDefaultState().toBaseBlock()), (Pattern)QUARTZ_SLAB.getDefaultState().toBaseBlock());

            for(int i = 0; i < quartzAllStairs.size(); i++){
                editSession.replaceBlocks(region, Set.of(waxedOxidizedStairs.get(i).toBaseBlock()), (Pattern)quartzAllStairs.get(i).toBaseBlock());
            }
            editSession.setBlock(BlockVector3.at(region.getCenter().getX(), region.getCenter().getY() + 38, region.getCenter().getZ()), WHITE_STAINED_GLASS.getDefaultState());
        } catch (MaxChangedBlocksException e) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(ChatColor.RED + "Max blocks reached");
            }
        }
    }
}
