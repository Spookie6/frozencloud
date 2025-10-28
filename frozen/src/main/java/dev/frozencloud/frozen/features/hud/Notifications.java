package dev.frozencloud.frozen.features.hud;

import dev.frozencloud.core.ModEnum;
import dev.frozencloud.frozen.config.ModConfig;
import dev.frozencloud.frozen.events.impl.ChatPacketEvent;
import dev.frozencloud.core.overlaymanager.BooleanConfigBinding;
import dev.frozencloud.core.overlaymanager.OverlayManager;
import dev.frozencloud.core.overlaymanager.TextOverlay;
import dev.frozencloud.frozen.utils.skyblock.dungeon.DungeonEnums;
import dev.frozencloud.frozen.utils.skyblock.dungeon.DungeonUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.stream.Stream;

public class Notifications {
    private TextOverlay overlay;
    private String text = "";
    private long endTime = -1;

    public Notifications() {
        this.overlay = new TextOverlay(
                ModEnum.FROZEN,
                new BooleanConfigBinding(() -> ModConfig.notifications, (val) -> ModConfig.notifications = val),
                "Notification",
                () -> text,
                () -> endTime != -1,
                "WISH"
        );

        OverlayManager.register(overlay);
    }

    @SubscribeEvent
    public void onChatPacket(ChatPacketEvent e) {
        long now = System.currentTimeMillis();
        if (!DungeonUtils.getInBoss()) return;

        DungeonEnums.DungeonPlayer player = DungeonUtils.getCurrentDungeonPlayer();
        if (player == null) return;
        DungeonEnums.Class clazz = player.clazz;

        if ((e.message.matches("⚠ Maxor is enraged! ⚠") || e.message.matches("[BOSS] Goldor: You have done it, you destroyed the factory…")) && ModConfig.notificationsWish && clazz.isClass(DungeonEnums.Class.HEALER)) {
            text = "WISH";
            endTime = now + (long)(ModConfig.notificationsTitleDuration * 1000.0);
        }

        if (e.message.matches("Your Wish healed your entire team for \\d+ health and shielded them for \\d+!") && ModConfig.notificationsWished) {
            text = "WISHED";
            endTime = now + (long)(ModConfig.notificationsTitleDuration * 1000.0);
        }

        if (e.message.matches("⚠ Storm is enraged! ⚠") && ModConfig.notificationsUpcrush && clazz.isClass(DungeonEnums.Class.TANK)) {
            text = "CRUSH";
            endTime = now + (long)(ModConfig.notificationsTitleDuration * 1000.0);
        }

        if (e.message.matches("⚠ Maxor is enraged! ⚠") && ModConfig.notificationsCastle && clazz.isClass(DungeonEnums.Class.TANK)) {
            text = "CASTLE";
            endTime = now + (long)(ModConfig.notificationsTitleDuration * 1000.0);
        }

        if (e.message.matches("[BOSS] Wither King: I no longer wish to fight, but I know that will not stop you.")
                && Stream.of(DungeonEnums.Class.BERSERK, DungeonEnums.Class.ARCHER, DungeonEnums.Class.MAGE).anyMatch(x -> x.equals(clazz))) {
            text = "RAG";
            endTime = now + (long)(ModConfig.notificationsTitleDuration * 1000);
        }
    }

    @SubscribeEvent
    public void onRenderHud(RenderGameOverlayEvent e) {
        long now = System.currentTimeMillis();

        if (now >= endTime - 50) {
            endTime = -1;
        }

        // No active notification → leave overlay fully transparent
        if (endTime == -1) {
            overlay.setColor(new Color(
                    overlay.getColor().getRed(),
                    overlay.getColor().getGreen(),
                    overlay.getColor().getBlue(),
                    0)); // transparent
            return;
        }

        // No fade-out: stay solid while active
        if (!ModConfig.notificationsFadeOut) {
            overlay.setColor(new Color(
                    overlay.getColor().getRed(),
                    overlay.getColor().getGreen(),
                    overlay.getColor().getBlue(),
                    255));
            return;
        }

        // Fade-out: linear over full duration
        long duration = (long) (ModConfig.notificationsTitleDuration * 1000);
        long remaining = endTime - now;

        float progress = Math.max(0f, Math.min(1f, remaining / (float) duration));
        int alpha = (int) (255 * progress);

        Color base = overlay.getColor();
        overlay.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha));
    }
}
