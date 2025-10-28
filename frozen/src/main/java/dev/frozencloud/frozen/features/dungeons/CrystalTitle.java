package dev.frozencloud.frozen.features.dungeons;

import dev.frozencloud.core.ModEnum;
import dev.frozencloud.frozen.config.ModConfig;
import dev.frozencloud.frozen.events.impl.ChatPacketEvent;
import dev.frozencloud.frozen.utils.ChatUtils;
import dev.frozencloud.core.overlaymanager.BooleanConfigBinding;
import dev.frozencloud.core.overlaymanager.OverlayManager;
import dev.frozencloud.core.overlaymanager.TextOverlay;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static dev.frozencloud.frozen.Frozen.mc;

public class CrystalTitle {

    private static long pickedUp = -1;

    public CrystalTitle() {
        OverlayManager.register(new TextOverlay(
                ModEnum.FROZEN,
                new BooleanConfigBinding(
                        () -> ModConfig.crystalTitle,
                        (val) -> ModConfig.crystalTitle = val
                ),
                "Energy crystal title",
                () -> "Place Crystal!",
                () -> pickedUp > -1,
                "Place Crystal!"
            )
        );
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onChatPacket(ChatPacketEvent e) {
        if (e.message.matches("(\\w+) picked up an Energy Crystal!")) {
            if (e.message.split(" ")[0].equals(mc.thePlayer.getDisplayNameString())) pickedUp = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (pickedUp <= 0) return;
        ItemStack item = mc.thePlayer.inventory.getStackInSlot(8);
        String displayName = ChatFormatting.stripFormatting(item.getDisplayName());

        if (!displayName.contains("Energy Crystal")) {
            if (pickedUp > 0) {
                if (ModConfig.sendCrystalTime) sendCrystalMessage();
                pickedUp = -1;
            }
        }
    }

    private static void sendCrystalMessage() {
        float time = System.currentTimeMillis() - pickedUp;
        ChatUtils.sendModInfo(String.format("Crystal placed in &a%.2fs&7.", time / 1000));
    }
}