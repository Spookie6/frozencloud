package com.github.spookie6.frozen.mixin;

import cc.polyfrost.oneconfig.config.core.OneColor;
import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.render.GuiRenderer;
import com.github.spookie6.frozen.utils.skyblock.ItemUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.Rarity;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "renderHotbarItem", at = @At("HEAD"))
    public void frozen$onRenderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player, CallbackInfo ci) {
        if (!ModConfig.rarityHighlight || !ModConfig.rarityShowInHotbar) return;

        ItemStack itemStack = player.inventory.getStackInSlot(index);
        if (itemStack == null) return;

        Rarity rarity = ItemUtils.getSkyblockRarity(itemStack);
        if (rarity.equals(Rarity.UNKNOWN)) return;

        OneColor color = rarity.getColor().getColor();
        float alpha = ModConfig.rarityHighlightOpacity / 100f;

        GuiRenderer.drawTintedOverlay(GuiRenderer.RenderShape.values()[ModConfig.rarityHighlightType].getTex(), xPos, yPos, color, alpha);
    }
}
