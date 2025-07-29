package com.github.spookie6.frozen.utils.overlays;

import com.github.spookie6.frozen.utils.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.io.IOException;

public class ColorPicker {

    // Position and dimensions
    private int x, y;
    private static int colorFieldSize = 100;
    private static int brightnessHeight = 10;

    private static int width = colorFieldSize;
    private static int height = colorFieldSize + brightnessHeight + 10;

    // Color state
    private float hue = 0f;
    private float saturation = 1f;
    private float brightness = 1f;

    // Dragging
    private boolean draggingField = false;
    private boolean draggingBrightness = false;

    // Inputs
    private RGBTextInput inputR, inputG, inputB;
    private HexTextInput inputHex;

    public ColorPicker(int x, int y, Color initialColor) {
        this.x = x;
        this.y = y;

        int r = initialColor.getRed();
        int g = initialColor.getGreen();
        int b = initialColor.getBlue();

        float[] hsb = Color.RGBtoHSB(r, g, b, null);

        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];

        inputR = new RGBTextInput(x, y + height + 21, 30, 16, "255");
        inputG = new RGBTextInput(x + 35, y + height + 21, 30, 16, "255");
        inputB = new RGBTextInput(x + 70, y + height + 21, 30, 16, "255");
        inputHex = new HexTextInput(x, y + height, 60, 16, "#000000");

        updateInputsFromColor();
    }

    public void draw(int mouseX, int mouseY, Minecraft mc) {
        drawColorField();
        drawBrightnessSlider();

        if (draggingField) updateColorFromMouse(mouseX, mouseY);
        if (draggingBrightness) updateBrightnessFromMouse(mouseX);

        drawSelector(mouseX, mouseY);

        inputR.draw(mc);
        inputG.draw(mc);
        inputB.draw(mc);
        inputHex.draw(mc);
    }

    private void drawColorField() {
        for (int i = 0; i < colorFieldSize; i++) {
            float hue = i / (float) colorFieldSize;
            for (int j = 0; j < colorFieldSize; j++) {
                float sat = 1.0f - j / (float) colorFieldSize;
                Color c = Color.getHSBColor(hue, sat, brightness);
                Gui.drawRect(x + i, y + j, x + i + 1, y + j + 1, c.getRGB());
            }
        }
    }

    private void drawBrightnessSlider() {
        int sliderY = y + colorFieldSize + 5;
        for (int i = 0; i < colorFieldSize; i++) {
            float value = i / (float) colorFieldSize;
            Color c = Color.getHSBColor(hue, saturation, value);
            Gui.drawRect(x + i, sliderY, x + i + 1, sliderY + brightnessHeight, c.getRGB());
        }
    }

    private void drawSelector(int mouseX, int mouseY) {
        int selX = (int) (x + hue * colorFieldSize);
        int selY = (int) (y + (1 - saturation) * colorFieldSize);
        Gui.drawRect(selX - 2, selY - 2, selX + 2, selY + 2, 0xFFFFFFFF);

        int brightnessX = (int) (x + brightness * colorFieldSize);
        int sliderY = y + colorFieldSize + 5;
        Gui.drawRect(brightnessX - 1, sliderY, brightnessX + 1, sliderY + brightnessHeight, 0xFF000000);
    }

    public void mouseClicked(Button button, int mouseX, int mouseY) {
        if (isInside(mouseX, mouseY, x, y, colorFieldSize, colorFieldSize)) {
            updateColorFromMouse(mouseX, mouseY);
            draggingField = true;
        } else if (isInside(mouseX, mouseY, x, y + colorFieldSize + 5, colorFieldSize, brightnessHeight)) {
            updateBrightnessFromMouse(mouseX);
            draggingBrightness = true;
        }

        inputR.mouseClicked(button, mouseX, mouseY);
        inputG.mouseClicked(button, mouseX, mouseY);
        inputB.mouseClicked(button, mouseX, mouseY);
        inputHex.mouseClicked(button, mouseX, mouseY);
    }

    public void mouseReleased() {
        draggingField = false;
        draggingBrightness = false;
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        inputR.keyTyped(typedChar, keyCode);
        inputG.keyTyped(typedChar, keyCode);
        inputB.keyTyped(typedChar, keyCode);
        inputHex.keyTyped(typedChar, keyCode);
        updateColorFromInputs();
    }

    private void updateColorFromMouse(int mouseX, int mouseY) {
        hue = MathHelper.clamp_float((mouseX - x) / (float) colorFieldSize, 0f, 1f);
        saturation = 1.0f - MathHelper.clamp_float((mouseY - y) / (float) colorFieldSize, 0f, 1f);
        updateInputsFromColor();
    }

    private void updateBrightnessFromMouse(int mouseX) {
        brightness = MathHelper.clamp_float((mouseX - x) / (float) colorFieldSize, 0f, 1f);
        updateInputsFromColor();
    }

    public void setColor(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        float[] hsb = Color.RGBtoHSB(r, g, b, null);

        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }

    private void updateInputsFromColor() {
        Color c = Color.getHSBColor(hue, saturation, brightness);
        inputR.setValue(String.valueOf(c.getRed()));
        inputG.setValue(String.valueOf(c.getGreen()));
        inputB.setValue(String.valueOf(c.getBlue()));
        inputHex.setText(String.format("%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue()));
    }

    private void updateColorFromInputs() {
        if (inputR.isFocused() || inputG.isFocused() || inputB.isFocused()) {
            try {
                int r = clampInput(inputR.getValue());
                int g = clampInput(inputG.getValue());
                int b = clampInput(inputB.getValue());

                float[] hsb = Color.RGBtoHSB(r, g, b, null);
                this.hue = hsb[0];
                this.saturation = hsb[1];
                this.brightness = hsb[2];
            } catch (NumberFormatException ignored) {
            }
        } else if (inputHex.isFocused()) {
            try {
                String hex = inputHex.getText().replaceAll("[^0-9A-Fa-f]", "");
                if (hex.length() == 6) {
                    int rgb = Integer.parseInt(hex, 16);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;

                    float[] hsb = Color.RGBtoHSB(r, g, b, null);
                    this.hue = hsb[0];
                    this.saturation = hsb[1];
                    this.brightness = hsb[2];
                }
            } catch (NumberFormatException ignored) {
            }
        }
        updateInputsFromColor();
    }

    private int clampInput(String text) {
        return MathHelper.clamp_int(Integer.parseInt(text.replaceAll("[^0-9]", "")), 0, 255);
    }

    private boolean isInside(int mx, int my, int rx, int ry, int rw, int rh) {
        return mx >= rx && mx <= rx + rw && my >= ry && my <= ry + rh;
    }

    public Color getColor() {
        return Color.getHSBColor(hue, saturation, brightness);
    }
}
