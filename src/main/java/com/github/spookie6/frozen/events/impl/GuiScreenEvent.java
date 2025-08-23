package com.github.spookie6.frozen.events.impl;

import com.github.spookie6.frozen.utils.Button;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class GuiScreenEvent extends Event {
    public final GuiScreen gui;
    private final CallbackInfo cbi;

    public GuiScreenEvent(GuiScreen gui, CallbackInfo cbi) {
        this.gui = gui;
        this.cbi = cbi;
    }

    public void cancel() { this.cbi.cancel(); }

    public static class ChestOpened extends GuiScreenEvent {
        private final String lowerContainerName;

        public ChestOpened(GuiChest gui, CallbackInfo cbi) {
            super(gui, cbi);

            this.lowerContainerName = ((ContainerChest) gui.inventorySlots).getLowerChestInventory().getName();
        }

        public String getLowerContainerName() {
            return lowerContainerName;
        }
    }

    public static class MouseClicked extends GuiScreenEvent {
        public final Button btn;
        public final int mx, my;

        public MouseClicked (GuiScreen gui, int mx, int my, int btn, CallbackInfo cbi) {
            super(gui, cbi);
            this.btn = Button.getButton(btn);
            this.mx = mx;
            this.my = my;
        }
    }

    public static class MouseReleased extends GuiScreenEvent {
        public final Button btn;
        public final int mx, my;

        public MouseReleased (GuiScreen gui, int mx, int my, int btn, CallbackInfo cbi) {
            super(gui, cbi);
            this.btn = Button.getButton(btn);
            this.mx = mx;
            this.my = my;
        }
    }

    public static class MouseInput extends GuiScreenEvent {
        public final int mx, my;
        public final int dWheel;

        public MouseInput(GuiScreen gui, CallbackInfo cbi) {
            super(gui, cbi);
            Mouse.poll();
            this.mx = Mouse.getX();
            this.my = Mouse.getY();
            this.dWheel = Mouse.getDWheel();
        }
    }

    public static class KeyTyped extends GuiScreenEvent {
        public final int keyCode;
        public final int typedChar;

        public KeyTyped(GuiScreen gui, int keyCode, char typedChar, CallbackInfo cbi) {
            super(gui, cbi);
            this.keyCode = keyCode;
            this.typedChar = typedChar;
        }
    }
}
