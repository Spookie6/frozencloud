package com.github.spookie6.frozen.features.dungeons;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonEnums;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.github.spookie6.frozen.events.impl.ChatPacketEvent;
import com.github.spookie6.frozen.utils.ChatUtils;
import com.github.spookie6.frozen.utils.skyblock.LocationUtils;
//import com.github.spookie6.frozen.utils.skyblock.dungeon.SplitsManager;

import java.util.Arrays;
import java.util.List;

import static com.github.spookie6.frozen.Frozen.mc;

public class HiderAfterLeap {
    private static long hiddenTill = -1;

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre e) {
        if (hiddenTill == -1 || e.entity.getUniqueID().version() == 2 || e.entityPlayer.equals(mc.thePlayer)) return;
        if (mc.thePlayer.getPosition().distanceSq(e.entityPlayer.getPosition()) > (ModConfig.hidePlayersAfterLeapRange * ModConfig.hidePlayersAfterLeapRange)) return;

        long remaining = hiddenTill - System.currentTimeMillis();
        if (remaining > 0) {
            e.setCanceled(true);
        } else {
            hiddenTill = -1;
            ChatUtils.sendModInfo("Revealing players!");
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onChatPacket(ChatPacketEvent e) {
        if (e.message.matches("You have teleported to (\\w{1,16})!")) {
        boolean hide = false;

            switch (PlayerHideOptions.values()[ModConfig.hideAfterLeap]) {
                case NEVER: return;
                case ALWAYS:
                    hide = true;
                    break;
                case BOSS:
                    hide = DungeonUtils.getInBoss();
                    break;
                case TERMINALS:
                    hide = DungeonUtils.getF7Phase().equals(DungeonEnums.M7Phases.P3);
                    break;
                case CLEAR:
                    hide = !DungeonUtils.getInBoss();
                    break;
            }
            if (!hide) return;

            hiddenTill = System.currentTimeMillis() + ModConfig.hidePlayersAfterLeapDuration * 1000L;
            ChatUtils.sendModInfo("Hiding players!");
        }
    }

    public enum PlayerHideOptions {
        NEVER,
        ALWAYS,
        BOSS,
        TERMINALS,
        CLEAR
    }
}
