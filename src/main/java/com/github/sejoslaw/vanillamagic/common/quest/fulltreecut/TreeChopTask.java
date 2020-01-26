package com.github.sejoslaw.vanillamagic.common.quest.fulltreecut;

import com.github.sejoslaw.vanillamagic.common.util.EventUtil;
import com.github.sejoslaw.vanillamagic.common.util.ToolUtil;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Class which describes single tree fall mechanics.
 *
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class TreeChopTask {
    public final World world;
    public final PlayerEntity player;
    public final ItemStack tool;
    public final int blocksPerTick;

    public Queue<BlockPos> blocks = Lists.newLinkedList();
    public Set<BlockPos> visited = new HashSet<>();

    public TreeChopTask(ItemStack tool, BlockPos start, PlayerEntity player, int blocksPerTick) {
        this.world = player.getEntityWorld();
        this.player = player;
        this.tool = tool;
        this.blocksPerTick = blocksPerTick;
        this.blocks.add(start);
    }

    /**
     * Break logs.
     */
    @SubscribeEvent
    public void chopChop(TickEvent.WorldTickEvent event) {
        if (event.side.isClient()) {
            finish();
            return;
        }

        if (event.world == world) {
            return;
        }

        int left = blocksPerTick;
        BlockPos pos;

        while (left > 0) {
            if (blocks.isEmpty()) {
                finish();
                return;
            }

            pos = blocks.remove();

            if (!visited.add(pos) || !TreeCutHelper.isLog(world, pos)) {
                continue;
            }

            for (Direction facing : Direction.Plane.HORIZONTAL) {
                BlockPos pos2 = pos.offset(facing);

                if (!visited.contains(pos2)) {
                    blocks.add(pos2);
                }
            }

            for (int x = 0; x < 3; ++x) {
                for (int z = 0; z < 3; ++z) {
                    BlockPos pos2 = pos.add(-1 + x, 1, -1 + z);

                    if (!visited.contains(pos2)) {
                        blocks.add(pos2);
                    }
                }
            }

            ToolUtil.breakExtraBlock(tool, world, player, pos, pos);
            left--;
        }
    }

    /**
     * When the tree is broken. Unregister work.
     */
    private void finish() {
        EventUtil.unregisterEvent(this);
    }
}