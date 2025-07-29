package com.github.spookie6.frozen.features.dungeons;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.events.impl.ChatPacketEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.spookie6.frozen.utils.ChatUtils.sendModInfo;

public class ExplosiveShot {
    @SubscribeEvent
    public void onChatPacket(ChatPacketEvent e) {
        if (!ModConfig.exploShot) return;
        Pattern pattern = Pattern.compile("Your Explosive Shot hit (\\d+) enemies for ([\\d,\\.]+) damage.");
        Matcher matcher = pattern.matcher(e.message);

        if (matcher.find()) {
            float damage = Float.parseFloat(matcher.group(2).replaceAll(",", ""));
            float unitDmg = damage / Integer.parseInt(matcher.group(1));
            sendModInfo(String.format("&7Explosive shot did &a%,d &7damage per enemy.", (int) unitDmg));
        }
    }
}
