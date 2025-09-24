package com.github.spookie6.frozen.utils.render;

import cc.polyfrost.oneconfig.config.core.OneColor;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import static com.github.spookie6.frozen.Frozen.mc;
import static com.github.spookie6.frozen.utils.render.RenderUtils.*;
import static com.github.spookie6.frozen.utils.skyblock.WorldUtils.getBlockAt;
import static net.minecraftforge.client.ForgeHooksClient.preDraw;

public class Renderer {
    private final static RenderManager renderManager = mc.getRenderManager();
    private final static Tessellator tessellator = Tessellator.getInstance();
    private final static WorldRenderer wr = tessellator.getWorldRenderer();

    private static boolean blockOverlayMode = false;

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
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

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
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

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

    public static void drawBlockBox(BlockPos blockPos, boolean outline, boolean fill,
                                    OneColor overlayColor, OneColor outlineColor,
                                    boolean depth, boolean smoothLines, float lineWidth) {
        if (!outline && !fill) {
            throw new IllegalArgumentException("outline and fill cannot both be false");
        }

        EntityPlayerSP player = mc.thePlayer;
        if (player == null) return;

        // Use block center for distance calc
        Vec3 blockCenter = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        double distance = distance3D(blockCenter, player.getPositionVector());

        float adjustedLineWidth = (float) Math.max(
                0.5,
                Math.min(lineWidth, lineWidth / (distance / 8f))
        );

        double x = blockPos.getX();
        double y = blockPos.getY();
        double z = blockPos.getZ();

        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1).expand(0.002, 0.002, 0.002);
        Block block = getBlockAt(blockPos);

        if (block != null) {
            AxisAlignedBB bb = block.getSelectedBoundingBox(mc.theWorld, blockPos);
            if (bb != null) {
                axisAlignedBB = bb.expand(0.002, 0.002, 0.002);
            }
        }

        if (fill) {
            drawFilledAABB(axisAlignedBB, overlayColor, depth);
        }
        if (outline) {
            drawOutlinedAABB(axisAlignedBB, outlineColor, depth, smoothLines, adjustedLineWidth);
        }
    }

    public enum STYLE {
        OUTLINED,
        FILLED,
        FILLED_OUTLINE
    }
}
