//package com.github.spookie6.frozen.mixin;
//
//import com.github.spookie6.frozen.config.ModConfig;
//import com.github.spookie6.frozen.utils.skyblock.ItemUtils;
//import net.minecraft.item.ItemStack;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin({ItemStack.class})
//public abstract class MixinItemStack {
//
//    @Unique
//    private boolean frozen$isLegacyBlockedAxe(ItemStack stack) {
//        if (stack == null) return false;
//
//        String id = ItemUtils.getSkyBlockID(stack);
//        return "AXE_OF_THE_SHREDDED".equals(id) ||
//                "RAGNAROCK_AXE".equals(id) ||
//                "DAEDALUS_AXE".equals(id) ||
//                "STARRED_DAEDALUS_AXE".equals(id);
//    }
//
//    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
//    private void frozen$renameLegacyAxes(CallbackInfoReturnable<String> cir) {
//        try {
//            if (!ModConfig.legacyAxes) return;
//
//            String originalName = cir.getReturnValue();
//            String itemID = ItemUtils.getSkyBlockID((ItemStack) (Object) this);
//            if (itemID != null && !itemID.isEmpty() && frozen$isLegacyBlockedAxe((ItemStack) (Object) this)) {
//                switch(itemID) {
//                    case "AXE_OF_THE_SHREDDED":
//                        cir.setReturnValue(originalName.replace("Halbert", "Axe"));
//                        break;
//                    case "RAGNAROCK_AXE":
//                        cir.setReturnValue(originalName.replace("Ragnarock", "Ragnarock Axe"));
//                        break;
//                    case "DAEDALUS_AXE":
//                    case "STARRED_DAEDALUS_AXE":
//                        cir.setReturnValue(originalName.replace("Blade", "Axe"));
//                        break;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}