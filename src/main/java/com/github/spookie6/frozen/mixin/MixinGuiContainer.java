package com.github.spookie6.frozen.mixin;

import cc.polyfrost.oneconfig.config.core.OneColor;
import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.events.impl.GuiScreenEvent;
import com.github.spookie6.frozen.utils.render.GuiRenderer;
import com.github.spookie6.frozen.utils.skyblock.ItemUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.Rarity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer {

    @Unique private final Pattern frozen$petsPattern = Pattern.compile("§7\\[Lvl \\d+] §(.)\\w+");

    @Inject(method = "drawSlot", at = @At("HEAD"))
    public void frozen$onRenderSlot(Slot slotIn, CallbackInfo ci) {
        if (!ModConfig.rarityHighlight) return;
        ItemStack stack = slotIn.getStack();

        if (stack == null) return;

        int x = slotIn.xDisplayPosition;
        int y = slotIn.yDisplayPosition;
        float alpha = ModConfig.rarityHighlightOpacity / 100f;

        if ((GuiScreen) (Object) this instanceof GuiChest) {
            GuiChest chestGui = (GuiChest) (Object) this;
            ContainerChest chest = (ContainerChest) chestGui.inventorySlots;

            String lowerChestName = chest.getLowerChestInventory().getName().toLowerCase();

            if (lowerChestName.matches("^pets( (\\d/\\d))*")) {
                Matcher m = frozen$petsPattern.matcher(stack.getDisplayName());
                if (m.find()) {
                    String colorCode = m.group(1);
                    Rarity rarity = Rarity.getRarityByColorCode(colorCode);
                    if (rarity.equals(Rarity.UNKNOWN)) return;
                    OneColor color = rarity.getColor().getColor();
                    GuiRenderer.drawTintedOverlay(GuiRenderer.RenderShape.values()[ModConfig.rarityHighlightType].getTex(), x, y, color, alpha);
                    return;
                }
            };
        }

        if (ItemUtils.getSkyBlockID(stack) == null) return;

        Rarity rarity = ItemUtils.getSkyblockRarity(stack);
        if (rarity.equals(Rarity.UNKNOWN)) return;

        OneColor color = rarity.getColor().getColor();

        GuiRenderer.drawTintedOverlay(GuiRenderer.RenderShape.values()[ModConfig.rarityHighlightType].getTex(), x, y, color, alpha);
    }
}
