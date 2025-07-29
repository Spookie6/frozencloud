package com.github.spookie6.frozen.features.hud;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.overlays.BooleanConfigBinding;
import com.github.spookie6.frozen.utils.overlays.OverlayManager;
import com.github.spookie6.frozen.utils.overlays.TextOverlay;
import com.github.spookie6.frozen.utils.skyblock.LocationUtils;
import net.minecraft.client.entity.EntityPlayerSP;

import static com.github.spookie6.frozen.Frozen.mc;

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