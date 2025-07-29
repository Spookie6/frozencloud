//package com.github.spookie6.frozen.mixin;
//
//import cc.polyfrost.oneconfig.config.core.OneColor;
//import com.github.spookie6.frozen.utils.render.Color;
//import com.github.spookie6.frozen.utils.render.Renderer;
//import net.minecraft.client.model.ModelBase;
//import net.minecraft.client.renderer.entity.RenderManager;
//import net.minecraft.client.renderer.entity.RendererLivingEntity;
//import net.minecraft.entity.EntityLivingBase;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(RenderLivingBase.class)
//public abstract class MixinRenderLivingBase<T extends EntityLivingBase> extends RendererLivingEntity<T> {
//
//    public MixinRenderLivingBase(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
//        super(renderManagerIn, modelBaseIn, shadowSizeIn);
//    }
//
//    @Inject(method = "doRender", at = @At("HEAD"))
//    private void onDoRender(EntityLivingBase entity, double x, double y, double z, float yaw, float partialTicks, CallbackInfo ci) {
//        if (!entity.hasCustomName()) return;
//        String name = entity.getCustomNameTag();
//
//        System.out.println(name);
//
//        if (name.endsWith("✯")) {
//            // Draw your ESP box, AABB, etc.
//            Renderer.drawEntityAABB(entity, new OneColor(0, 0, 255, 255), true, true, 2.5f, partialTicks);
//        }
//    }
//}