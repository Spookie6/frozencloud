package com.github.spookie6.frozen.mixin;

import net.minecraft.client.gui.inventory.GuiContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiContainer.class)
public interface AccessorGuiContainer {
    @Accessor("guiLeft")
    int getGuiLeft_frozen();

    @Accessor("guiTop")
    int getGuiTop_frozen();
}