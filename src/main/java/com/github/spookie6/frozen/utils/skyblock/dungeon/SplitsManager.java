package com.github.spookie6.frozen.utils.skyblock.dungeon;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.skyblock.Island;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.github.spookie6.frozen.events.impl.ChatPacketEvent;
import com.github.spookie6.frozen.events.impl.ServerTickEvent;
import com.github.spookie6.frozen.utils.ChatUtils;
import com.github.spookie6.frozen.utils.StringUtils;
import com.github.spookie6.frozen.utils.skyblock.LocationUtils;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SplitsManager {
    public static Split currentSplit = Split.Unknown;
    private static long[] runStarted = {0, 0};
    private static long ticks = 0;
    private static boolean firstTick = false;

    private static boolean showMins = false;
    private static boolean showTickMins = false;

    private final static LinkedHashMap<Split, long[]> splits = new LinkedHashMap<>();

    public static void initialize(DungeonEnums.Floor floor) {
        if (isInitialized()) return;

        if (ModConfig.debugMessages) ChatUtils.sendModInfo("Splitsmanager initializing for floor: " + floor);

//        Defining splits for this run.
        for (Split split : Arrays.asList(Split.BloodOpened, Split.BloodCleared, Split.Portal, Split.BossEntry)) {
            splits.put(split, new long[]{0L, 0L});
        }

        if (floor.isFloor(7)) {
            for (Split split : Arrays.asList(Split.Maxor, Split.Storm, Split.Terminals, Split.Goldor, Split.Necron)) {
                splits.put(split, new long[]{0L, 0L});
            }
            if (floor.isMM) {
                splits.put(Split.Dragons, new long[]{0L, 0L});
            }
        }

        if (ModConfig.showBossSplit == 0 || (ModConfig.showBossSplit == 2 && !floor.isFloor(7) && !floor.isFloor(0)) || (ModConfig.showBossSplit == 3 && floor.isFloor(7))) {
            splits.put(Split.Boss, new long[]{0L, 0L});
        }
        currentSplit = Split.BloodOpened;
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onChatPacket(ChatPacketEvent e) {
        if (!isInitialized()) return;
        long now = System.currentTimeMillis();

        Pattern startRegex = Pattern.compile("\\[NPC] Mort: Here, I found this map when I first entered the dungeon\\.|\\[NPC] Mort: Right-click the Orb for spells, and Left-click \\(or Drop\\) to use your Ultimate");
        if (startRegex.matcher(ChatFormatting.stripFormatting(e.message)).find() && runStarted[0] <= 0) runStarted[0] = now;

        if (currentSplit.regex.matcher(ChatFormatting.stripFormatting(e.message)).find()) {
            splits.put(currentSplit, new long[]{now, ticks});
            if (currentSplit.equals(Split.Portal)) {
                LocationUtils.currentDungeon.inBoss = true;
                splits.put(Split.BossEntry, new long[]{now, ticks});
            }
            if (currentSplit.equals(Split.Dragons) && splits.get(Split.Boss) != null) {
                splits.put(Split.Boss, new long[]{now, ticks});
            }
            if (currentSplit.equals(Split.Boss) || currentSplit.equals(Split.Dragons)) {
                sendAllSplitsToChat();
                currentSplit = Split.Unknown;
                return;
            }
            if (ModConfig.sendSplits) ChatUtils.sendModInfo(currentSplit.name + " &7took " + getFormattedSplitTime(currentSplit));

            // Set new current split
            List<Split> remaining = splits.keySet().stream()
                    .filter(x -> splits.get(x)[0] == 0)
                    .collect(Collectors.toList());

            if (!remaining.isEmpty()) {
                currentSplit = remaining.get(0);
            } else {
                currentSplit = Split.Unknown; // Done
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event) {
        if (!isInitialized()) return;
        if (!firstTick && runStarted[0] > 0) {
            ticks = 0;
            firstTick = true;
        }
        if (firstTick) ticks++;
    }

    public static long[] getSplitTime(Split split) {
        if (runStarted[0] <= 0) return new long[]{0, 0};

        if (split.equals(Split.BloodOpened)) {
            if (splits.get(Split.BloodOpened)[0] > 0) return new long[]{splits.get(Split.BloodOpened)[0] - runStarted[0], splits.get(Split.BloodOpened)[1]};
            return new long[]{System.currentTimeMillis() - runStarted[0], ticks};
        }

        if (split.equals(Split.BossEntry)) {
            if (splits.get(Split.BossEntry)[0] > 0) return new long[]{splits.get(Split.BossEntry)[0] - runStarted[0], splits.get(Split.BossEntry)[1]};
            return new long[]{System.currentTimeMillis() - runStarted[0], ticks};
        }

        if (split.equals(Split.Boss)) {
            if (!DungeonUtils.getInBoss()) return new long[]{0, 0};
            if (splits.get(Split.Boss)[0] > 0) return new long[]{(splits.get(Split.Boss)[0] - splits.get(Split.BossEntry)[0]), (splits.get(Split.Boss)[1] - splits.get(Split.BossEntry)[1])};
            return new long[]{(System.currentTimeMillis() - splits.get(Split.BossEntry)[0]), (ticks - splits.get(Split.BossEntry)[1])};
        }

        long[] pValue = {0, 0};
        for (Split spl : splits.keySet()) {
            if (split == spl) {
                if (spl == currentSplit) return new long[]{(System.currentTimeMillis() - pValue[0]), ticks - pValue[1]};
                if (splits.get(spl)[0] > 0) return new long[]{(splits.get(spl)[0] - pValue[0]), splits.get(spl)[1] - pValue[1]};
            }
            pValue = splits.get(spl);
        }
        return new long[]{0, 0};
    }

    public static String getFormattedSplitTime(Split split) {
        long[] time = getSplitTime(split);
        return String.format("§a%s§r §8[§7%s§r§8]§r",
                StringUtils.formatTime((float) time[0] / 1000, showMins),
                StringUtils.formatTime((float) time[1] / 20, showTickMins)
        );
    }

    public static String getText() {
        if (!isInitialized()) return "";
        List<String> lines = new ArrayList<>();
        for (Split split : splits.keySet()) {
            lines.add(split.name + '#' + getFormattedSplitTime(split));
        }
        return String.join("\n", lines);
    }

    public void sendAllSplitsToChat() {
        for (Split split : splits.keySet()) {
            ChatUtils.sendModInfo(split.name + " &7took " + getFormattedSplitTime(split));
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {reset();}
    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {reset();}

    private static void reset() {
        runStarted = new long[]{0, 0};
        ticks = 0;
        firstTick = false;
        currentSplit = Split.Unknown;

        splits.clear();
    }

    public static boolean isInitialized() {
        return !currentSplit.equals(Split.Unknown);
    }

    public enum Split {
        BloodOpened(Pattern.compile("^\\[BOSS] The Watcher: (Congratulations, you made it through the Entrance\\.|Ah, you've finally arrived\\.|Ah, we meet again\\.\\.\\.|So you made it this far\\.\\.\\. interesting\\.|You've managed to scratch and claw your way here, eh\\?|I'm starting to get tired of seeing you around here\\.\\.\\.|Oh\\.\\. hello\\?|Things feel a little more roomy now, eh\\?)|^The BLOOD DOOR has been opened!"), "§4Blood Open§r"),
        BloodCleared(Pattern.compile("\\[BOSS] The Watcher: You have proven yourself\\. You may pass\\."), "§cBlood Clear§r"),
        Portal(Pattern.compile("^\\[BOSS] (Bonzo: Gratz for making it this far, but I'm basically unbeatable\\.|Scarf: This is where the journey ends for you, Adventurers\\.|The Professor: I was burdened with terrible news recently\\.\\.\\.|Thorn: Welcome Adventurers! I am Thorn, the Spirit! And host of the Vegan Trials!|Livid: Welcome, you've arrived right on time\\. I am Livid, the Master of Shadows\\.|Sadan: So you made it all the way here\\.\\.\\. Now you wish to defy me\\? Sadan\\?!|Maxor: WELL! WELL! WELL! LOOK WHO'S HERE!)"), "§dPortal§r"),
        BossEntry(Pattern.compile("^\\[BOSS] (Bonzo: Gratz for making it this far, but I'm basically unbeatable\\.|Scarf: This is where the journey ends for you, Adventurers\\.|The Professor: I was burdened with terrible news recently\\.\\.\\.|Thorn: Welcome Adventurers! I am Thorn, the Spirit! And host of the Vegan Trials!|Livid: Welcome, you've arrived right on time\\. I am Livid, the Master of Shadows\\.|Sadan: So you made it all the way here\\.\\.\\. Now you wish to defy me\\? Sadan\\?!|Maxor: WELL! WELL! WELL! LOOK WHO'S HERE!)"), "§9Boss Entry§r"),
        Maxor(Pattern.compile("\\[BOSS] Storm: Pathetic Maxor, just like expected\\."), "§5Maxor§r"),
        Storm(Pattern.compile("\\[BOSS] Goldor: Who dares trespass into my domain\\?"), "§3Storm§r"),
        Terminals(Pattern.compile("The Core entrance is opening!"), "§eTerminals§r"),
        Goldor(Pattern.compile("\\[BOSS] Necron: You went further than any human before, congratulations\\."), "§6Goldor§r"),
        Necron(Pattern.compile("\\[BOSS] Necron: All this, for nothing\\.\\.\\."), "§cNecron§r"),
        Dragons(Pattern.compile("^\\s*☠ Defeated (.+) in 0?([\\dhms ]+?)\\s*(\\(NEW RECORD!\\))?$"), "§4Dragons§r"),
        Boss(Pattern.compile("^\\s*☠ Defeated (.+) in 0?([\\dhms ]+?)\\s*(\\(NEW RECORD!\\))?$"), "§bBoss§r"),
        Unknown(Pattern.compile(""), "");

        public final Pattern regex;
        public final String name;

        Split(Pattern regex, String name) {
            this.regex = regex;
            this.name = name;
        }

        public DungeonEnums.M7Phases getM7Phase() {
            if (equals(Split.Maxor)) return DungeonEnums.M7Phases.P1;
            if (equals(Split.Storm)) return DungeonEnums.M7Phases.P2;
            if (equals(Split.Terminals)) return DungeonEnums.M7Phases.P3;
            if (equals(Split.Goldor)) return DungeonEnums.M7Phases.P3;
            if (equals(Split.Necron)) return DungeonEnums.M7Phases.P4;
            if (equals(Split.Dragons)) return DungeonEnums.M7Phases.P5;
            return DungeonEnums.M7Phases.UNKOWN;
        }
    }

}
