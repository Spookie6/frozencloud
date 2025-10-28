package dev.frozencloud.frozen.features.misc;

import dev.frozencloud.frozen.config.ModConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static dev.frozencloud.frozen.Frozen.mc;

public class PlayerRenderer {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onEntityRenderPre(RenderPlayerEvent.Pre e) {
        if (e.entity != mc.thePlayer) return;
        if (!ModConfig.customPlayerScale) return;
        GlStateManager.pushMatrix();

        double x = e.x;
        double y = e.y;
        double z = e.z;

        GlStateManager.translate(x, y, z);

        GlStateManager.scale(ModConfig.playerScaleX,ModConfig.playerScaleY,ModConfig.playerScaleZ);
        GlStateManager.translate(-x / ModConfig.playerScaleX, -y / ModConfig.playerScaleY, -z / ModConfig.playerScaleZ);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onEntityRenderPost(RenderPlayerEvent.Post e) {
        if (e.entity != mc.thePlayer) return;
        if (!ModConfig.customPlayerScale) return;
        GlStateManager.popMatrix();
    }
}

