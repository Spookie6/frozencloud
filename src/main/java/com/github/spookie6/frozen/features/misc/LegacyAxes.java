package com.github.spookie6.frozen.features.misc;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.skyblock.SkyblockAxeModel;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LegacyAxes {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        if (!ModConfig.legacyAxes) return;

        IBakedModel goldenAxe = event.modelRegistry.getObject(
                new ModelResourceLocation("minecraft:golden_axe", "inventory")
        );

        IBakedModel diamondAxe = event.modelRegistry.getObject(
                new ModelResourceLocation("minecraft:diamond_axe", "inventory")
        );

        // Wrap diamond_sword model
        ModelResourceLocation diamondSwordLoc = new ModelResourceLocation("minecraft:diamond_sword", "inventory");
        IBakedModel diamondSword = event.modelRegistry.getObject(diamondSwordLoc);
        if (diamondSword != null && goldenAxe != null) {
            event.modelRegistry.putObject(diamondSwordLoc, new SkyblockAxeModel(diamondSword, diamondAxe));
        }

        // Wrap golden_sword model
        ModelResourceLocation goldenSwordLoc = new ModelResourceLocation("minecraft:golden_sword", "inventory");
        IBakedModel goldenSword = event.modelRegistry.getObject(goldenSwordLoc);
        if (goldenSword != null && goldenAxe != null) {
            event.modelRegistry.putObject(goldenSwordLoc, new SkyblockAxeModel(goldenSword, goldenAxe));
        }
    }
}
