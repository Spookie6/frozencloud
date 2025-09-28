package dev.frozencloud.frozen.features.misc;

import cc.polyfrost.oneconfig.config.core.OneColor;
import dev.frozencloud.frozen.config.ModConfig;
import dev.frozencloud.frozen.utils.ChatUtils;
import dev.frozencloud.frozen.utils.SlotBindingUtils;
import dev.frozencloud.frozen.utils.render.Color;
import dev.frozencloud.frozen.utils.render.GuiRenderer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SlotBinding {
    Slot bindingSlot = null;

    @SubscribeEvent
    public void onGuiDraw(GuiScreenEvent.DrawScreenEvent.Post e) {
        if (!ModConfig.slotbinding) return;
        if (!(e.gui instanceof GuiInventory)) return;
        GuiInventory gui = (GuiInventory) e.gui;

        for (Map.Entry<Integer, Integer> entry : SlotBindingUtils.colorMap.entrySet()) {
            int slotIndex = entry.getKey();
            int colorId = entry.getValue();

            Slot slot = gui.inventorySlots.inventorySlots.get(slotIndex); // assuming Forge 1.8.9
            OneColor color = ModConfig.slotbindingUniqueColors ? SlotBindingUtils.getColorById(colorId) : ModConfig.slotbindingColor; // a helper method returning actual RGB colors

            GuiRenderer.drawSlotOutline(e.gui, slot, color, ModConfig.slotbindingOutlineThickness);
        }

        if (bindingSlot != null) GuiRenderer.drawSlotOutline(e.gui, bindingSlot, Color.MINECRAFT_AQUA.getColor(), ModConfig.slotbindingOutlineThickness);

        if (gui.getSlotUnderMouse() != null) {
            Set<Integer> boundSlots = SlotBindingUtils.getDirectlyConnectedSlots(gui.getSlotUnderMouse().slotNumber);
            if (!boundSlots.isEmpty()) {
                Iterator<Integer> iterator = boundSlots.iterator();
                while(iterator.hasNext()) {
                    int n = iterator.next();
                    GuiRenderer.drawLineBetweenSlots(e.gui, gui.inventorySlots.getSlot(n), gui.getSlotUnderMouse(), ModConfig.slotbindingUniqueColors ? SlotBindingUtils.getColorById(SlotBindingUtils.colorMap.get(n)) : ModConfig.slotbindingColor, ModConfig.slotbindingOutlineThickness);
                }
            }
        }

        if (ModConfig.slotbindingClearKeybind.isActive()) {
            Slot slotUnderMouse = gui.getSlotUnderMouse();
            if (slotUnderMouse != null) {
                SlotBindingUtils.removeBinding(slotUnderMouse.slotNumber);
            }
        }

        if (ModConfig.slotbindingKeybind.isActive()) {
            if (bindingSlot == null) bindingSlot = gui.getSlotUnderMouse();
            else {
                if (gui.getSlotUnderMouse() != null) {
                    GuiRenderer.drawLineBetweenSlots(e.gui, bindingSlot, gui.getSlotUnderMouse(), Color.MINECRAFT_AQUA.getColor(), ModConfig.slotbindingOutlineThickness);
                    GuiRenderer.drawSlotOutline(e.gui, gui.getSlotUnderMouse(), Color.MINECRAFT_AQUA.getColor(), ModConfig.slotbindingOutlineThickness);
                }
            }
        } else {
            if (bindingSlot == null) return;
            Slot slotUnderMouse = gui.getSlotUnderMouse();
            if (slotUnderMouse == null) return;
            SlotBindingUtils.addBinding(bindingSlot.slotNumber, slotUnderMouse.slotNumber);
            bindingSlot = null;
            SlotBindingUtils.saveData();
        }
    }

    @SubscribeEvent
    public void onGuiMouseClick(GuiScreenEvent.MouseInputEvent e) {
        if (!ModConfig.slotbinding) return;
        if (!(e.gui instanceof GuiInventory)) return;
        GuiInventory gui = (GuiInventory) e.gui;
        ChatUtils.sendModInfo("Event triggered.");

        int btn = Mouse.getEventButton();
        ChatUtils.sendModInfo("Btn: " + btn);
        if (btn != 0 || !Mouse.getEventButtonState()) return;
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) return;
        ChatUtils.sendModInfo("Shift key is held.");

        Slot slotUnderMouse = gui.getSlotUnderMouse();
        if (slotUnderMouse == null) return;
        Map<Integer, Set<Integer>> bindings = SlotBindingUtils.getCurrentBindings();

        Set<Integer> bindingsUnderMouse = bindings.getOrDefault(slotUnderMouse.slotNumber, new HashSet<Integer>());
        if (bindingsUnderMouse.isEmpty()) return;
        ChatUtils.sendModInfo("Binding found.");

        int connectedSlot = bindingsUnderMouse.iterator().next();

        int hotbarSlot = -1;
        int inventorySlot = -1;

        if ((slotUnderMouse.slotNumber >= 9 && slotUnderMouse.slotNumber <= 35 && connectedSlot >= 36 && connectedSlot <= 44) || (slotUnderMouse.slotNumber >= 5 && slotUnderMouse.slotNumber <= 8 && connectedSlot >= 36 && connectedSlot <= 44)) {
            inventorySlot = slotUnderMouse.slotNumber;
            hotbarSlot = connectedSlot;
        } else if ((connectedSlot >= 9 && connectedSlot <= 35 && slotUnderMouse.slotNumber >= 36 && slotUnderMouse.slotNumber <= 44) || (connectedSlot >= 5 && connectedSlot <= 8 && slotUnderMouse.slotNumber >= 36 && slotUnderMouse.slotNumber <= 44)) {
            inventorySlot = connectedSlot;
            hotbarSlot = slotUnderMouse.slotNumber;
        } else {
            // Invalid slot pair (shouldn't be possible here but a safeguard nonetheless)
            return;
        }
        System.out.println("Swapping " + inventorySlot + " with hotbar " + hotbarSlot);
        SlotBindingUtils.swapSlots(inventorySlot, hotbarSlot % 36);
    }
}
