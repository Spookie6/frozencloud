package dev.frozencloud.frozen.utils.skyblock;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartItemModel;

import java.util.List;

public class SkyblockAxeModel implements ISmartItemModel {
    private final IBakedModel baseModel;
    private final IBakedModel axeModel;

    public SkyblockAxeModel(IBakedModel baseModel, IBakedModel axeModel) {
        this.baseModel = baseModel;
        this.axeModel = axeModel;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        if (stack != null) {
            String id = ItemUtils.getSkyBlockID(stack);
            if (id != null && (
                    id.equals("AXE_OF_THE_SHREDDED") ||
                            id.equals("RAGNAROCK_AXE") ||
                            id.equals("DAEDALUS_AXE") ||
                            id.equals("STARRED_DAEDALUS_AXE")
            )) {
                return axeModel; // Use axe model
            }
        }
        return baseModel; // Default
    }

    // Delegate all IBakedModel methods to baseModel
    @Override public List<BakedQuad> getFaceQuads(EnumFacing side) { return baseModel.getFaceQuads(side); }
    @Override public List<BakedQuad> getGeneralQuads() { return baseModel.getGeneralQuads(); }
    @Override public boolean isAmbientOcclusion() { return baseModel.isAmbientOcclusion(); }
    @Override public boolean isGui3d() { return baseModel.isGui3d(); }
    @Override public boolean isBuiltInRenderer() { return baseModel.isBuiltInRenderer(); }
    @Override public TextureAtlasSprite getParticleTexture() { return baseModel.getParticleTexture(); }
    @Override public ItemCameraTransforms getItemCameraTransforms() { return baseModel.getItemCameraTransforms(); }
}