package com.github.spookie6.frozen.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderUtils {
    public static ResourceLocation beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");
    public static Tessellator tessellator = Tessellator.getInstance();
    public static WorldRenderer wr = tessellator.getWorldRenderer();


    private static double getEntityRenderX (Entity entity, float partialTicks) {
        return entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
    }

    private static double getEntityRenderY (Entity entity, float partialTicks) {
        return entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
    }

    private static double getEntityRenderZ (Entity entity, float partialTicks) {
        return entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
    }

    public static Vec3 getEntityRenderVec(Entity entity, float partialTicks) {
        return new Vec3(
                getEntityRenderX(entity, partialTicks),
                getEntityRenderY(entity, partialTicks),
                getEntityRenderZ(entity, partialTicks));
    }

    public static AxisAlignedBB getEntityRenderBoundingBox(Entity entity, float partialTicks) {
        return new AxisAlignedBB(
                getEntityRenderX(entity, partialTicks) - entity.width / 2,
                getEntityRenderY(entity, partialTicks),
                getEntityRenderZ(entity, partialTicks) - entity.width / 2,
                getEntityRenderX(entity, partialTicks) + entity.width / 2,
                getEntityRenderY(entity, partialTicks) + entity.height,
                getEntityRenderZ(entity, partialTicks) + entity.width / 2
        );
    }

    public static AxisAlignedBB getAABBOutlineBounds(AxisAlignedBB axisAlignedBB) {
        return axisAlignedBB.expand(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026);
    }

    public static void preRender(boolean disableTexture2D) {
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        if (disableTexture2D) GlStateManager.disableTexture2D(); else GlStateManager.enableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.translate(-renderManager.viewerPosX, -renderManager.viewerPosY, -renderManager.viewerPosZ);
    }

    public static void postRender() {
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.resetColor();
    }

    public static void setDepth(boolean depth) {
        if (depth) GlStateManager.enableDepth(); else GlStateManager.disableDepth();
        GlStateManager.depthMask(depth);
    }

    public static void resetDepth() {
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
    }

    public static void addVertexesForFilledBox(AxisAlignedBB box) {
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        // Bottom face (minY)
        wr.pos(box.minX, box.minY, box.minZ).endVertex();
        wr.pos(box.maxX, box.minY, box.minZ).endVertex();
        wr.pos(box.maxX, box.minY, box.maxZ).endVertex();
        wr.pos(box.minX, box.minY, box.maxZ).endVertex();

        // Top face (maxY)
        wr.pos(box.minX, box.maxY, box.minZ).endVertex();
        wr.pos(box.minX, box.maxY, box.maxZ).endVertex();
        wr.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        wr.pos(box.maxX, box.maxY, box.minZ).endVertex();

        // Front face (minZ)
        wr.pos(box.minX, box.minY, box.minZ).endVertex();
        wr.pos(box.minX, box.maxY, box.minZ).endVertex();
        wr.pos(box.maxX, box.maxY, box.minZ).endVertex();
        wr.pos(box.maxX, box.minY, box.minZ).endVertex();

        // Back face (maxZ)
        wr.pos(box.minX, box.minY, box.maxZ).endVertex();
        wr.pos(box.maxX, box.minY, box.maxZ).endVertex();
        wr.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        wr.pos(box.minX, box.maxY, box.maxZ).endVertex();

        // Left face (minX)
        wr.pos(box.minX, box.minY, box.minZ).endVertex();
        wr.pos(box.minX, box.minY, box.maxZ).endVertex();
        wr.pos(box.minX, box.maxY, box.maxZ).endVertex();
        wr.pos(box.minX, box.maxY, box.minZ).endVertex();

        // Right face (maxX)
        wr.pos(box.maxX, box.minY, box.minZ).endVertex();
        wr.pos(box.maxX, box.maxY, box.minZ).endVertex();
        wr.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        wr.pos(box.maxX, box.minY, box.maxZ).endVertex();
    }

    public static void addVertexesForOutlinedBox(AxisAlignedBB box) {
        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        // Bottom face edges (connecting adjacent corners)
        wr.pos(box.minX, box.minY, box.minZ).endVertex();  // (0)
        wr.pos(box.maxX, box.minY, box.minZ).endVertex();  // (1)

        wr.pos(box.maxX, box.minY, box.minZ).endVertex();  // (1)
        wr.pos(box.maxX, box.minY, box.maxZ).endVertex();  // (2)

        wr.pos(box.maxX, box.minY, box.maxZ).endVertex();  // (2)
        wr.pos(box.minX, box.minY, box.maxZ).endVertex();  // (3)

        wr.pos(box.minX, box.minY, box.maxZ).endVertex();  // (3)
        wr.pos(box.minX, box.minY, box.minZ).endVertex();  // (0)

        // Top face edges (connecting adjacent corners)
        wr.pos(box.minX, box.maxY, box.minZ).endVertex();  // (4)
        wr.pos(box.maxX, box.maxY, box.minZ).endVertex();  // (5)

        wr.pos(box.maxX, box.maxY, box.minZ).endVertex();  // (5)
        wr.pos(box.maxX, box.maxY, box.maxZ).endVertex();  // (6)

        wr.pos(box.maxX, box.maxY, box.maxZ).endVertex();  // (6)
        wr.pos(box.minX, box.maxY, box.maxZ).endVertex();  // (7)

        wr.pos(box.minX, box.maxY, box.maxZ).endVertex();  // (7)
        wr.pos(box.minX, box.maxY, box.minZ).endVertex();  // (4)

        // Vertical edges (connecting bottom to top)
        wr.pos(box.minX, box.minY, box.minZ).endVertex();  // (0)
        wr.pos(box.minX, box.maxY, box.minZ).endVertex();  // (4)

        wr.pos(box.maxX, box.minY, box.minZ).endVertex();  // (1)
        wr.pos(box.maxX, box.maxY, box.minZ).endVertex();  // (5)

        wr.pos(box.maxX, box.minY, box.maxZ).endVertex();  // (2)
        wr.pos(box.maxX, box.maxY, box.maxZ).endVertex();  // (6)

        wr.pos(box.minX, box.minY, box.maxZ).endVertex();  // (3)
        wr.pos(box.minX, box.maxY, box.maxZ).endVertex();  // (7)
    }
}
