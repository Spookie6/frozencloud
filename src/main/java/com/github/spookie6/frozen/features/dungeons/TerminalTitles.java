package com.github.spookie6.frozen.features.dungeons;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.events.impl.TitleEvent;
import com.github.spookie6.frozen.utils.StringUtils;
import com.github.spookie6.frozen.utils.TitleType;
import com.github.spookie6.frozen.utils.gui.overlays.BooleanConfigBinding;
import com.github.spookie6.frozen.utils.gui.overlays.OverlayManager;
import com.github.spookie6.frozen.utils.gui.overlays.TextOverlay;
import com.github.spookie6.frozen.utils.skyblock.Island;
import com.github.spookie6.frozen.utils.skyblock.LocationUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonEnums;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TerminalTitles {
    private static final Pattern terminalsTitlePattern = Pattern.compile("^(\\w{2,18}) (activated|completed) a (\\w+)! \\((\\d)\\/(\\d)\\)$");
    private String text = "";
    private int showingTicks = 0;

    public TerminalTitles() {
        OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.customTerminalTitles,
                        (val) -> ModConfig.customTerminalTitles = val
                ),
                "Terminal Title",
                this::getText,
                () -> DungeonUtils.getF7Phase().equals(DungeonEnums.M7Phases.P3) && showingTicks > 0,
                "Terminal &f(&a4/7&f)&r"
        ));
    }

    public String getText() {
        return text;
    }

    @SubscribeEvent
    public void onTitle(TitleEvent.Incoming event) {
        if (!ModConfig.customTerminalTitles || !LocationUtils.currentArea.isArea(Island.Dungeon) || event.getType() != TitleType.SUBTITLE) return;

        String raw = StringUtils.removeFormatting(event.getComponent().getUnformattedText());

        Matcher matcher = terminalsTitlePattern.matcher(raw);
        if (!matcher.find()) return;

        String completedThing = matcher.group(3);
        int progress = Integer.parseInt(matcher.group(4));
        int total = Integer.parseInt(matcher.group(5));

        StringBuilder sb = new StringBuilder();
        if (ModConfig.terminalTitlesStaticColor) sb.append(completedThing);
            else sb.append(applyThingColorCodes(completedThing));
        sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
        sb.append("&f(&a");
        sb.append(progress);
        sb.append("&f/&a");
        sb.append(total);
        sb.append("&f)&r");
        text = sb.toString();
        showingTicks = (int) (ModConfig.terminalTitleDuration * 20);

        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (showingTicks > 0) showingTicks--;
    }

    private String applyThingColorCodes(String thing) {
        switch(thing) {
            case "lever":
                return "&4Lever&r";
            case "terminal":
                return "&5Terminal&r";
            case "device":
                return "&3Device&r";
        }
        return thing;
    }
}
