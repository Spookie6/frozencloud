package com.github.spookie6.frozen.events.impl;

import com.github.spookie6.frozen.utils.Button;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GuiContainerEvent extends Event {
    public final GuiContainer gui;

    public GuiContainerEvent(GuiContainer gui) {
        this.gui = gui;
    }

    public static class MouseClicked extends GuiContainerEvent {
        public final Button btn;
        public final int mx, my;

        public MouseClicked (GuiContainer gui, int mx, int my, int btn) {
            super(gui);
            this.btn = Button.getButton(btn);
            this.mx = mx;
            this.my = my;
        }
    }

    public static class MouseReleased extends GuiContainerEvent {
        public final Button btn;
        public final int mx, my;

        public MouseReleased (GuiContainer gui, int mx, int my, int btn) {
            super(gui);
            this.btn = Button.getButton(btn);
            this.mx = mx;
            this.my = my;
        }
    }
}
