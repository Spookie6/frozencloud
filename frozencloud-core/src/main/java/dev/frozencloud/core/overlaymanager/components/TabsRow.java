package dev.frozencloud.core.overlaymanager.components;

import dev.frozencloud.core.overlaymanager.ButtonEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.*;

public class TabsRow extends Gui {
    private final int x, y;
    private final int width;
    private final int height = 16;
    private final int TAB_WIDTH = 48;

    private final int backgroundColor = 0xFF323232;
    private final int backgroundColorSelected = 0xFF222222;
    private final int textColor = 0xFFC1C1C1;
    private final int borderColor = 0xFF646464;

    private final List<String> tabNames;
    private int selectedIndex;

    public TabsRow(int x, int y, String[] options, String defaultOption) {
        if (options.length < 2) throw new IllegalArgumentException("Must have at least 2 options");

        this.tabNames = Arrays.asList(options);
        this.width = tabNames.size() * TAB_WIDTH;
        this.x = x;
        this.y = y;

        this.selectedIndex = tabNames.indexOf(defaultOption);
        if (selectedIndex == -1) throw new IllegalArgumentException("Default option not found in list");
    }

    public void draw(int mx, int my, Minecraft mc) {
        String hoveredTab = getHoveredTab(mx, my);

        for (int i = 0; i < tabNames.size(); i++) {
            String option = tabNames.get(i);
            boolean selected = (i == selectedIndex);
            int tabX = this.x + i * TAB_WIDTH;

            drawRect(tabX, y, tabX + TAB_WIDTH, y + height, selected ? backgroundColorSelected : backgroundColor);

            if (hoveredTab != null && hoveredTab.equals(option)) {
                drawHorizontalLine(tabX, tabX + TAB_WIDTH - 1, y, borderColor);
                drawHorizontalLine(tabX, tabX + TAB_WIDTH - 1, y + height - 1, borderColor);
                drawVerticalLine(tabX, y, y + height - 1, borderColor);
                drawVerticalLine(tabX + TAB_WIDTH - 1, y, y + height - 1, borderColor);
            }

            int strW = mc.fontRendererObj.getStringWidth(option);
            mc.fontRendererObj.drawString(option, tabX + TAB_WIDTH / 2 - strW / 2,
                    y + height / 2 - mc.fontRendererObj.FONT_HEIGHT / 2, textColor);
        }
    }

    public void mouseClicked(ButtonEnum button, int mouseX, int mouseY) {
        if (!button.equals(ButtonEnum.MOUSE_LEFT)) return;
        String hovered = getHoveredTab(mouseX, mouseY);
        if (hovered == null) return;
        selectedIndex = tabNames.indexOf(hovered);
    }

    private String getHoveredTab(int mx, int my) {
        if (!isMouseOver(mx, my)) return null;
        int index = (mx - this.x) / TAB_WIDTH;
        return (index >= 0 && index < tabNames.size()) ? tabNames.get(index) : null;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public String getSelected() {
        return tabNames.get(selectedIndex);
    }
}
