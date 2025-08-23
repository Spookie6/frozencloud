package com.github.spookie6.frozen.utils.gui.overlays;

import com.github.spookie6.frozen.Frozen;
import com.github.spookie6.frozen.utils.Button;
import com.github.spookie6.frozen.utils.gui.components.ToggleSwitch;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Color;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class GuiOverlayEditor extends GuiScreen {
    private Overlay draggedOverlay = null;
    private OverlayConfigGui overlayConfigGui = null;
    private int dragOffsetX, dragOffsetY;

    public boolean opened = false;

    private final int snapThreshold = 5;
    private boolean snappingX = false;
    private boolean snappingY = false;
    private int snapLineX = -1;
    private int snapLineY = -1;

    private ToggleSwitch showDisabledToggle;
    private ToggleSwitch showInvisibleToggle;
    private ToggleSwitch snapToggle;

    private boolean showDisabled = false;
    private boolean showinvisible = false;
    private boolean snap = false;

    private Overlay scaledOverlay = null;
    private long lastScaleChange = -1;
    private final int scaleOverlayDuration = 250; // ms

    private boolean showTip = false;

    public ScaledResolution res;
    public static final File configFile = new File(Minecraft.getMinecraft().mcDataDir, "config/frozen/editor.json");
    Gson GSON = new Gson();

    @Override
    public void initGui() {
        this.res = new ScaledResolution(mc);
        this.overlayConfigGui = null;
        this.draggedOverlay = null;

        this.showTip = true;

        int margin = 5;
        int yBase = height - 74;

        loadConfig();
        showDisabledToggle = new ToggleSwitch(margin, yBase, showDisabled, "Show Disabled");
        showInvisibleToggle = new ToggleSwitch(margin, yBase + 24, showinvisible, "Show Invisible");
        snapToggle = new ToggleSwitch(margin, yBase + 48, snap, "Snapping");

        for (Overlay overlay : OverlayManager.getOverlays()) {
            overlay.setEditMode(true);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        this.res = new ScaledResolution(mc);

        if (draggedOverlay != null && Mouse.isButtonDown(0)) {
            int newX = mouseX - dragOffsetX;
            int newY = mouseY - dragOffsetY;

            int screenWidth = res.getScaledWidth();
            int screenHeight = res.getScaledHeight();

            newX = Math.max(0, Math.min(screenWidth - draggedOverlay.getWidth(), newX));
            newY = Math.max(0, Math.min(screenHeight - draggedOverlay.getHeight(), newY));

            if (snapToggle.getState()) {
                newX = snapToOtherOverlays(newX, 1, res);
                newY = snapToOtherOverlays(newY, 2, res);
            }

            draggedOverlay.setPosition(newX, newY);

            String str = String.format("%sx ; %sy", draggedOverlay.x, draggedOverlay.y);
            fontRendererObj.drawStringWithShadow(str, (float) res.getScaledWidth() / 2 - (float) fontRendererObj.getStringWidth(str) / 2, res.getScaledHeight() - 20, 0xff00ff);
        }

        if (draggedOverlay != null || getHoveredOverlay(mouseX, mouseY) != null) {
            Overlay ov = draggedOverlay != null ? draggedOverlay : getHoveredOverlay(mouseX, mouseY);

            assert ov != null;
            drawHorizontalLine(ov.x, ov.x + ov.getWidth(), ov.y, new Color(255, 255, 255, 255).getRGB());
            drawHorizontalLine(ov.x, ov.x + ov.getWidth(), ov.y + ov.getHeight(), new Color(255, 255, 255, 255).getRGB());

            drawVerticalLine(ov.x, ov.y, ov.y + ov.getHeight(), new Color(255, 255, 255, 255).getRGB());
            drawVerticalLine(ov.x + ov.getWidth(), ov.y, ov.y + ov.getHeight(), new Color(255, 255, 255, 255).getRGB());
        }

        if (scaledOverlay != null) {
            if (System.currentTimeMillis() - lastScaleChange > scaleOverlayDuration) {
                scaledOverlay = null;
            } else {
                String str = String.format("Scale: %.2fx", scaledOverlay.getScale());
                fontRendererObj.drawStringWithShadow(str, (float) res.getScaledWidth() / 2 - (float) fontRendererObj.getStringWidth(str) / 2, res.getScaledHeight() - 35, 0xff00ff);
            }
        }

        if (this.snappingX) {
            drawVerticalLine(snapLineX, 0, height, new Color(137, 207, 240, 75).getRGB());
        }

        if (this.snappingY) {
            drawHorizontalLine(0, width, snapLineY, new Color(137, 207, 240, 75).getRGB());
        }

        for (Overlay overlay : OverlayManager.getOverlays()) {
            if (overlay.isVisible() || (showInvisibleToggle.getState() && overlay.configOption.get()) || (showDisabledToggle.getState() && !overlay.configOption.get())) {
                overlay.render(mc);
            }
        }

        showDisabledToggle.draw(mc, partialTicks);
        showInvisibleToggle.draw(mc, partialTicks);
        snapToggle.draw(mc, partialTicks);

        if (overlayConfigGui != null) {
            overlayConfigGui.draw(mouseX, mouseY, mc, partialTicks);
        }

        if (showTip) {
            String str = "§7Left click on overlay to move, right click to configure!§r";
            String str2 = "§8§oPress 1 on an overlay to center it on x, 2 to center on y.§r";
            int strW = mc.fontRendererObj.getStringWidth(str);
            int strW2 = mc.fontRendererObj.getStringWidth(str2);
            mc.fontRendererObj.drawStringWithShadow(str, (float) res.getScaledWidth() / 2 - (float) strW / 2, (float) res.getScaledHeight() / 2 - (float) mc.fontRendererObj.FONT_HEIGHT / 2, 0xFFC1C1C1);
            mc.fontRendererObj.drawStringWithShadow(str2, (float) res.getScaledWidth() / 2 - (float) strW2 / 2, (float) res.getScaledHeight() / 2 - (float) mc.fontRendererObj.FONT_HEIGHT / 2 + mc.fontRendererObj.FONT_HEIGHT + 2, 0xFF323232);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        showTip = false;

        if (keyCode == Keyboard.KEY_ESCAPE) {
            for (Overlay overlay : OverlayManager.getOverlays()) {
                overlay.setEditMode(false);
            }
            OverlayConfigManager.saveOverlayConfigs();
            saveConfig();
            opened = false;
            snappingX = false;
            snappingY = false;
        }
        if (overlayConfigGui != null) {
            overlayConfigGui.keyTyped(typedChar, keyCode);
        } else {
            int scaledMouseX = Mouse.getX() * res.getScaledWidth() / mc.displayWidth;
            int scaledMouseY = res.getScaledHeight() - Mouse.getY() * res.getScaledHeight() / mc.displayHeight - 1;

            Overlay target = this.draggedOverlay == null ? getHoveredOverlay(scaledMouseX, scaledMouseY) : this.draggedOverlay;
            if (target == null) return;
            if (keyCode == Keyboard.KEY_1) target.centerX = !target.centerX;
            if (keyCode == Keyboard.KEY_2) target.centerY = !target.centerY;
            target.updateDynamicPosition();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        showTip = false;

        com.github.spookie6.frozen.utils.Button mouseBtn = com.github.spookie6.frozen.utils.Button.getButton(mouseButton);
        Overlay hoveringOverlay = getHoveredOverlay(mouseX, mouseY);

        if (mouseBtn.equals(com.github.spookie6.frozen.utils.Button.MOUSE_LEFT)) {
            if (overlayConfigGui != null) {
                if (overlayConfigGui.isMouseOver(mouseX, mouseY)) {
                    overlayConfigGui.mouseClicked(mouseBtn, mouseX, mouseY);
                } else {
                    overlayConfigGui = null;
                }
                return;
            }

            if (showDisabledToggle.isMouseOver(mouseX, mouseY)) {
                showDisabledToggle.toggle();
                showDisabled = showDisabledToggle.getState();
            }

            if (showInvisibleToggle.isMouseOver(mouseX, mouseY)) {
                showInvisibleToggle.toggle();
                showinvisible = showInvisibleToggle.getState();
            }

            if (snapToggle.isMouseOver(mouseX, mouseY)) {
                snapToggle.toggle();
                snap = snapToggle.getState();
            }

            if (hoveringOverlay != null) {
                draggedOverlay = hoveringOverlay;
                dragOffsetX = mouseX - hoveringOverlay.getX();
                dragOffsetY = mouseY - hoveringOverlay.getY();
            }
        } else if (mouseBtn.equals(Button.MOUSE_RIGHT)) {
            if (hoveringOverlay != null) {
                int mx = OverlayConfigGui.fits(mouseX, mouseY) ? mouseX : mouseX - OverlayConfigGui.width;
                int my = OverlayConfigGui.fits(mouseX, mouseY) ? mouseY : mouseY - OverlayConfigGui.height;
                overlayConfigGui = new OverlayConfigGui(hoveringOverlay, mx, my);
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        Mouse.poll();
        int dWheel = Mouse.getDWheel();                     // LWJGL mouse wheel delta
        if (dWheel == 0) return;                            // no scroll? nothing to do

        int mouseX = Mouse.getEventX() * width  / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

        if (overlayConfigGui != null) {
            overlayConfigGui.handleMouseInput(mouseX, mouseY, dWheel);
            return;
        }

        Overlay target = this.draggedOverlay != null ? this.draggedOverlay : getHoveredOverlay(mouseX, mouseY);

        if (target == null) return;
        lastScaleChange = System.currentTimeMillis();
        scaledOverlay = target;
        if (dWheel > 0) {
            assert target != null;
            target.incrementScale();
        }
        else {
            assert target != null;
            target.decrementScale();
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        draggedOverlay = null;
        snappingX = false;
        snappingY = false;

        if (overlayConfigGui != null) {
            overlayConfigGui.mouseReleased();
        }
    }

    private Overlay getHoveredOverlay(int mouseX, int mouseY) {
        for (Overlay overlay : OverlayManager.getOverlays()) {
            if ((overlay.isVisible() || (showInvisibleToggle.getState() && overlay.configOption.get()) || (showDisabledToggle.getState() && !overlay.configOption.get())) && isMouseOverOverlay(overlay, mouseX, mouseY)) {
                return overlay;
            }
        }
        return null;
    }

    private boolean isMouseOverOverlay(Overlay overlay, int mouseX, int mouseY) {
        return mouseX >= overlay.getX() && mouseX <= overlay.getX() + overlay.getWidth() &&
                mouseY >= overlay.getY() && mouseY <= overlay.getY() + overlay.getHeight();
    }

    private int snapToOtherOverlays(int newCoord, int direction, ScaledResolution res) {
        int snappedCoord;

        Overlay dragged = this.draggedOverlay;

        // Which edges to use based on direction
        List<Edge> draggedEdges = direction == 1
                ? Arrays.<Edge>asList(Edge.LEFT, Edge.RIGHT, Edge.CENTER_X)
                : Arrays.<Edge>asList(Edge.TOP, Edge.BOTTOM, Edge.CENTER_Y);

        List<Edge> targetEdges = draggedEdges; // We compare same kinds of edges

        for (Edge draggedEdge : draggedEdges) {
            int draggedValue = draggedEdge.get(dragged) - (direction == 1 ? dragged.getX() : dragged.getY()) + newCoord;

            // Snap to screen center
            int screenCenter = direction == 1 ? res.getScaledWidth() / 2 : res.getScaledHeight() / 2;
            if (draggedEdge == Edge.CENTER_X || draggedEdge == Edge.CENTER_Y) {
                if (Math.abs(draggedValue - screenCenter) < snapThreshold) {
                    snappedCoord = draggedEdge.adjust(screenCenter, dragged);

                    if (draggedEdge.isHorizontal()) {
                        this.snappingX = true;
                        this.snapLineX = screenCenter;
                    } else {
                        this.snappingY = true;
                        this.snapLineY = screenCenter;
                    }
                    return snappedCoord;
                }
            }

            // Snap to other overlays
            for (Overlay other : OverlayManager.getOverlays()) {
                if (!other.isVisible() && !(showInvisibleToggle.getState() && other.configOption.get()) && !(showDisabledToggle.getState() && !other.configOption.get())) continue;
                if (other == dragged) continue;

                for (Edge targetEdge : targetEdges) {
                    int otherValue = targetEdge.get(other);
                    if (Math.abs(draggedValue - otherValue) < snapThreshold) {
                        snappedCoord = draggedEdge.adjust(otherValue, dragged);

                        if (draggedEdge.isHorizontal()) {
                            this.snappingX = true;
                            this.snapLineX = otherValue;
                        } else {
                            this.snappingY = true;
                            this.snapLineY = otherValue;
                        }

                        return snappedCoord;
                    }
                }
            }
        }

        // If nothing snapped, reset flags
        if (direction == 1) {
            this.snappingX = false;
            this.snapLineX = -1;
        } else {
            this.snappingY = false;
            this.snapLineY = -1;
        }

        return newCoord;
    }

    public void open() {
        Frozen.guiToOpen = this;
        opened = true;
    }

    private void saveConfig() {
        Map<String, Object> data = new HashMap<>();
        data.put("show_invisible", showinvisible);
        data.put("show_disabled", showDisabled);
        data.put("snap", snap);

        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        configFile.getParentFile().mkdirs();

        if (!configFile.exists()) return;

        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> loaded;

        try (FileReader reader = new FileReader(configFile)) {
            loaded = GSON.fromJson(reader, type);
            showinvisible = loaded.get("show_invisible") instanceof Boolean && (boolean) loaded.get("show_invisible");
            showDisabled = loaded.get("show_disabled") instanceof Boolean && (boolean) loaded.get("show_disabled");
            snap = loaded.get("snap") instanceof Boolean && (boolean) loaded.get("snap");
        } catch (IOException | ClassCastException e) {
            e.printStackTrace(); // optionally show an error dialog/log
        }
    }

    public enum Edge {
        LEFT, RIGHT, CENTER_X, TOP, BOTTOM, CENTER_Y;

        public int get(Overlay o) {
            switch (this) {
                case LEFT: return o.getX();
                case RIGHT: return o.getX() + o.getWidth();
                case CENTER_X: return o.getX() + o.getWidth() / 2;
                case TOP: return o.getY();
                case BOTTOM: return o.getY() + o.getHeight();
                case CENTER_Y: return o.getY() + o.getHeight() / 2;
            }
            return 0;
        }

        public int adjust(int target, Overlay o) {
            switch (this) {
                case LEFT: return target;
                case CENTER_X: return target - o.getWidth() / 2;
                case RIGHT: return target - o.getWidth();
                case TOP: return target;
                case CENTER_Y: return target - o.getHeight() / 2;
                case BOTTOM: return target - o.getHeight();
            }
            return target;
        }

        public boolean isHorizontal() {
            return this == LEFT || this == RIGHT || this == CENTER_X;
        }
    }
}
