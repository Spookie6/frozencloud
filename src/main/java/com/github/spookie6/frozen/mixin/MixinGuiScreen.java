package com.github.spookie6.frozen.mixin;

import com.github.spookie6.frozen.events.impl.GuiScreenEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public abstract class MixinGuiScreen extends net.minecraft.client.gui.GuiScreen {

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void onMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseClicked(this, mouseX, mouseY, mouseButton, ci));
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void onMouseReleased(int mouseX, int mouseY, int state, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseReleased(this, mouseX, mouseY, state, ci));
    }

    @Inject(method = "handleMouseInput", at = @At("HEAD"))
    private void onMouseInput(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.MouseInput(this, ci));
    }

    @Inject(method = "keyTyped", at = @At("HEAD"))
    private void onKeyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyTyped(this, keyCode, typedChar, ci));
    }
}