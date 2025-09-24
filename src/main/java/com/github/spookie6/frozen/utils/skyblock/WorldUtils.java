package com.github.spookie6.frozen.utils.skyblock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

import static com.github.spookie6.frozen.Frozen.mc;

public class WorldUtils {
    public static IBlockState getStateAt(BlockPos pos) {
        return mc.theWorld == null ? null : mc.theWorld.getBlockState(pos);
    }

    public static Block getBlockAt(BlockPos pos) {
        IBlockState state = getStateAt(pos);
        return state == null ? null : state.getBlock();
    }
}
