package com.github.spookie6.frozen.utils.gui.overlays;

import com.github.spookie6.frozen.Frozen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.function.Supplier;

import static com.github.spookie6.frozen.Frozen.mc;

public abstract class Overlay {
    protected int x, y;
    protected boolean centerX, centerY = false;

    protected Dimensions dimensions;
    protected OverlayConfig config;
    protected String configName;

    protected BooleanConfigBinding configOption;
    protected String displayName;
    protected Supplier<Boolean> renderCondition;
    protected Color color;
    protected boolean shadow = false;

    protected double scale = 1.0;
    protected final double MAX_SCALE = 5;
    protected final double MIN_SCALE = .25;

    protected int padding;

    protected boolean inEditMode = false;

    public void setEditMode(boolean bool) {
        this.inEditMode = bool;
        this.updateDimensions();
    }

    public abstract void render(Minecraft mc);
    public abstract void updateDimensions();

    public Overlay(BooleanConfigBinding configOption, String displayName, Supplier<Boolean> renderCondition) {
        this.configOption = configOption;
        this.displayName = displayName;
        this.renderCondition = renderCondition;

        this.padding = 3;

        configName = displayName.trim().toLowerCase().replaceAll(" ", "_");
        config = OverlayConfigManager.getOverlayConfig(configName);
        this.x = config.x;
        this.y = config.y;
        this.color = new Color(config.color);
        this.scale = config.scale;
        this.shadow = config.shadow;
    }

    void updateConfig() {
        config.x = x;
        config.y = y;
        config.centerX = centerX;
        config.centerY = centerY;
        config.color = color.getRGB();
        config.scale = scale;
        config.shadow = shadow;
        OverlayConfigManager.updateOverlayConfig(configName, config);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;

        this.updateDynamicPosition();

        updateConfig();
    }

    public void updateDynamicPosition() {
        if (this.centerX) this.x = new ScaledResolution(mc).getScaledWidth() / 2 - this.getWidth() / 2;
        if (this.centerY) this.y = new ScaledResolution(mc).getScaledHeight() / 2 - this.getHeight() / 2;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() {
        return (int) ((this.dimensions.width + padding * 2) * scale);
    }
    public int getHeight() {
        return (int) ((this.dimensions.height + padding * 2) * scale);
    }

    public boolean isVisible() {
        return renderCondition.get() && configOption.get();
    }

    public double getScale() {
        return scale;
    }

    public void updateScale(double scale) {
        ScaledResolution res = Frozen.guiOverlayEditor.res;
        this.scale = roundToDecimals(MathHelper.clamp_double(scale, MIN_SCALE, MAX_SCALE), 2);

        if (this.x + this.getWidth() > res.getScaledWidth()) {
            this.x = res.getScaledWidth() - this.getWidth();
        }
        if (this.y + this.getHeight() > res.getScaledHeight()) {
            this.y = res.getScaledHeight() - this.getHeight();
        }

        updateDimensions();
        updateConfig();
    }

    public void incrementScale() {
        updateScale(scale + .05);
    }

    public void decrementScale() {
        updateScale(scale - .05);
    }

    public void reset() {
        this.x = 0;
        this.y = 0;
        this.scale = 1;
        this.color = new Color(255, 255, 255);
    }

    public void setColor(Color color) { this.color = color; }

    public boolean hasShadow() { return this.shadow; }
    public void setShadow(boolean bool) { this.shadow = bool; }

    static class Dimensions {
        int width, height;

        public Dimensions(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    private static double roundToDecimals(double value, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }
}