package com.github.spookie6.frozen.utils.skyblock;

import com.github.spookie6.frozen.utils.StringUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.Rarity;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.regex.Pattern;

import static com.github.spookie6.frozen.Frozen.mc;

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

    private static final Pattern RARITY_PATTERN = Pattern.compile(
            "\\b(VERY\\s+SPECIAL|SPECIAL|DIVINE|MYTHIC|LEGENDARY|EPIC|RARE|UNCOMMON|COMMON)\\b",
            Pattern.CASE_INSENSITIVE
    );

    public static Rarity getSkyblockRarity(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) return Rarity.UNKNOWN;

        NBTTagCompound display = stack.getSubCompound("display", false);
        if (display == null || !display.hasKey("Lore", 9)) return Rarity.UNKNOWN;

        NBTTagList lore = display.getTagList("Lore", 8); // 8 = NBT string
        if (lore.tagCount() == 0) return Rarity.UNKNOWN;

        // scan from bottom up; rarity is near the end in most cases
        for (int i = lore.tagCount() - 1; i >= 0; i--) {
            String raw = lore.getStringTagAt(i);
            if (raw == null || raw.isEmpty()) continue;

            // 1) remove obfuscated segments: §k + <one char>
            //    do this BEFORE stripping other formatting; otherwise the obfuscated char survives.
            raw = raw.replaceAll("(?i)\u00A7k.", "");

            // 2) strip all remaining Minecraft formatting codes
            String line = net.minecraft.util.StringUtils.stripControlCodes(raw);
            if (line == null || line.isEmpty()) continue;

            // 3) normalize: keep letters/spaces, drop stars, punctuation, emojis, etc.
            String cleaned = line.toUpperCase(java.util.Locale.ROOT)
                    .replaceAll("[^A-Z ]", " ")
                    .replaceAll("\\s+", " ")
                    .trim();

            // 4) find rarity token anywhere on the line
            java.util.regex.Matcher m = RARITY_PATTERN.matcher(cleaned);
            if (m.find()) {
                String token = m.group(1).replace(' ', '_');
                Rarity r = Rarity.getRarityByString(token);
                if (r != null && r != Rarity.UNKNOWN) return r;
            }
        }
        return Rarity.UNKNOWN;
    }
}
