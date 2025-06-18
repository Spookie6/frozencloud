package com.github.spookie6.overlaymanager.utils.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class HexTextInput extends Gui {
    private int x, y, width, height;
    private String text;
    private boolean focused = false;

    private final int backgroundColor = 0xFF222222;
    private final int textColor = 0xFFC1C1C1;
    private final int borderColor = 0xFF646464;

    public HexTextInput(int x, int y, int width, int height, String initial) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
        this.text = initial;
    }

    public void draw(Minecraft mc) {
        drawRect(x, y, x + width, y + height, backgroundColor);
        if (focused) {
            // Border
            drawHorizontalLine(x, x + width - 1, y, borderColor);
            drawHorizontalLine(x, x + width - 1, y + height - 1, borderColor);
            drawVerticalLine(x, y, y + height - 1, borderColor);
            drawVerticalLine(x + width - 1, y, y + height - 1, borderColor);
        }
        mc.fontRendererObj.drawString(text + (focused && (System.currentTimeMillis() / 500) % 2 == 0 ? "|" : ""), x + 4, y + 4, textColor);
    }

    public void mouseClicked(GuiOverlayEditor.Button button, int mouseX, int mouseY) {
        if (!button.equals(GuiOverlayEditor.Button.MOUSE_LEFT)) return;
        focused = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (!focused) return;
        if (keyCode == 14 && !text.isEmpty()) {
            text = text.substring(0, text.length() - 1);
        } else if (isAllowedChar(typedChar)) {
            text += Character.toUpperCase(typedChar);
        }
        if (!text.startsWith("#")) text = "#" + text;
        if (text.length() > 7) text = text.substring(0, 7);
    }

    public void setText(String t) { this.text = t.startsWith("#") ? t : "#" + t; }
    public String getText() { return text; }

    private boolean isAllowedChar(char c) {
        return Character.isDigit(c) || "ABCDEFabcdef".indexOf(c) != -1;
    }

    public boolean isFocused() {
        return focused;
    }
}