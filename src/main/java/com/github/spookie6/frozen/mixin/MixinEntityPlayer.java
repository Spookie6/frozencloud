package com.github.spookie6.frozen.mixin;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.skyblock.ItemUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer {

    @Shadow public abstract ItemStack getHeldItem();

    @Unique
    private boolean frozen$isLegacyBlockedAxe(ItemStack stack) {
        if (stack == null) return false;

        String id = ItemUtils.getSkyBlockID(stack);
        return "AXE_OF_THE_SHREDDED".equals(id) ||
                "RAGNAROCK_AXE".equals(id) ||
                "DAEDALUS_AXE".equals(id) ||
                "STARRED_DAEDALUS_AXE".equals(id);
    }

    @Inject(method = "getItemInUseCount", at = @At("HEAD"), cancellable = true)
    public void onGetItemInUseCount(CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.legacyAxes && frozen$isLegacyBlockedAxe(getHeldItem())) {
            cir.setReturnValue(0);
        } else {
            cir.setReturnValue(cir.getReturnValue());
        }
    }
}
