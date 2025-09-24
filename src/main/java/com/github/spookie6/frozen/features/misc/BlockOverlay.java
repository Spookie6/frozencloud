package com.github.spookie6.frozen.features.misc;

import cc.polyfrost.oneconfig.config.core.OneColor;
import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.render.Renderer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.github.spookie6.frozen.Frozen.mc;

public class BlockOverlay {
    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent e) {
        if (ModConfig.blockOverlay) e.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent e) {
        if (mc.gameSettings.hideGUI || !ModConfig.blockOverlay || mc.objectMouseOver == null) return;

        if (mc.objectMouseOver.typeOfHit != null && mc.objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.BLOCK)) {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            OneColor fillColor = ModConfig.blockOverlayFillColor;
            OneColor lineColor = ModConfig.blockOverlayLineColor;
            boolean depthCheck = ModConfig.blockOverlayDepthCheck;
            boolean smoothLines = ModConfig.blockOverlaySmoothLines;

            Renderer.STYLE renderStyle = Renderer.STYLE.values()[ModConfig.blockOverlayRenderType];

            Renderer.drawBlockBox(
                    blockPos,
                    renderStyle == Renderer.STYLE.OUTLINED || renderStyle == Renderer.STYLE.FILLED_OUTLINE, // outline
                    renderStyle == Renderer.STYLE.FILLED || renderStyle == Renderer.STYLE.FILLED_OUTLINE,   // fill
                    fillColor,
                    lineColor,
                    depthCheck,
                    smoothLines,
                    ModConfig.blockOverlayLineWidth
            );
        }
    }
}
