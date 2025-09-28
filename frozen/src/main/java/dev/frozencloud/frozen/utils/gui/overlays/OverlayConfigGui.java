package dev.frozencloud.frozen.utils.gui.overlays;

import dev.frozencloud.frozen.Frozen;
import dev.frozencloud.frozen.utils.Button;
import dev.frozencloud.frozen.utils.gui.components.ColorPicker;
import dev.frozencloud.frozen.utils.gui.components.IntegerInput;
import dev.frozencloud.frozen.utils.gui.components.TabsRow;
import dev.frozencloud.frozen.utils.gui.components.ToggleSwitch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OverlayConfigGui extends Gui {
    public final int x, y;
    public static final int width = 200;
    public static int height = 200;

    protected final Overlay overlay;

    private final int margin = 3;
    private ColorPicker colorPicker = null;

    private TabsRow tabsRow;
    private ToggleSwitch configOptiontoggle;
    private dev.frozencloud.frozen.utils.gui.components.Button resetButton;
    private ToggleSwitch shadowToggle;
    private ToggleSwitch rightAlignToggle;
    private IntegerInput extraWidthInput;

    public OverlayConfigGui(Overlay overlay, int mx, int my) {
        this.overlay = overlay;
        this.x = mx;
        this.y = my;

        int baseY = my + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + margin * 2;
        if (overlay instanceof TextOverlay) {
            TextOverlay ov = (TextOverlay) overlay;
            List<String> options = new ArrayList<>();
            if (ov.getTitleSupplier() != null) options.add("Title");
            if (options.isEmpty()) {
                height = 176;
            } else {
                options.add("Body");
                String[] arr = new String[options.size()];
                options.toArray(arr);
                this.tabsRow = new TabsRow(mx + margin, baseY, arr, "Body");
            }
        } else {
            height = 176;
        }

        this.colorPicker = new ColorPicker(mx + margin, this.tabsRow == null ? baseY : baseY + 22, overlay.color);
        this.configOptiontoggle = new ToggleSwitch(mx + 110, baseY, overlay.configOption.get(), "Enabled");
        this.resetButton = new dev.frozencloud.frozen.utils.gui.components.Button(mx + 110, baseY + 24, "Reset", () -> {
            overlay.reset();
            colorPicker.setColor(overlay.color);
        });
        this.shadowToggle = new ToggleSwitch(mx + 110, baseY + 48, overlay.hasShadow(), "Shadow");
        if (overlay instanceof TextOverlay) {
            if (((TextOverlay) overlay).getRightAlign() != null) this.rightAlignToggle = new ToggleSwitch(mx + 110, baseY + 72, ((TextOverlay) overlay).getRightAlign().get(), "Right Align");
            if (((TextOverlay) overlay).getExtraWidth() != null) this.extraWidthInput = new IntegerInput(mx + 110, baseY + 96, ((TextOverlay) overlay).getExtraWidth().get(), 0, 50, 1);
        }
    }

    public void draw(int mx, int my, Minecraft mc, float partialTicks) {
        drawRect(x, y, x + width, this.y + height, 0xFF424242);
        mc.fontRendererObj.drawString(overlay.displayName, x + margin, y + margin, 0xFFC1C1C1);

        if (tabsRow != null) tabsRow.draw(mx, my, mc);
        colorPicker.draw(mx, my, mc);

        updateColor();

        configOptiontoggle.draw(mc, partialTicks);
        resetButton.draw(mx, my, mc);
        shadowToggle.draw(mc, partialTicks);
        if (rightAlignToggle != null) rightAlignToggle.draw(mc, partialTicks);
        if (extraWidthInput != null) extraWidthInput.draw(mx, my, mc);
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void mouseClicked(Button button, int mouseX, int mouseY) {
        colorPicker.mouseClicked(button, mouseX, mouseY);
        updateColor();

        if (tabsRow != null && tabsRow.isMouseOver(mouseX, mouseY)) {
            tabsRow.mouseClicked(button, mouseX, mouseY);
        }
        if (configOptiontoggle.isMouseOver(mouseX, mouseY)) {
            configOptiontoggle.toggle();
            overlay.configOption.set(configOptiontoggle.getState());
        }
        resetButton.mouseClicked(button, mouseX, mouseY);
        if (shadowToggle.isMouseOver(mouseX, mouseY)) {
            shadowToggle.toggle();
            overlay.setShadow(shadowToggle.getState());
        }
        if (rightAlignToggle != null && rightAlignToggle.isMouseOver(mouseX, mouseY)) {
            rightAlignToggle.toggle();
            ((TextOverlay) overlay).getRightAlign().set(rightAlignToggle.getState());
            overlay.updateDimensions();
        }
        if (extraWidthInput != null && extraWidthInput.isMouseOver(mouseX, mouseY)) {
            extraWidthInput.mouseClicked(button, mouseX, mouseY);
            ((TextOverlay) overlay).getExtraWidth().set(extraWidthInput.getValue());
            overlay.updateDimensions();
        }
        if (tabsRow != null) {
            String selected = tabsRow.getSelected();
            switch (selected) {
                case "Title":
                    colorPicker.setColor(((TextOverlay) overlay).titleColor);
                    break;
                case "Body":
                    colorPicker.setColor(overlay.color);
                    break;
            }
        }
    }

    public void mouseReleased() {
        colorPicker.mouseReleased();
        updateColor();
        overlay.updateConfig();
    }

    public void handleMouseInput(int mx, int my, int dWheel) {
        if (extraWidthInput != null && extraWidthInput.isMouseOver(mx, my)) {
            extraWidthInput.handleMouseInput(dWheel);
            ((TextOverlay) overlay).getExtraWidth().set(extraWidthInput.getValue());
            overlay.updateDimensions();
        }
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        colorPicker.keyTyped(typedChar, keyCode);
        updateColor();
    }

    private void updateColor() {
        if  (tabsRow == null) {
            overlay.setColor(colorPicker.getColor());
        } else {
            switch (tabsRow.getSelected()) {
                case "Title":
                    ((TextOverlay) overlay).setTitleColor(colorPicker.getColor());
                    break;
                case "Body":
                    overlay.setColor(colorPicker.getColor());
                    break;
            }
        }
    }

    public static boolean fits(int mx, int my) {
        ScaledResolution res = Frozen.guiOverlayEditor.res;

        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();

        return (mx + width < screenWidth && my + height < screenHeight);
    }
}