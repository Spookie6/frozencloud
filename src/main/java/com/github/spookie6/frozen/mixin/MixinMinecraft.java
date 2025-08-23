package com.github.spookie6.frozen.mixin;

import com.github.spookie6.frozen.events.impl.GuiScreenEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "displayGuiScreen", at = @At("HEAD"))
    private void onDisplayGuiScreen(GuiScreen guiScreen, CallbackInfo ci) {
        if (guiScreen instanceof GuiChest) {
            MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.ChestOpened((GuiChest) guiScreen, ci));
        }
    }
}