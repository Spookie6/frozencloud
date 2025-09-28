package dev.frozencloud.frozen.features.dungeons;

import dev.frozencloud.frozen.config.ModConfig;
import dev.frozencloud.frozen.events.impl.ChatPacketEvent;
import dev.frozencloud.frozen.utils.ChatUtils;
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
