package com.github.spookie6.frozen.mixin;

import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemRenderer.class)
public interface AccessorItemRenderer {
    @Accessor("equippedProgress")
    float getEquippedProgress();

    @Invoker("transformFirstPersonItem")
    void invokeTransformFirstPersonItem(float equipProgress, float swingProgress);

    @Invoker("doBlockTransformations")
    void invokeDoBlockTransformations();
}
