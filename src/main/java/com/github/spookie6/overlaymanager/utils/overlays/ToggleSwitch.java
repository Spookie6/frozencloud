package com.github.spookie6.overlaymanager.utils.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class ToggleSwitch extends Gui {
    private int x, y;
    private int width = 30, height = 16;
    private boolean state;
    private String label;

    // Animation
    private float knobXAnimated = 0;
    private final int animationSpeed = 10; // higher is faster

    public ToggleSwitch(int x, int y, boolean initialState, String label) {
        this.x = x;
        this.y = y;
        this.state = initialState;
        this.label = label;

        knobXAnimated = getTargetKnobX(); // initialize to correct position
    }

    public void draw(Minecraft mc) {
        // Label
        mc.fontRendererObj.drawString(label, x + width + 6, y + height / 2 - mc.fontRendererObj.FONT_HEIGHT / 2, 0xFFC1C1C1);

        // Background
        int bgColor = state ? 0xFF4CAF50 : 0xFFB0BEC5; // green / gray
        drawRect(x, y, x + width, y + height, bgColor);

        // Animate knob position
        float targetX = getTargetKnobX();
        knobXAnimated += (targetX - knobXAnimated) / animationSpeed;

        // Draw knob (white circle)
        int knobSize = height - 4;
        drawRect((int) knobXAnimated, y + 2, (int) knobXAnimated + knobSize, y + 2 + knobSize, 0xFFFFFFFF);
    }

    private float getTargetKnobX() {
        int knobSize = height - 4;
        return state ? (x + width - knobSize - 2) : (x + 2);
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void toggle() {
        state = !state;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}