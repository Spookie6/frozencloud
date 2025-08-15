package com.github.spookie6.frozen.utils.gui.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.spookie6.frozen.Frozen.mc;
import static net.minecraft.client.gui.Gui.drawRect;

public class TextOverlay extends Overlay {
    private final Supplier<String> textSupplier;
    private final String exampleText;

    private BooleanConfigBinding rightAlign = null;
    private IntegerConfigBinding extraWidth = null;

    private String cachedText = "";

    private final String delimiter = "#";

    public TextOverlay(BooleanConfigBinding configOption, String displayName, Supplier<String> textSupplier, Supplier<Boolean> renderCondition, String exampleText) {
        super(configOption, displayName, renderCondition);
        this.textSupplier = textSupplier;
        this.exampleText = exampleText;

        updateDimensions();
    }

    public void render(Minecraft mc) {
        if (getText().isEmpty()) return;
        String[] lines = getText().split("\n");

        if (!getText().equals(cachedText)) {
            cachedText = getText();
            updateDimensions();
            this.updateDynamicPosition();
        }

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1.0);

        if (inEditMode) {
            drawRect(0, 0, this.dimensions.width + padding * 2, this.dimensions.height + padding * 2, new Color(211, 211, 211, 70).getRGB());
        }

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].replaceAll("&", "§");
            if (rightAlign != null && rightAlign.get()) {
                String[] parts = line.split(delimiter, 2);
                String left = parts.length > 0 ? parts[0] : "";
                String right = parts.length > 1 ? parts[1] : "";
                int rightWidth = mc.fontRendererObj.getStringWidth(right);
                mc.fontRendererObj.drawString(left, padding, padding + (i * (mc.fontRendererObj.FONT_HEIGHT + 1)), color.getRGB(), shadow);
                if (this.configName.equals("splits")) {
                    int maxRight2Width = mc.fontRendererObj.getStringWidth("[10m 00.00s]");
                    String right1 = right.split(delimiter)[0];
                    String right2 = right.split(delimiter)[1];
                    int maxWidth = mc.fontRendererObj.getStringWidth("Blood Clear 10m 00.00s [10m 00.00s]") + this.extraWidth.get();
                    mc.fontRendererObj.drawString(right2, maxWidth - padding - mc.fontRendererObj.getStringWidth(right2), padding + (i * (mc.fontRendererObj.FONT_HEIGHT + 1)), color.getRGB(), shadow);
                    mc.fontRendererObj.drawString(right1, maxWidth - padding - mc.fontRendererObj.getStringWidth(right1) - maxRight2Width - mc.fontRendererObj.getStringWidth(" "), padding + (i * (mc.fontRendererObj.FONT_HEIGHT + 1)), color.getRGB(), shadow);
                } else {
                    mc.fontRendererObj.drawString(right, dimensions.width - padding - rightWidth, padding + (i * (mc.fontRendererObj.FONT_HEIGHT + 1)), color.getRGB(), shadow);
                }
            } else mc.fontRendererObj.drawString(line.replaceAll("#", " "), padding, padding + (i * (mc.fontRendererObj.FONT_HEIGHT + 1)), color.getRGB(), shadow);
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
        int totalHeight = lines.length == 1 ? fontRenderer.FONT_HEIGHT :  lines.length * (fontRenderer.FONT_HEIGHT + 1) - 3;

        for (String line : lines) {
            line = line.replaceAll("&", "§");
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

        return new Dimensions((rightAlign != null && rightAlign.get()) ? maxWidth + (extraWidth == null ? 0 : extraWidth.get()) : maxWidth, totalHeight);
    }

    public TextOverlay setExtraWidth(IntegerConfigBinding extraWidth) {
        this.extraWidth = extraWidth;
        return this;
    }

    public TextOverlay setRightAlign(BooleanConfigBinding rightAlign) {
        this.rightAlign = rightAlign;
        return this;
    }

    public BooleanConfigBinding getRightAlign() {
        return rightAlign;
    }

    public IntegerConfigBinding getExtraWidth() {
        return extraWidth;
    }
}