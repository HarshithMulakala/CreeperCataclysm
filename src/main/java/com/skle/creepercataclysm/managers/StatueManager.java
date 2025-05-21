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
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.*;

import static com.sk89q.worldedit.world.block.BlockTypes.*;

public class StatueManager {
    private final CreeperCataclysmPlugin plugin;
    private final ArrayList<BaseBlock> quartzAllStairs;
    private final ArrayList<BaseBlock> waxedOxidizedStairs;
    private final ArrayList<BaseBlock> cutCopperStairs;

    private final Region[] regions = new CuboidRegion[] {
            new CuboidRegion(BlockVector3.at(1020, -54, 1028), BlockVector3.at(1016, -46, 1023)),
            new CuboidRegion(BlockVector3.at(1117, -54, 1028), BlockVector3.at(1113, -46, 1023)),
            new CuboidRegion(BlockVector3.at(1117, -54, 1161), BlockVector3.at(1112, -46, 1156)),
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
    }

    public void statueToBlue() {
        World world = BukkitAdapter.adapt(plugin.getGameManager().getCurrentMap().creeperspawn.getWorld());

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            for (Region region : regions) {
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
            }
        } catch (MaxChangedBlocksException e) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(ChatColor.RED + "Max blocks reached");
            }
        }
    }

    public void statueToRed() {
        World world = BukkitAdapter.adapt(plugin.getGameManager().getCurrentMap().creeperspawn.getWorld());

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            for (Region region : regions) {
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
            }
        } catch (MaxChangedBlocksException e) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(ChatColor.RED + "Max blocks reached");
            }
        }
    }

    public void statueToWhite() {
        World world = BukkitAdapter.adapt(plugin.getGameManager().getCurrentMap().creeperspawn.getWorld());

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            for (Region region : regions) {
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
            }
        } catch (MaxChangedBlocksException e) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(ChatColor.RED + "Max blocks reached");
            }
        }
    }
}
