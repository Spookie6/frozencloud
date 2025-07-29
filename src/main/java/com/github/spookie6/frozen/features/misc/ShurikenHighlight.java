package com.github.spookie6.frozen.features.misc;

import cc.polyfrost.oneconfig.config.core.OneColor;
import com.github.spookie6.frozen.utils.render.Renderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.github.spookie6.frozen.Frozen.mc;

public class ShurikenHighlight {
    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.theWorld == null || mc.getRenderViewEntity() == null) return;

        Entity viewer = mc.getRenderViewEntity();
        World world = mc.theWorld;

        for (Entity entity : world.loadedEntityList) {
            if (entity == null || entity == viewer) continue;

            try {
                if (!entity.hasCustomName()) continue;
                String name = entity.getCustomNameTag();

                if (name != null && name.endsWith("✯")) {
                    System.out.println("[ESP] Found shuriken entity: " +
                            entity.getClass().getSimpleName() + " – " + name);
                    Renderer.drawEntityAABB(entity, new OneColor(0, 0, 255, 255), true, true, 2.5f, event.partialTicks);
                }
            } catch (Exception ex) {
                System.err.println("ESP: Failed to handle entity: " + entity.getClass().getName());
                ex.printStackTrace();
            }
        }
    }
}
