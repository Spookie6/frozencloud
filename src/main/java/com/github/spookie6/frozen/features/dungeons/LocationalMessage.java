package com.github.spookie6.frozen.features.dungeons;

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.github.spookie6.frozen.Frozen.mc;

public class LocationalMessage {
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase.equals(TickEvent.Phase.END)) return;

        BlockPos playerPos = mc.thePlayer.getPosition();

//        Fuck it im making a new gui for this, cyua later file.
    }
}
