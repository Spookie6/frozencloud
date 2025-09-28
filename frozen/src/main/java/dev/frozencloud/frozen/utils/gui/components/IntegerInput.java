package dev.frozencloud.frozen.utils.gui.components;

import dev.frozencloud.frozen.utils.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class IntegerInput extends Gui {
    private final int x, y, width = 60, height = 20, min, max, step;
    private int value;

    private final int backgroundColor = 0xFF222222;
    private final int clickableBackground = 0xFF323232;
    private final int textColor = 0xFFC1C1C1;
    private final int borderColor = 0xFF646464;

    public IntegerInput(int x, int y, int initial, int min, int max, int step) {
        this.x = x;
        this.y = y;
        this.value = initial;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public void draw(int mx, int my, Minecraft mc) {
        // Draw background box
        drawRect(x, y, x + width, y + height, backgroundColor);
        drawRect(x + width - 20, y, x + width, y + height / 2, clickableBackground); // up button
        drawRect(x + width - 20, y + height / 2, x + width, y + height, clickableBackground); // down button

        if (isMouseOver(mx, my)) {
            if (my >= y && my <= y + height / 2) {
                // Border Up
                drawHorizontalLine(x + width - 21, x + width - 1, y, borderColor);
                drawHorizontalLine(x + width - 21, x + width - 1, y + height / 2 - 1, borderColor);
                drawVerticalLine(x + width - 21, y, y + height / 2 - 1, borderColor);
                drawVerticalLine(x + width - 1, y, y + height / 2 - 1, borderColor);
            } else if (my >= y + height / 2 && my <= y + height) {
                // Border Down
                drawHorizontalLine(x + width - 21, x + width - 1, y + height / 2 - 1, borderColor);
                drawHorizontalLine(x + width - 21, x + width - 1, y + height - 1, borderColor);
                drawVerticalLine(x + width - 21, y + height / 2 - 1, y + height - 1, borderColor);
                drawVerticalLine(x + width - 1, y + height / 2 - 1, y + height - 1, borderColor);
            }
        }

        // Text: value
        String str = String.valueOf(value);
        int strW = mc.fontRendererObj.getStringWidth(str);
        mc.fontRendererObj.drawString(str, x + (width - 20) / 2 - strW / 2, y + height / 2 - mc.fontRendererObj.FONT_HEIGHT / 2, textColor);

        // Arrows
        mc.fontRendererObj.drawString("+", x + width - 10 - mc.fontRendererObj.getStringWidth("+") / 2, y + 2, textColor); // up
        mc.fontRendererObj.drawString("-", x + width - 10 - mc.fontRendererObj.getStringWidth("-") / 2, y + height / 2 + 2, textColor); // down
    }

    public void mouseClicked(Button button, int mouseX, int mouseY) {
        if (!button.equals(Button.MOUSE_LEFT)) return;

        if (mouseY >= y && mouseY <= y + height / 2) {
            // Up button
            value = Math.min(value + step, max);
        } else if (mouseY >= y + height / 2 && mouseY <= y + height) {
            // Down button
            value = Math.max(value - step, min);
        }
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x + width - 20 && mouseX <= x + width;
    }

    public int getValue() {
        return value;
    }

    public void handleMouseInput(int dWheel) {
        if (dWheel > 0) value = Math.min(value + step, max);
        else value = Math.max(value - step, min);
    }
}