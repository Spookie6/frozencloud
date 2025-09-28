package dev.frozencloud.frozen.features.dungeons;

import dev.frozencloud.frozen.utils.gui.overlays.BooleanConfigBinding;
import dev.frozencloud.frozen.utils.gui.overlays.OverlayManager;
import dev.frozencloud.frozen.utils.gui.overlays.TextOverlay;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import dev.frozencloud.frozen.config.ModConfig;
import dev.frozencloud.frozen.events.impl.ChatPacketEvent;

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
