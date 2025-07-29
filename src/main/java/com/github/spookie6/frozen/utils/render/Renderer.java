package com.github.spookie6.frozen.utils.render;

import cc.polyfrost.oneconfig.config.core.OneColor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

public class Renderer {
    private final static Tessellator tessellator = Tessellator.getInstance();
    private final static WorldRenderer wr = tessellator.getWorldRenderer();

    public static void drawOutlinedAABB(AxisAlignedBB aabb, OneColor color, boolean depth, boolean smoothLines, float thickness) {
        if (color.getAlpha() == 0) return;

        GlStateManager.pushMatrix();
        RenderUtils.preRender(true);

        if (smoothLines) {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        }

        GL11.glLineWidth(thickness);
        RenderUtils.setDepth(depth);
        GlStateManager.resetColor();
        GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        RenderUtils.addVertexesForOutlinedBox(aabb);
        tessellator.draw();

        if (smoothLines) GL11.glDisable(GL11.GL_LINE_SMOOTH);

        if (!depth) RenderUtils.resetDepth();
        GL11.glLineWidth(1f);
        RenderUtils.postRender();
        GlStateManager.popMatrix();
    }

    public static void drawFilledAABB(AxisAlignedBB aabb, OneColor color, boolean depth) {
        if (color.getAlpha() == 0) return;

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        RenderUtils.preRender(true);
        RenderUtils.setDepth(depth);
        GlStateManager.resetColor();
        GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        RenderUtils.addVertexesForFilledBox(aabb);
        tessellator.draw();

        if (!depth) RenderUtils.resetDepth();
        RenderUtils.postRender();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    public static void drawOutlinedBlock(BlockPos blockPos, OneColor color, boolean depth, boolean smoothLines, float thickness) {
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();

        AxisAlignedBB aabb = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
        drawOutlinedAABB(aabb, color, depth, smoothLines, thickness);
    }

    public static void drawFilledBlock(BlockPos blockPos, OneColor color, boolean depth) {
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();

        AxisAlignedBB aabb = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
        drawFilledAABB(aabb, color, depth);
    }

    public static void drawEntityAABB(Entity entity, OneColor color, boolean depth, boolean smoothLines, float thickness, float partialTicks) {
        drawOutlinedAABB(RenderUtils.getEntityRenderBoundingBox(entity, partialTicks), color, depth, smoothLines, thickness);
    }

    public enum STYLE {
        FILLED,
        OUTLINES,
        FILLED_OUTLINE
    }
}
