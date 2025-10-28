package dev.frozencloud.core.overlaymanager;

import dev.frozencloud.core.Core;
import dev.frozencloud.core.ModEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class Overlay {
    protected ModEnum mod;
    protected int x, y;
    protected boolean centerX, centerY;

    protected Dimensions dimensions;
    protected OverlayConfig config;
    protected String configName;

    protected BooleanConfigBinding configOption;
    protected String displayName;
    protected Supplier<Boolean> renderCondition;
    protected Color color;
    protected boolean shadow = false;

    protected double scale = 1.0d;
    protected final double MAX_SCALE = 5;
    protected final double MIN_SCALE = .25;

    protected int padding;

    protected boolean inEditMode = false;

    public void setEditMode(boolean bool) {
        this.inEditMode = bool;
        this.updateDimensions();
        this.updateDynamicPosition();
    }

    public abstract void render(Minecraft mc);
    public abstract void updateDimensions();

    public Overlay(ModEnum mod, BooleanConfigBinding configOption, String displayName, Supplier<Boolean> renderCondition) {
        this.configOption = configOption;
        this.displayName = displayName;
        this.renderCondition = renderCondition;

        this.padding = 2;

        configName = displayName.trim().toLowerCase().replaceAll(" ", "_");
        config = OverlayConfigManager.getOverlayConfig(configName);
        this.x = config.x;
        this.y = config.y;
        this.centerX = config.centerX;
        this.centerY = config.centerY;
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
        config.titleColor =
                (this instanceof TextOverlay)
                    ? ((TextOverlay) this).getTitleSupplier() == null
                        ? null
                        : ((TextOverlay) this).titleColor.getRGB()
                    : null;
        OverlayConfigManager.updateOverlayConfig(configName, config);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;

        updateDynamicPosition();
        updateConfig();
    }

    public void updateDynamicPosition() {
        if (this.centerX) this.x = new ScaledResolution(Objects.requireNonNull(Core.INSTANCE.getMc())).getScaledWidth() / 2 - this.getWidth() / 2;
        if (this.centerY) this.y = new ScaledResolution(Objects.requireNonNull(Core.INSTANCE.getMc())).getScaledHeight() / 2 - this.getHeight() / 2;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() {
        return (int) ((this.dimensions.width + padding * 2) * scale);
    }
    public int getHeight() {
        return (int) ((this.dimensions.height + padding * 2) * scale);
    }

    public boolean getInEditMode() {
        return this.inEditMode;
    }

    public boolean isVisible() {
        return renderCondition.get() && configOption.get();
    }

    public double getScale() {
        return scale;
    }

    public void updateScale(double scale) {
        ScaledResolution res = new ScaledResolution(Objects.requireNonNull(Core.INSTANCE.getMc()));
        this.scale = roundToDecimals(MathHelper.clamp_double(scale, MIN_SCALE, MAX_SCALE), 2);

        if (this.x + this.getWidth() > res.getScaledWidth()) {
            this.x = res.getScaledWidth() - this.getWidth();
        }
        if (this.y + this.getHeight() > res.getScaledHeight()) {
            this.y = res.getScaledHeight() - this.getHeight();
        }

        updateDimensions();
        updateDynamicPosition();
        updateConfig();
    }

    public void incrementScale(double multiplier) {
        updateScale(scale + .05 * multiplier);
    }

    public void decrementScale(double multiplier) {
        updateScale(scale - .05 * multiplier);
    }

    public void reset() {
        this.x = 0;
        this.y = 0;
        this.centerX = false;
        this.centerY = false;
        this.scale = 1;
        this.color = new Color(255, 255, 255);
    }

    public void setColor(Color color) { this.color = color; }

    public Color getColor() {
        return color;
    }

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