package com.github.spookie6.frozen.features.dungeons;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.events.impl.ChatPacketEvent;
import com.github.spookie6.frozen.events.impl.GuiScreenEvent;
import com.github.spookie6.frozen.utils.ChatUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.SplitsManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TerminalSplits {
    private final Pattern terminalCompletedPattern = Pattern.compile("^(.{1,16}) (activated|completed) a (terminal|lever|device)! \\((\\d)/(\\d)\\)$");
    private final Pattern p3StartPattern = SplitsManager.Split.Storm.regex;

    private long p3Start = -1;
    private final List<Long> sectionTimes = new LinkedList<>();

    private boolean gateBlown = false;
    private boolean allTermsDone = false;

    @SubscribeEvent
    public void onChatPacket(ChatPacketEvent e) {
        if (!DungeonUtils.getFloor().isFloor(7) || !DungeonUtils.getInBoss()) return;

        long now = System.currentTimeMillis();

        if (p3StartPattern.matcher(e.message).find()) p3Start = now;
        if (e.message.matches("^The gate has been destroyed!$")) gateBlown = true;

        Matcher m = terminalCompletedPattern.matcher(e.message);

        if (m.find()) {
            String completed = m.group(4);
            String total = m.group(5);
            if (completed.equals(total)) allTermsDone = true;

            if (!ModConfig.terminalTimes) return;
            long timeSinceLastSection = now -  (sectionTimes.isEmpty() ? p3Start : sectionTimes.get(sectionTimes.size() - 1));
            e.editMessage(e.original_message + String.format(" &8[&7%.2fs &8| &7%.2fs&8]&r", (float) timeSinceLastSection / 1000, (float) (now - p3Start) / 1000));
        }

        if (e.message.matches("^The Core entrance is opening!$")) {
            gateBlown = false;
            allTermsDone = false;

            sectionTimes.add(now);
            if (ModConfig.sendTerminalSplits && sectionTimes.size() >= 4) sendSectionTimes();
        }

        if (gateBlown && allTermsDone) {
            gateBlown = false;
            allTermsDone = false;
            sectionTimes.add(now);
        }
    }

    private void sendSectionTimes() {
        ChatUtils.sendModInfo(String.format("Terminals: &b1: &a%.2fs &7| &b2: &a%.2fs &7| &b3: &a%.2fs &7| &b4: &a%.2fs &7| &bTotal: &a%.2fs", (sectionTimes.get(0) - p3Start) / 1000.0, (sectionTimes.get(1) - sectionTimes.get(0)) / 1000.0, (sectionTimes.get(2) - sectionTimes.get(1)) / 1000.0, (sectionTimes.get(3) - sectionTimes.get(2)) / 1000.0, (sectionTimes.get(3) - p3Start) / 1000.0));
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        p3Start = -1;
        gateBlown = false;
        allTermsDone = false;
        sectionTimes.clear();
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        p3Start = -1;
        gateBlown = false;
        allTermsDone = false;
        sectionTimes.clear();
    }
}
