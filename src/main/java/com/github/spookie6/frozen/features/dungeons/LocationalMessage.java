package com.github.spookie6.frozen.features.dungeons;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.events.impl.ChatPacketEvent;
import com.github.spookie6.frozen.utils.ChatUtils;
import com.github.spookie6.frozen.utils.gui.overlays.BooleanConfigBinding;
import com.github.spookie6.frozen.utils.gui.overlays.OverlayManager;
import com.github.spookie6.frozen.utils.gui.overlays.TextOverlay;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonEnums;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonUtils;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.spookie6.frozen.Frozen.mc;

public class LocationalMessage {
    private final Pattern LOC_MSG_PATTERN = Pattern.compile("^Party >.* (\\w+): (At|Inside) (.+)(!)?$");

    private long time = -1;
    private DungeonEnums.Class clazz = DungeonEnums.Class.Unknown;
    private String action = null;
    private String loc = null;

    private final Map<DungeonEnums.LocationEnums, Boolean> messageSent = new HashMap<DungeonEnums.LocationEnums, Boolean>();

    public LocationalMessage() {
        OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.locationalMessages,
                        (val) -> ModConfig.locationalMessages = val
                ),
                "Location messages",
                this::getText,
                () -> DungeonUtils.getF7Phase().equals(DungeonEnums.M7Phases.P3),
                "Healer is at Mid!"
        ));

        for (DungeonEnums.LocationEnums loc : DungeonEnums.LocationEnums.values()) {
            this.messageSent.put(loc, true);
        }
    }

    private String getText() {
        if (time <= 0) return "";
        long remaining = time + (long) ModConfig.locationMessageTitleDuration * 1000 - System.currentTimeMillis();
        if (remaining <= 0) {
            this.time = -1;
            this.action = null;
            this.clazz = null;
            this.loc = null;
            return "";
        }

        if (this.action == null || this.loc == null || this.clazz == null) return "";
        return String.format("%s is %s %s", this.clazz.toString(), this.action, this.loc);
    }

    @SubscribeEvent
    public void onChatPacket(ChatPacketEvent e) {
        if (!DungeonUtils.getF7Phase().equals(DungeonEnums.M7Phases.P3)) return;

        Matcher matcher = LOC_MSG_PATTERN.matcher(e.message);

        if (matcher.find()) {
            DungeonEnums.DungeonPlayer player = DungeonUtils.getDungeonPlayers().stream().filter(x -> x.username.equals(matcher.group(1))).findFirst().orElse(null);
            if (player != null) {
                this.time = System.currentTimeMillis();
                this.action = matcher.group(2);
                this.loc = matcher.group(3);
                this.clazz = player.clazz;
            }
        }

        if (e.message.equals("[BOSS] Goldor: Who dares trespass into my domain?")) {
            this.messageSent.forEach((x, y) -> this.messageSent.put(x, false));
        }
        if (e.message.equals("The Core entrance is opening!")) {
            this.messageSent.forEach((x, y) -> this.messageSent.put(x, true));
            this.messageSent.put(DungeonEnums.LocationEnums.MID, false);
        }
        if (e.message.equals("[BOSS] Storm: I'd be happy to show you what that's like!")) {
            this.messageSent.put(DungeonEnums.LocationEnums.SIMONSAYS, false);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (!e.phase.equals(TickEvent.Phase.END)) return;
        if (!DungeonUtils.getF7Phase().equals(DungeonEnums.M7Phases.P3)) return;

        BlockPos playerPos = mc.thePlayer.getPosition();

        for (DungeonEnums.LocationEnums loc: DungeonEnums.LocationEnums.values()) {
            if (loc.isAtLocation(playerPos) && loc.clazz.equals(DungeonUtils.getCurrentDungeonPlayer().clazz) && !this.messageSent.get(loc)) {
                ChatUtils.sendCommand("pc " + loc.message, true);
                this.messageSent.put(loc, true);
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        this.time = -1;
        this.action = null;
        this.clazz = null;
        this.loc = null;
        this.messageSent.forEach((x, y) -> this.messageSent.put(x, true));
    }
}
