package com.github.spookie6.frozen.features.misc;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.events.impl.ChatPacketEvent;
import com.github.spookie6.frozen.utils.skyblock.LocationUtils;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import com.github.spookie6.frozen.utils.ChatUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.github.spookie6.frozen.Frozen.mc;

public class Refills {
    private long lastUpdate = -1;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (!e.phase.equals(TickEvent.Phase.END) || !LocationUtils.isInSkyblock) return;
        long now = Minecraft.getSystemTime();
        if (now - lastUpdate < 2000) return;
        lastUpdate = now;

        if (ModConfig.pearlRefill == 1) {
            refillItem("Ender Pearl", ModConfig.pearlRefillThreshold);
        }

        if (ModConfig.jerryRefill == 1) {
            refillItem("Inflatable Jerry", ModConfig.jerryRefillThreshold);
        }

        if (ModConfig.superboomRefill == 1) {
            refillItem("Superboom TNT", ModConfig.superboomRefillThreshold);
        }
    }

    private static final Pattern startRegex = Pattern.compile("\\[NPC] Mort: Here, I found this map when I first entered the dungeon\\.|\\[NPC] Mort: Right-click the Orb for spells, and Left-click \\(or Drop\\) to use your Ultimate");

    @SubscribeEvent
    public void onChatPacket(ChatPacketEvent e) {
        if (startRegex.matcher(e.message).find()) {
            if (ModConfig.pearlRefill == 2) {
                refillItem("Ender Pearl", -1);
            }

            if (ModConfig.jerryRefill == 2) {
                refillItem("Inflatable Jerry", -1);
            }

            if (ModConfig.superboomRefill == 2) {
                refillItem("Superboom TNT", -1);
            }
        }
    }

    private void refillItem(String itemName, int threshold) {
        int maxItems = itemName.equals("Ender Pearl") ? 16 : 64;

        EntityPlayer player = mc.thePlayer;
        if (mc.theWorld == null || player.inventory == null || player.inventory.mainInventory == null) return;

        ItemStack itemStack = Arrays.stream(player.inventory.mainInventory)
                .filter(Objects::nonNull)
                .filter(x -> x.getDisplayName() != null)
                .filter(x -> ChatFormatting.stripFormatting(x.getDisplayName()).trim().equals(itemName))
                .findFirst()
                .orElse(null);
        if (itemStack == null || (threshold > 0 && itemStack.stackSize > threshold) || itemStack.stackSize == maxItems) return;

        ChatUtils.sendCommand("gfs " + itemName.replaceAll(" ", "_").toLowerCase() + " " + String.valueOf(maxItems - itemStack.stackSize), false);
    }
}
