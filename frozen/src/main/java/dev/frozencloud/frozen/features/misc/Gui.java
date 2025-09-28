package dev.frozencloud.frozen.features.misc;

import dev.frozencloud.frozen.config.ModConfig;
import dev.frozencloud.frozen.utils.ChatUtils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import static dev.frozencloud.frozen.Frozen.mc;

public class Gui {
    @SubscribeEvent
    public void onGuiMouseInput(GuiScreenEvent.MouseInputEvent.Pre e) { handleGuiInput(true, e); }

    @SubscribeEvent
    public void onGuiKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre e) { handleGuiInput(false, e); }

    private void handleGuiInput(boolean mouseInput, GuiScreenEvent e) {
        if (mc.theWorld == null) return;

        if (e.gui == null) return;
        if (!(e.gui instanceof GuiChest)) return;
        GuiChest gui = (GuiChest) e.gui;
        if (!(gui.inventorySlots instanceof ContainerChest)) return;
        ContainerChest chest = (ContainerChest) gui.inventorySlots;

        String containerName = chest.getLowerChestInventory().getName().toLowerCase();

//        Various packets based click input replacements
        if (mouseInput) {
            int btn = Mouse.getEventButton();
            if (btn != 0) return;

            if (!Mouse.getEventButtonState()) return;
            if (gui.getSlotUnderMouse() == null) return;

            switch (containerName) {
                case "trades":
                case "booster cookie":
                    if (!ModConfig.tradesPacket) return;
                    sendWindowPacket(e, chest.windowId, gui.getSlotUnderMouse().slotNumber);
                    break;
                case "your equipment and stats":
                    if (!ModConfig.equipmentPacket) return;
                    sendWindowPacket(e, chest.windowId, gui.getSlotUnderMouse().slotNumber);
                    break;
                default:
                    if (containerName.startsWith("pets")) {
                        if (!ModConfig.petsPacket) return;
                        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) return;
                        sendWindowPacket(e, chest.windowId, gui.getSlotUnderMouse().slotNumber);
                        break;
                    }
            }
        }

//        Wardrobe keybinds
        if (!mouseInput) {
            if (!ModConfig.wardrobePackets) return;
            if (!containerName.startsWith("wardrobe")) return;

            int slot = 0;

            if (ModConfig.wdKeyOne.isActive()) slot = 36;
            if (ModConfig.wdKeyTwo.isActive()) slot = 37;
            if (ModConfig.wdKeyThree.isActive()) slot = 38;
            if (ModConfig.wdKeyFour.isActive()) slot = 39;
            if (ModConfig.wdKeyFive.isActive()) slot = 40;
            if (ModConfig.wdKeySix.isActive()) slot = 41;
            if (ModConfig.wdKeySeven.isActive()) slot = 42;
            if (ModConfig.wdKeyEight.isActive()) slot = 43;
            if (ModConfig.wdKeyNine.isActive()) slot = 44;

            if (slot == 0) return;

            if (ModConfig.wardrobePreventUnequip && chest.getLowerChestInventory().getStackInSlot(slot).getDisplayName().contains("Equipped")) {
                ChatUtils.sendModInfo("Wardrobe slot already quipped!");
                e.setCanceled(true);
                return;
            }

            try {
                NetHandlerPlayClient client =  mc.getNetHandler();
                client.getNetworkManager().sendPacket(new C0EPacketClickWindow(chest.windowId, slot, 0, 0, null, (short) 0));
            } catch(NullPointerException ignored) {;}
            e.setCanceled(true);
        }
    }

    private void sendWindowPacket(Event e, int windowId, int slotNumber) {
        try {
            NetHandlerPlayClient client =  mc.getNetHandler();
            client.getNetworkManager().sendPacket(new C0EPacketClickWindow(windowId, slotNumber, 0, 0, null, (short) 0));
            e.setCanceled(true);
        } catch(NullPointerException ignored) {;}
    }
}
