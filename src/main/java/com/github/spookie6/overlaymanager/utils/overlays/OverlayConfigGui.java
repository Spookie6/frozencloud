package com.github.spookie6.overlaymanager.utils.overlays;

import com.github.spookie6.overlaymanager.OverlayManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;

public class OverlayConfigGui extends Gui {
    public final int x, y;
    public static final int width = 200;
    public static final int height = 176;

    private final Overlay overlay;

    private final int margin = 3;
    private ColorPicker colorPicker = null;

    private ToggleSwitch configOptiontoggle;
    private Button resetButton;
    private ToggleSwitch shadowToggle;

    public OverlayConfigGui(Overlay overlay, int mx, int my) {
        this.overlay = overlay;
        this.x = mx;
        this.y = my;

        int baseY = my + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + margin * 2;
        this.colorPicker = new ColorPicker(mx + margin, baseY, overlay.color);
        this.configOptiontoggle = new ToggleSwitch(mx + 110, baseY, overlay.configOption.get(), "Enabled");
        this.resetButton = new Button(mx + 110, baseY + 24, "Reset", () -> {
            overlay.reset();
            colorPicker.setColor(overlay.color);
        });
        if (overlay instanceof TextOverlay) {
            this.shadowToggle = new ToggleSwitch(mx + 110, baseY + 48, ((TextOverlay) overlay).hasShadow(), "Shadow");
        }
    }

    public void draw(int mx, int my, Minecraft mc) {
        drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF424242);
        mc.fontRendererObj.drawString(overlay.displayName, x + margin, y + margin, 0xFFC1C1C1);

        colorPicker.draw(mx, my, mc);
        overlay.setColor(colorPicker.getColor());

        configOptiontoggle.draw(mc);
        resetButton.draw(mx, my, mc);
        if (shadowToggle != null) shadowToggle.draw(mc);
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void mouseClicked(GuiOverlayEditor.Button button, int mouseX, int mouseY) {
        colorPicker.mouseClicked(button, mouseX, mouseY);
        overlay.setColor(colorPicker.getColor());

        if (configOptiontoggle.isMouseOver(mouseX, mouseY)) {
            configOptiontoggle.toggle();
            overlay.configOption.set(configOptiontoggle.getState());
        }
        resetButton.mouseClicked(button, mouseX, mouseY);
        if (shadowToggle != null && shadowToggle.isMouseOver(mouseX, mouseY)) {
            shadowToggle.toggle();
            overlay.setShadow(shadowToggle.getState());
        }
    }

    public void mouseReleased() {
        colorPicker.mouseReleased();
        overlay.setColor(colorPicker.getColor());
        overlay.updateConfig();
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        colorPicker.keyTyped(typedChar, keyCode);
        overlay.setColor(colorPicker.getColor());
    }

    public static boolean fits(int mx, int my) {
        ScaledResolution res = OverlayManager.guiOverlayEditor.res;

        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();

        return (mx + width < screenWidth || my + height < screenHeight);
    }
}