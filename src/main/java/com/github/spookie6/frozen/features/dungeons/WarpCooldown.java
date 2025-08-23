package com.github.spookie6.frozen.features.dungeons;

import com.github.spookie6.frozen.utils.gui.overlays.BooleanConfigBinding;
import com.github.spookie6.frozen.utils.gui.overlays.OverlayManager;
import com.github.spookie6.frozen.utils.gui.overlays.TextOverlay;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.events.impl.ChatPacketEvent;

import java.util.Collections;

public class WarpCooldown  {

    public WarpCooldown() {
        OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.warpCooldown,
                        (val) -> ModConfig.warpCooldown = val
                ),
                "Warp cooldown",
                () -> String.format("%.2fs", (float) (lastWarp + 30000 - System.currentTimeMillis()) / 1000),
                    () -> lastWarp + 30000 > System.currentTimeMillis(),
                    "30s"
            ).setTitleSupplier(() -> ModConfig.warpCooldownTitle)
        );
    }

    private long lastWarp = -1;

    @SubscribeEvent(receiveCanceled = true)
    public void onChatPacket(ChatPacketEvent e) {
        if (e.message.matches("^-*\\n\\[[^]]+] (\\w+) entered (?:MM )?\\w+ Catacombs, (Floor (\\w+)|Entrance)!\\n-*$") && lastWarp + 30000 < System.currentTimeMillis()) {
            lastWarp = System.currentTimeMillis();
        }
    }
}
