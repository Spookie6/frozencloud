package com.github.spookie6.frozen.features.dungeons;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.events.impl.ChatPacketEvent;
import com.github.spookie6.frozen.utils.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoDraft {
    private static final Pattern pattern = Pattern.compile("PUZZLE FAIL! (\\w+) .*");

    @SubscribeEvent
    public void onChatPacket(ChatPacketEvent e) {
        Matcher m = pattern.matcher(e.message);

        if (!ModConfig.autoDraft) return;
        if (m.find()) {
            if (!m.group(1).equals(Minecraft.getMinecraft().thePlayer.getName())) return;
            ChatUtils.sendCommand("gfs ARCHITECT_FIRST_DRAFT 1", false);
        }
    }
}
