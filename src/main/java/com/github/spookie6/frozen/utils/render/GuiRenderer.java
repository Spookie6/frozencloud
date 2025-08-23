package com.github.spookie6.frozen.utils.render;

import cc.polyfrost.oneconfig.config.core.OneColor;
import com.github.spookie6.frozen.mixin.AccessorGuiContainer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import static com.github.spookie6.frozen.Frozen.mc;

public class GuiRenderer {
    public static void drawFilledSlot(GuiScreen gui, Slot slot, OneColor color) {
        try {
            int left = ((AccessorGuiContainer) gui).getGuiLeft_frozen();
            int top = ((AccessorGuiContainer) gui).getGuiTop_frozen();

            int x = left + slot.xDisplayPosition;
            int y = top + slot.yDisplayPosition;

            GlStateManager.disableDepth(); // important
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            Tessellator tess = Tessellator.getInstance();
            WorldRenderer wr = tess.getWorldRenderer();
            wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
            wr.pos(x, y + 16, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex(); // blue
            wr.pos(x + 16, y + 16, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            wr.pos(x + 16, y, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            wr.pos(x, y, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            tess.draw();

            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();

            ItemStack stack = slot.getStack();
            if (stack != null) {
                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack, x, y, null);
                RenderHelper.disableStandardItemLighting();
            }

            ItemStack heldStack = mc.thePlayer.inventory.getItemStack();
            if (heldStack != null) {
                int mouseX = Mouse.getX() * gui.width / mc.displayWidth;
                int mouseY = gui.height - Mouse.getY() * gui.height / mc.displayHeight - 1;

                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(heldStack, mouseX - 8, mouseY - 8);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, heldStack, mouseX - 8, mouseY - 8, null);
                RenderHelper.disableStandardItemLighting();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawSlotOutline(GuiScreen gui, Slot slot, OneColor color, float thickness) {
        try {
            int left = ((AccessorGuiContainer) gui).getGuiLeft_frozen();
            int top = ((AccessorGuiContainer) gui).getGuiTop_frozen();

            int x = left + slot.xDisplayPosition;
            int y = top + slot.yDisplayPosition;

            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            GL11.glLineWidth(thickness);

            Tessellator tess = Tessellator.getInstance();
            WorldRenderer wr = tess.getWorldRenderer();
            wr.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

            wr.pos(x,     y,      0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            wr.pos(x,     y + 16, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            wr.pos(x + 16, y + 16, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            wr.pos(x + 16, y,      0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

            tess.draw();

            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();


            ItemStack stack = slot.getStack();
            if (stack != null) {
                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack, x, y, null);
                RenderHelper.disableStandardItemLighting();
            }

            ItemStack heldStack = mc.thePlayer.inventory.getItemStack();
            if (heldStack != null) {
                int mouseX = Mouse.getX() * gui.width / mc.displayWidth;
                int mouseY = gui.height - Mouse.getY() * gui.height / mc.displayHeight - 1;

                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(heldStack, mouseX - 8, mouseY - 8);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, heldStack, mouseX - 8, mouseY - 8, null);
                RenderHelper.disableStandardItemLighting();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawLine(float xStart, float yStart, float xEnd, float yEnd, OneColor color, float thickness) {
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glLineWidth(thickness);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        wr.pos(xStart, yStart, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        wr.pos(xEnd, yEnd, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        tess.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
    }

    public static void drawLineBetweenSlots(GuiScreen gui, Slot slot1, Slot slot2, OneColor color, float thickness) {
        try {
            int left = ((AccessorGuiContainer) gui).getGuiLeft_frozen();
            int top = ((AccessorGuiContainer) gui).getGuiTop_frozen();

            float cx1 = left + slot1.xDisplayPosition + 8;
            float cy1 = top + slot1.yDisplayPosition + 8;

            float cx2 = left + slot2.xDisplayPosition + 8;
            float cy2 = top + slot2.yDisplayPosition + 8;

            float[] start = intersectSlotEdge(cx1, cy1, cx2, cy2);
            float[] end   = intersectSlotEdge(cx2, cy2, cx1, cy1);

            drawLine(start[0], start[1], end[0], end[1], color, thickness);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the point where a line from (cx, cy) toward (tx, ty)
     * exits the 16x16 box centered at (cx, cy).
     */
    private static float[] intersectSlotEdge(float cx, float cy, float tx, float ty) {
        float half = 8f;
        float dx = tx - cx;
        float dy = ty - cy;

        // avoid division by zero
        if (dx == 0 && dy == 0) return new float[]{cx, cy};

        float scaleX = dx != 0 ? half / Math.abs(dx) : Float.POSITIVE_INFINITY;
        float scaleY = dy != 0 ? half / Math.abs(dy) : Float.POSITIVE_INFINITY;

        float scale = Math.min(scaleX, scaleY);

        return new float[]{
                cx + dx * scale,
                cy + dy * scale
        };
    }

    public static void drawTintedOverlay(ResourceLocation texture, int x, int y, OneColor color, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        mc.getTextureManager().bindTexture(texture);

        GlStateManager.color(
                color.getRed() / 255f,
                color.getGreen() / 255f,
                color.getBlue() / 255f,
                alpha
        );

        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    public enum RenderShape {
        CIRCLE(new ResourceLocation("frozen", "textures/gui/rarity.png")),
        SQUARE(new ResourceLocation("frozen", "textures/gui/rarity2.png")),
        OUTLINED(new ResourceLocation("frozen", "textures/gui/rarity3.png")),
        FADE_IN(new ResourceLocation("frozen", "textures/gui/rarity4.png"));

        private final ResourceLocation tex;

        RenderShape(ResourceLocation tex) {
            this.tex = tex;
        }

        public ResourceLocation getTex() {return this.tex;}
    }
}
