package com.github.spookie6.frozen.mixin;

import cc.polyfrost.oneconfig.config.core.OneColor;
import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.render.GuiRenderer;
import com.github.spookie6.frozen.utils.skyblock.ItemUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.Rarity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer {

    @Inject(method = "drawSlot", at = @At("HEAD"))
    public void frozen$onRenderSlot(Slot slotIn, CallbackInfo ci) {
        if (!ModConfig.rarityHighlight) return;
        ItemStack stack = slotIn.getStack();
        if (stack == null) return;

        Rarity rarity = ItemUtils.getSkyblockRarity(stack);
        if (rarity.equals(Rarity.UNKNOWN)) return;

        int x = slotIn.xDisplayPosition;
        int y = slotIn.yDisplayPosition;

        OneColor color = rarity.getColor().getColor();
        float alpha = ModConfig.rarityHighlightOpacity / 100f;

        GuiRenderer.drawTintedOverlay(GuiRenderer.RenderShape.values()[ModConfig.rarityHighlightType].getTex(), x, y, color, alpha);
    }
}
