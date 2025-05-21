package com.skle.creepercataclysm.managers;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
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
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.sk89q.worldedit.world.block.BlockTypes.*;

public class StatueManager {
    private final CreeperCataclysmPlugin plugin;
    private final Region[] regions = new CuboidRegion[] {
            new CuboidRegion(new BlockVector3(1020, -54, 1028), new BlockVector3(1016, -46, 1023)),
            new CuboidRegion(new BlockVector3(1117, -54, 1028), new BlockVector3(1113, -46, 1023)),
            new CuboidRegion(new BlockVector3(1117, -54, 1161), new BlockVector3(1112, -46, 1156)),
            new CuboidRegion(new BlockVector3(1020, -54, 1161), new BlockVector3(1016, -46, 1156))
    };
    List<Map<BlockState, BlockState>> whiteToBlueReplacements = new ArrayList<Map<BlockState, BlockState>>();

    public StatueManager(CreeperCataclysmPlugin plugin) {
        this.plugin = plugin;
    }

    public void statueToBlue() {
        World world = (World) plugin.getGameManager().getCurrentMap().creeperspawn.getWorld();
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            for (Region region : regions) {
                editSession.replaceBlocks(region, Set.of(QUARTZ_BLOCK.getDefaultState().toBaseBlock()), (Pattern)WAXED_OXIDIZED_CUT_COPPER.getDefaultState().toBaseBlock());
                editSession.replaceBlocks(region, Set.of(QUARTZ_SLAB.getDefaultState().toBaseBlock()), (Pattern)WAXED_OXIDIZED_CUT_COPPER_SLAB.getDefaultState().toBaseBlock());
                editSession.replaceBlocks(region, Set.of(QUARTZ_STAIRS.getDefaultState().toBaseBlock()), (Pattern)WAXED_OXIDIZED_CUT_COPPER_STAIRS.getDefaultState().toBaseBlock());
                editSession.replaceBlocks(region, Set.of(WAXED_CUT_COPPER.getDefaultState().toBaseBlock()), (Pattern)WAXED_OXIDIZED_CUT_COPPER.getDefaultState().toBaseBlock());
                editSession.replaceBlocks(region, Set.of(WAXED_CUT_COPPER_SLAB.getDefaultState().toBaseBlock()), (Pattern)WAXED_OXIDIZED_CUT_COPPER_SLAB.getDefaultState().toBaseBlock());
                editSession.replaceBlocks(region, Set.of(WAXED_CUT_COPPER_STAIRS.getDefaultState().toBaseBlock()), (Pattern)WAXED_OXIDIZED_CUT_COPPER_STAIRS.getDefaultState().toBaseBlock());
            }
        } catch (MaxChangedBlocksException e) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(ChatColor.RED + "Max blocks reached");
            }
        }
    }

    public void statueToRed() {

    }

    public void statueToWhite() {

    }
}
