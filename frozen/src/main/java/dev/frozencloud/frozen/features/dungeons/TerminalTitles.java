package dev.frozencloud.frozen.features.dungeons;

import dev.frozencloud.frozen.config.ModConfig;
import dev.frozencloud.frozen.events.impl.TitleEvent;
import dev.frozencloud.frozen.utils.StringUtils;
import dev.frozencloud.frozen.utils.TitleType;
import dev.frozencloud.frozen.utils.gui.overlays.BooleanConfigBinding;
import dev.frozencloud.frozen.utils.gui.overlays.Overlay;
import dev.frozencloud.frozen.utils.gui.overlays.OverlayManager;
import dev.frozencloud.frozen.utils.gui.overlays.TextOverlay;
import dev.frozencloud.frozen.utils.skyblock.Island;
import dev.frozencloud.frozen.utils.skyblock.LocationUtils;
import dev.frozencloud.frozen.utils.skyblock.dungeon.DungeonEnums;
import dev.frozencloud.frozen.utils.skyblock.dungeon.DungeonUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TerminalTitles {
    private static final Pattern terminalsTitlePattern = Pattern.compile("^(\\w{2,18}) (activated|completed) a (\\w+)! \\((\\d)\\/(\\d)\\)$");
    private String completedThing = "";
    private String text = "";
    private int showingTicks = 0;

    public TerminalTitles() {
        OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.customTerminalTitles,
                        (val) -> ModConfig.customTerminalTitles = val
                ),
                "Terminal Title",
                () -> text,
                () -> DungeonUtils.getF7Phase().equals(DungeonEnums.M7Phases.P3) && showingTicks > 0,
                " (4/7)"
        ).setTitleSupplier(this::getTitle));
    }

    public String getTitle() {
        Overlay overlay = OverlayManager.getOverlay("terminal_title");

        if (overlay.getInEditMode()) {
            return "Terminal";
        } else {
            return this.completedThing;
        }
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
        sb.append(" ");
        if (!ModConfig.terminalTitlesStaticColor) sb.append("&f");
        sb.append("(");
        if (!ModConfig.terminalTitlesStaticColor) sb.append("&a");
        sb.append(progress);
        sb.append("/");
        sb.append(total);
        if (!ModConfig.terminalTitlesStaticColor) sb.append("&f");
        sb.append(")");
        text = sb.toString();
        showingTicks = (int) (ModConfig.terminalTitleDuration * 20);

        this.completedThing = (ModConfig.terminalTitlesStaticColor) ? completedThing : applyThingColorCodes(completedThing);
        this.completedThing = this.completedThing.substring(0, 1).toUpperCase() + this.completedThing.substring(1);
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
