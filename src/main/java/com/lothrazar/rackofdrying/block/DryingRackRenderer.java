package com.lothrazar.rackofdrying.block;

import com.lothrazar.library.util.RenderTextUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DryingRackRenderer implements BlockEntityRenderer<BlockEntityDryingRack> {

  public DryingRackRenderer(BlockEntityRendererProvider.Context ctx) {}

  @Override
  public void render(BlockEntityDryingRack tile, float partialTicks, PoseStack ms, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
    ItemStack stack = tile.getStack();
    if (stack.isEmpty()) {
      return;
    }
    ms.pushPose();
    RenderTextUtil.alignRendering(ms, tile.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));
    ms.translate(0.5, 0.7, 0.15);
    final float scale = 0.4F;
    ms.scale(scale, scale, scale);
    Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, combinedLightIn, combinedOverlayIn, ms, buffer, tile.getLevel(), combinedLightIn);
    ms.popPose();
  }
}
