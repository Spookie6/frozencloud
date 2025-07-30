package com.github.spookie6.frozen.events.impl;

import com.github.spookie6.frozen.utils.Button;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GuiScreenEvent extends Event {
    public final GuiScreen gui;

    public GuiScreenEvent(GuiScreen gui) {
        this.gui = gui;
    }

    public static class MouseClicked extends GuiScreenEvent {
        public final Button btn;
        public final int mx, my;

        public MouseClicked (GuiScreen gui, int mx, int my, int btn) {
            super(gui);
            this.btn = Button.getButton(btn);
            this.mx = mx;
            this.my = my;
        }
    }

    public static class MouseReleased extends GuiScreenEvent {
        public final Button btn;
        public final int mx, my;

        public MouseReleased (GuiScreen gui, int mx, int my, int btn) {
            super(gui);
            this.btn = Button.getButton(btn);
            this.mx = mx;
            this.my = my;
        }
    }
}
