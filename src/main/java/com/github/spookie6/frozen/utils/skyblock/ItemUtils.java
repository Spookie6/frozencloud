package com.github.spookie6.frozen.utils.skyblock;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemUtils {
    public static String getSkyBlockID(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) return null;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return null;
        NBTTagCompound extra = tag.getCompoundTag("ExtraAttributes");
        return extra.hasKey("id") ? extra.getString("id") : null;
    }

    public static String getDisplayName(ItemStack stack) {
        if (stack == null) return null;
        return ChatFormatting.stripFormatting(stack.getDisplayName());
    }

    public static ItemStack getHeldItem() {
        if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null) return null;
        return Minecraft.getMinecraft().thePlayer.getHeldItem();
    }

    public static ItemStack getItemInSlot(int slot) {
        if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null) return null;
        return Minecraft.getMinecraft().thePlayer.getEquipmentInSlot(slot);
    }
}
