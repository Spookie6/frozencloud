package dev.frozencloud.frozen.features.hud;

import dev.frozencloud.frozen.config.ModConfig;
import dev.frozencloud.frozen.utils.gui.overlays.BooleanConfigBinding;
import dev.frozencloud.frozen.utils.gui.overlays.OverlayManager;
import dev.frozencloud.frozen.utils.gui.overlays.TextOverlay;
import dev.frozencloud.frozen.utils.skyblock.LocationUtils;
import net.minecraft.client.entity.EntityPlayerSP;

import static dev.frozencloud.frozen.Frozen.mc;

public class Speed {

    public Speed() {
        OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.speedHud,
                        (val) -> ModConfig.speedHud = val
                ),
                "Speed",
                this::getText,
                () -> LocationUtils.isInSkyblock,
        "✦500"
            )
        );
    }

    public String getText() {
        EntityPlayerSP player = mc.thePlayer;
        int speed = 0;
        try {
            float walkSpeed = player.capabilities.getWalkSpeed();
            speed = (int) Math.floor(walkSpeed * 1000);
        } catch (NullPointerException ignored) {;}

        return "✦" + speed;
    }
}