package dev.frozencloud.frozen.utils.gui.components;

import dev.frozencloud.frozen.utils.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

public class RGBTextInput extends Gui {
    private final int x, y, width, height;
    private String text;
    private boolean focused = false;

    private final int backgroundColor = 0xFF222222;
    private final int textColor = 0xFFC1C1C1;
    private final int borderColor = 0xFF646464;

    public RGBTextInput(int x, int y, int width, int height, String initualValue) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = initualValue;
    }

    public void draw(Minecraft mc) {
        // Background
        drawRect(x, y, x + width, y + height, backgroundColor);
        if (focused) {
            // Border
            drawHorizontalLine(x, x + width - 1, y, borderColor);
            drawHorizontalLine(x, x + width - 1, y + height - 1, borderColor);
            drawVerticalLine(x, y, y + height - 1, borderColor);
            drawVerticalLine(x + width - 1, y, y + height - 1, borderColor);
        }

        // Text
        int textY = y + height / 2 - mc.fontRendererObj.FONT_HEIGHT / 2;
        String visibleText = mc.fontRendererObj.trimStringToWidth(text, width - 6);
        mc.fontRendererObj.drawString(visibleText + (focused && (System.currentTimeMillis() / 500) % 2 == 0 ? "|" : ""), x + 4, textY, textColor);
    }

    public void mouseClicked(Button button, int mouseX, int mouseY) {
        if (!button.equals(Button.MOUSE_LEFT)) return;
        focused = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (!focused) return;

        if (keyCode == Keyboard.KEY_BACK) {
            if (text.length() <= 1) {
                text = "";
            } else {
                text = text.substring(0, text.length() - 1);
            }
        } else if (Character.isDigit(typedChar)) {
            if (text.length() >= 3) return;
            String newText = text + typedChar;
            try {
                int val = Integer.parseInt(newText);
                if (val >= 0 && val <= 255) {
                    text = newText;
                }
                if (val > 255) text = "255";
                if (val < 0) text = "0";
            } catch (NumberFormatException ignored) {}
        }
    }

    public String getValue() {
        return text.isEmpty() ? "0" : text;
    }

    public int getIntValue() {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setValue(String newText) {
        try {
            if (newText.length() > 3) newText = newText.substring(0, 2);
            int val = Integer.parseInt(newText.replaceAll("[^0-9]", ""));
            if (val > 255) val = 255;
            if (val < 0) val = 0;
            this.text = String.valueOf(val);
        } catch (NumberFormatException e) {
            this.text = "0";
        }
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }
}