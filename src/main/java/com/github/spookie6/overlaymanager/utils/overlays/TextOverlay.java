package com.github.spookie6.overlaymanager.utils.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.function.Supplier;

import static com.github.spookie6.overlaymanager.OverlayManager.mc;
import static net.minecraft.client.gui.Gui.drawRect;

public class TextOverlay extends Overlay {
    private final Supplier<String> textSupplier;
    private final String exampleText;

    public TextOverlay(BooleanConfigBinding configOption, String displayName, Supplier<String> textSupplier, Supplier<Boolean> renderCondition, String exampleText) {
        super(configOption, displayName, renderCondition);
        this.textSupplier = textSupplier;
        this.exampleText = exampleText;

        updateDimensions();
    }

    public void render(Minecraft mc) {
        if (getText().isEmpty()) return;
        String[] lines = getText().split("\n");
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1.0);

        if (inEditMode) {
            drawRect(0, 0, this.dimensions.width + padding * 2, this.dimensions.height + padding * 2, new Color(211, 211, 211, 70).getRGB());
        }

        for (int i = 0; i < lines.length; i++) {
            mc.fontRendererObj.drawString(lines[i], padding, padding + (i * (mc.fontRendererObj.FONT_HEIGHT + 3)), color.getRGB(), shadow);
        }

        GL11.glPopMatrix();
    }

    public void updateDimensions() {
        String content = getText();
        dimensions = calculateTextDimensions(content);
    }

    public String getText() {
        return this.inEditMode ? this.exampleText : textSupplier.get();
    }

    private Dimensions calculateTextDimensions(String text) {
        FontRenderer fontRenderer = mc.fontRendererObj;
        String[] lines = text.split("\n");

        int maxWidth = 0;
        int totalHeight = lines.length * (fontRenderer.FONT_HEIGHT + 3);

        for (String line : lines) {
            boolean bold = false;
            int lineWidth = 0;

            for (int i = 0, len = line.length(); i < len; i++) {
                char c = line.charAt(i);

                if (c == '§' && i + 1 < len) {
                    char code = Character.toLowerCase(line.charAt(++i));
                    switch (code) {
                        case 'l': bold = true; break;
                        case 'r': bold = false; break;
                    }
                    continue;
                }

                int charWidth = fontRenderer.getCharWidth(c);
                if (bold) charWidth++;

                lineWidth += charWidth;
            }

            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }

        return new Dimensions(maxWidth, totalHeight);
    }
}