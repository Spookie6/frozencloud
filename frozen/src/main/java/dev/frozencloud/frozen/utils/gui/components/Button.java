package dev.frozencloud.frozen.utils.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class Button extends Gui {
    private int x, y;
    private int width = 60, height = 16;
    private Runnable action;
    private String label;

    private final int backgroundColor = 0xFF222222;
    private final int textColor = 0xFFC1C1C1;
    private final int borderColor = 0xFF646464;

    private boolean pressed = false;
    private long pressedTime = 0;
    private final int PRESS_DURATION = 250; // milliseconds

    public Button(int x, int y, String label, Runnable action) {
        this.x = x;
        this.y = y;
        this.action = action;
        this.label = label;
    }

    public void draw(int mx, int my, Minecraft mc) {
        long now = System.currentTimeMillis();
        if (pressed && now - pressedTime > PRESS_DURATION) {
            pressed = false;
        }

        int currentColor = pressed ? 0xFF323232 : backgroundColor; // darker when pressed

        // Background
        drawRect(x, y, x + width, y + height, currentColor);

        if (isMouseOver(mx, my)) {
            // Border
            drawHorizontalLine(x, x + width - 1, y, borderColor);
            drawHorizontalLine(x, x + width - 1, y + height - 1, borderColor);
            drawVerticalLine(x, y, y + height - 1, borderColor);
            drawVerticalLine(x + width - 1, y, y + height - 1, borderColor);
        }

        // Label
        int strW = mc.fontRendererObj.getStringWidth(label);
        mc.fontRendererObj.drawString(label, x + width / 2 - strW / 2, y + height / 2 - mc.fontRendererObj.FONT_HEIGHT / 2, textColor);
    }

    public void mouseClicked(dev.frozencloud.frozen.utils.Button button, int mouseX, int mouseY) {
        if (!button.equals(dev.frozencloud.frozen.utils.Button.MOUSE_LEFT)) return;
        if (isMouseOver(mouseX, mouseY)) {
            action.run();
            pressed = true;
            pressedTime = System.currentTimeMillis();
        }
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
