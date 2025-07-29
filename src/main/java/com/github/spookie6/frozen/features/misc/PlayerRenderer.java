package com.github.spookie6.frozen.features.misc;

import com.github.spookie6.frozen.config.ModConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.github.spookie6.frozen.Frozen.mc;

public class PlayerRenderer {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onEntityRenderPre(RenderPlayerEvent.Pre e) {
        if (!ModConfig.customPlayerScale || e.entity != mc.thePlayer) return;
        GlStateManager.pushMatrix();

        double x = e.x;
        double y = e.y;
        double z = e.z;

        GlStateManager.translate(x, y, z);

        GlStateManager.scale(ModConfig.playerScale,ModConfig.playerScale,ModConfig.playerScale);
        GlStateManager.translate(-x / ModConfig.playerScale, -y / ModConfig.playerScale, -z / ModConfig.playerScale);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onEntityRenderPost(RenderPlayerEvent.Post e) {
        if (!ModConfig.customPlayerScale || e.entity != mc.thePlayer) return;
        GlStateManager.popMatrix();
    }
}

