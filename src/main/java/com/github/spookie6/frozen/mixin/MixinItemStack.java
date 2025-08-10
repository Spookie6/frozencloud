package com.github.spookie6.frozen.mixin;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.skyblock.ItemUtils;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class, priority = 9999)
public abstract class MixinItemStack {
    @Shadow public abstract boolean hasTagCompound();

    @Shadow private NBTTagCompound stackTagCompound;

    @Unique
    private boolean frozen$isLegacyBlockedAxe(ItemStack stack) {
        if (stack == null) return false;

        String id = ItemUtils.getSkyBlockID(stack);
        return "AXE_OF_THE_SHREDDED".equals(id) ||
                "RAGNAROCK_AXE".equals(id) ||
                "DAEDALUS_AXE".equals(id) ||
                "STARRED_DAEDALUS_AXE".equals(id);
    }

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    public void onGetDisplayName(CallbackInfoReturnable<String> cir) {
        try {
            if (!ModConfig.legacyAxes || !hasTagCompound() || !stackTagCompound.hasKey("ExtraAttributes", 10)) return;

            String itemID = ItemUtils.getSkyBlockID((ItemStack) (Object) this);
            if (itemID == null) return;

            String originalName = cir.getReturnValue();

            if (itemID.equals("AXE_OF_THE_SHREDDED")) {
                cir.setReturnValue(originalName.replace("Halberd", "Axe"));
            } else if (itemID.equals("DAEDALUS_AXE") || itemID.equals("STARRED_DAEDALUS_AXE")) {
                cir.setReturnValue(originalName.replace("Blade", "Axe"));
            } else if (itemID.equals("RAGNAROCK_AXE")) {
                cir.setReturnValue(originalName.replace("Ragnarock", "Ragnarock Axe"));
            }
        } catch (Exception ignored) {}
    }

    @Inject(method = "getItemUseAction", at = @At("HEAD"), cancellable = true)
    public void onGetItemUseAction(CallbackInfoReturnable<EnumAction> cir) {
        if (!ModConfig.legacyAxes) return;
        if (frozen$isLegacyBlockedAxe((ItemStack) (Object) this)) {
            cir.setReturnValue(EnumAction.NONE);
        }
    }
}
