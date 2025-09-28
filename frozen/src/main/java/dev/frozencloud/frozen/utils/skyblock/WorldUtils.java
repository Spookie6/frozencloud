package dev.frozencloud.frozen.utils.skyblock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

import static dev.frozencloud.frozen.Frozen.mc;

public class WorldUtils {
    public static IBlockState getStateAt(BlockPos pos) {
        return mc.theWorld == null ? null : mc.theWorld.getBlockState(pos);
    }

    public static Block getBlockAt(BlockPos pos) {
        IBlockState state = getStateAt(pos);
        return state == null ? null : state.getBlock();
    }
}
