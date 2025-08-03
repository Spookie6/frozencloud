package com.github.spookie6.frozen.utils.gui.lists;

import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonEnums;
import net.minecraft.util.BlockPos;

public class DefaultOptions {
    public enum LocationEnums {
        SIMONSAYS(new BlockPos(108, 120, 94), 2.5, "At SS!", DungeonEnums.Class.HEALER),
        EARLY_ENTER_2(new BlockPos(55, 109, 130), 2.5, "At Pre Enter 2!", DungeonEnums.Class.ARCHER),
        SAFE_SPOT_2(new BlockPos(48, 109, 121.988), 2.5, "At 2 Safespot!", DungeonEnums.Class.ARCHER),
        EARLY_ENTER_3(new BlockPos(1, 109, 104), 2.5, "At Pre Enter 3!", DungeonEnums.Class.HEALER),
        SAFE_SPOT_3(new BlockPos(18, 121, 97), 2.5, "At 3 Safespot!", DungeonEnums.Class.HEALER),
        CORE(new BlockPos(54, 115, 50), 2.5, "At Core!", DungeonEnums.Class.MAGE),
        TUNNEL(new BlockPos(54, 115, 56), 2.5, "Inside Goldor Tunnel!", DungeonEnums.Class.MAGE),
        MID(new BlockPos(54.5, 65, 76.5), 7.8, "At Mid!", DungeonEnums.Class.HEALER);

        public BlockPos pos;
        public double range;
        public String message;
        public DungeonEnums.Class clazz;

        LocationEnums(BlockPos pos, double range, String message, DungeonEnums.Class clazz) {
            this.pos = pos;
            this.range = range;
            this.message = message;
            this.clazz = clazz;
        }

        public boolean isAtLocation(BlockPos pos) {
            if (this.equals(SAFE_SPOT_2)) {
                return this.pos.distanceSq(pos.getX(), pos.getY(), pos.getZ()) < 5 && this.pos.getZ() == pos.getZ();
            }

            return this.pos.distanceSq(pos.getX(), pos.getY(), pos.getZ()) < 5;
        }
    }
}
