package com.github.spookie6.frozen.features.hud;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.ChatUtils;
import com.github.spookie6.frozen.utils.gui.overlays.*;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonUtils;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.github.spookie6.frozen.events.impl.ChatPacketEvent;
import com.github.spookie6.frozen.utils.skyblock.Island;
import com.github.spookie6.frozen.utils.skyblock.LocationUtils;

import java.util.ArrayList;
import java.util.List;

public class MaskTimers {
    public MaskTimers() {
        OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.maskTimers,
                        (val) -> ModConfig.maskTimers = val
                ),
                "Mask timers",
                this::getText,
                () -> {
                    if (!LocationUtils.isInSkyblock) return false;
                    return !ModConfig.maskTimerDungeonsOnly || !LocationUtils.currentArea.isNotArea(Island.Dungeon);
                },
                "§9Bonzo:#§r§a" + ModConfig.maskTimerReadyTitle + "§r\n§fSpirit:#§r§a" + ModConfig.maskTimerReadyTitle + "§r\n§cPhoenix:#§r§a" + ModConfig.maskTimerReadyTitle + "§r"
                ).setRightAlign(new BooleanConfigBinding(() -> ModConfig.masktimersRightAlign, (val) -> ModConfig.masktimersRightAlign = val))
                .setExtraWidth(new IntegerConfigBinding(() -> ModConfig.maskTimersExtraWidth, (val) -> ModConfig.maskTimersExtraWidth = val)));

        OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.invincibilityTimer,
                        (val) -> ModConfig.invincibilityTimer = val
                ),
                "Invincibility timer",
                () -> String.format("%.2fs", (float) (poppedAt + 3000 - System.currentTimeMillis()) / 1000),
                () -> LocationUtils.isInSkyblock && (poppedAt + 3000) > System.currentTimeMillis(),
                "3.00s"
            )
        );
    }

    private static long bonzoTime = -1;
    private static long spiritTime = -1;
    private static long phoenixTime = -1;

    private static long poppedAt = -1;

    protected String getText() {
        List<String> lines = new ArrayList<>();

        long now = System.currentTimeMillis();

        if (LocationUtils.currentArea.isArea(Island.Dungeon)) lines.add("§9Bonzo:#§r" + (bonzoTime - now > 0 ? "§7" + String.format("%.1f", (float) (bonzoTime - now) / 1000) + "§r" : "§a" + ModConfig.maskTimerReadyTitle + "§r"));
        lines.add("§fSpirit:#§r" + (spiritTime - now > 0 ? "§7" + String.format("%.1f", (float) (spiritTime - now) / 1000) + "§r" : "§a" + ModConfig.maskTimerReadyTitle + "§r"));
        lines.add("§cPhoenix:#§r" + (phoenixTime - now > 0 ? "§7" + String.format("%.1f", (float) (phoenixTime - now) / 1000) + "§r" : "§a" + ModConfig.maskTimerReadyTitle + "§r"));

        return String.join("\n", lines);
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onChatReceive(ChatPacketEvent e) {
        long now = System.currentTimeMillis();
        String mask = "";
        switch (e.message) {
            case("Your (⚚)? Bonzo's Mask saved your life!"):
            case("Your Bonzo's Mask saved your life!"):
                bonzoTime = now + DungeonUtils.getAbilityCooldown(120000);
                poppedAt = System.currentTimeMillis();
                mask = "Bonzo";
                break;
            case("Second Wind Activated! Your Spirit Mask saved your life!"):
                spiritTime = now + DungeonUtils.getAbilityCooldown(30000);
                poppedAt = System.currentTimeMillis();
                mask = "Sprit";
                break;
            case("Your Phoenix Pet saved you from certain death!"):
                phoenixTime = now + DungeonUtils.getAbilityCooldown(60000);
                poppedAt = System.currentTimeMillis();
                mask = "Phoenix";
                break;
        }
        if (!mask.isEmpty() && ModConfig.maskTimerSendNoti) {
            ChatUtils.sendCommand("pc " + ModConfig.maskTimerChatMsg.replace("{mask}", mask), false);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        bonzoTime = -1;
        spiritTime = -1;
        phoenixTime = -1;
        poppedAt = -1;
    }
}
