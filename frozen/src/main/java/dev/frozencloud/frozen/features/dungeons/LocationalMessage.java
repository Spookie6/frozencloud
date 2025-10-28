package dev.frozencloud.frozen.features.dungeons;

import dev.frozencloud.core.ModEnum;
import dev.frozencloud.frozen.config.ModConfig;
import dev.frozencloud.frozen.events.impl.ChatPacketEvent;
import dev.frozencloud.frozen.utils.ChatUtils;
import dev.frozencloud.core.overlaymanager.BooleanConfigBinding;
import dev.frozencloud.core.overlaymanager.OverlayManager;
import dev.frozencloud.core.overlaymanager.TextOverlay;
import dev.frozencloud.frozen.utils.skyblock.dungeon.DungeonEnums;
import dev.frozencloud.frozen.utils.skyblock.dungeon.DungeonUtils;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.frozencloud.frozen.Frozen.mc;

public class LocationalMessage {
    private final Pattern LOC_MSG_PATTERN = Pattern.compile("^Party >.* (\\w+): (At|Inside) (.+)(!)?$");

    private long time = -1;
    private DungeonEnums.Class clazz = DungeonEnums.Class.Unknown;
    private String action = null;
    private String loc = null;

    private final Map<DungeonEnums.LocationEnums, Boolean> messageSent = new HashMap<DungeonEnums.LocationEnums, Boolean>();

    public LocationalMessage() {
        OverlayManager.register(new TextOverlay(
                ModEnum.FROZEN,
                new BooleanConfigBinding(
                        () -> ModConfig.locationalMessages,
                        (val) -> ModConfig.locationalMessages = val
                ),
                "Location messages",
                this::getText,
                () -> DungeonUtils.getFloor().isFloor(7) && DungeonUtils.getInBoss(),
                "Healer is at Mid!"
        ));

        OverlayManager.register(new TextOverlay(
                ModEnum.FROZEN,
                new BooleanConfigBinding(
                        () -> ModConfig.debugOverlays,
                        (val) -> ModConfig.debugOverlays = val
                ),
                "Locational msgs debug overlay",
                () -> {
                    List<String> res = new ArrayList<>();
                    for (DungeonEnums.LocationEnums loc : this.messageSent.keySet()) {
                        res.add(loc.message + ": " + messageSent.get(loc));
                    }
                    return String.join("\n", res);
                },
                () -> true,
                "Loc debug overlay"
        ));

        for (DungeonEnums.LocationEnums loc : DungeonEnums.LocationEnums.values()) {
            this.messageSent.put(loc, true);
        }
    }

    private String getText() {
        if (time <= 0) return "";
        long remaining = time + ((long) (ModConfig.locationMessageTitleDuration * 1000)) - System.currentTimeMillis();
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
        if (!DungeonUtils.getInBoss() && !DungeonUtils.getFloor().isFloor(7)) return;
        if (!ModConfig.locationalMessages) return;

        Matcher matcher = LOC_MSG_PATTERN.matcher(e.message);

        if (matcher.find()) {
            DungeonEnums.DungeonPlayer player = DungeonUtils.getDungeonPlayers().stream().filter(x -> x.username.equalsIgnoreCase(matcher.group(1))).findFirst().orElse(null);
            DungeonEnums.LocationEnums loc = DungeonEnums.LocationEnums.getLocationEnumByMessage(matcher.group(2) + " " + matcher.group(3));

            if (loc == null || player == null || !loc.configSupplier.get()) return;

            if (!player.clazz.equals(loc.clazz) && !(loc.equals(DungeonEnums.LocationEnums.SAFE_SPOT_3) && player.clazz.equals(DungeonEnums.Class.BERSERK))) return;

            this.time = System.currentTimeMillis();
            this.action = matcher.group(2);
            this.loc = matcher.group(3);
            this.clazz = player.clazz;
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
        if (!e.phase.equals(TickEvent.Phase.END) || !ModConfig.locationalMessages || mc.theWorld == null || mc.thePlayer == null) return;
        if (!DungeonUtils.getInBoss() && !DungeonUtils.getFloor().isFloor(7)) return;

        BlockPos playerPos = mc.thePlayer.getPosition();

        for (DungeonEnums.LocationEnums loc: DungeonEnums.LocationEnums.values()) {
            if (loc.isAtLocation(playerPos) && !this.messageSent.get(loc) && loc.configSupplier.get()) {
                DungeonEnums.Class clazz = DungeonUtils.getCurrentDungeonPlayer().clazz;
                if (!clazz.equals(loc.clazz) && !(loc.equals(DungeonEnums.LocationEnums.SAFE_SPOT_3) && clazz.equals(DungeonEnums.Class.BERSERK))) return;
                ChatUtils.sendCommand("pc " + loc.message, false);
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
