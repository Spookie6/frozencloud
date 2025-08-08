package com.github.spookie6.frozen.mixin;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.skyblock.ItemUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.github.spookie6.frozen.Frozen.mc;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    // Shadow the private vanilla method so we can call it
    @Shadow
    private void doBlockTransformations() {}

    @Redirect(
            method = "renderItemInFirstPerson",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemRenderer;doBlockTransformations()V"
            )
    )
    private void redirectDoBlockTransformations(ItemRenderer instance) {
        EntityPlayerSP player = mc.thePlayer;
        if (player == null) {
            doBlockTransformations();
            return;
        }

        ItemStack stack = player.getHeldItem();
        if (stack == null) {
            doBlockTransformations();
            return;
        }

        if (ModConfig.legacyAxes) {
            String heldItemID = ItemUtils.getSkyBlockID(stack);
            if (heldItemID != null &&
                    (heldItemID.equals("AXE_OF_THE_SHREDDED") ||
                            heldItemID.equals("RAGNAROCK_AXE") ||
                            heldItemID.equals("DAEDALUS_AXE") ||
                            heldItemID.equals("STARRED_DAEDALUS_AXE"))) {
                return; // Skip block pose entirely
            }
        }

        // Normal behaviour
        doBlockTransformations();
    }
}