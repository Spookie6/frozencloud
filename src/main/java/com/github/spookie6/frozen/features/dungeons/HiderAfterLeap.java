package com.github.spookie6.frozen.features.dungeons;

import com.github.spookie6.frozen.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.github.spookie6.frozen.events.impl.ChatPacketEvent;
import com.github.spookie6.frozen.utils.ChatUtils;
import com.github.spookie6.frozen.utils.skyblock.LocationUtils;
//import com.github.spookie6.frozen.utils.skyblock.dungeon.SplitsManager;

import static com.github.spookie6.frozen.Frozen.mc;

public class HiderAfterLeap {
    private static long hiddenTill = -1;

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre e) {
        if (hiddenTill == -1) return;
        if (e.entityPlayer.equals(mc.thePlayer)) return;

        long remaining = hiddenTill - Minecraft.getSystemTime();

        if (remaining > 0) e.setCanceled(true);
        else {
            hiddenTill = -1;
            ChatUtils.sendModInfo("Revealing players!");
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onChatPacket(ChatPacketEvent e) {
        if (ModConfig.hidePlayersAfterLeap == 0) return;
        if (e.message.matches("You have teleported to (\\w{1,16})!")) {
//            if (ModConfig.hidePlayersAfterLeap == 2 && !LocationUtils.currentDungeon.splitsManager.currentSplit.equals(SplitsManager.Split.Terminals)) return;
            hiddenTill = Minecraft.getSystemTime() + ModConfig.hidePlayersAfterLeapDuration * 1000L;
            ChatUtils.sendModInfo("Hiding players!");
        }
    }
}
