package com.lothrazar.rackofdrying.compat.jade;

import com.lothrazar.rackofdrying.ModMain;
import com.lothrazar.rackofdrying.block.BlockDryingRack;
import com.lothrazar.rackofdrying.block.BlockEntityDryingRack;
import com.lothrazar.rackofdrying.block.DryingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin(ModMain.MODID)
public class RackWailaPlugin implements IWailaPlugin, IBlockComponentProvider {

  private static final ResourceLocation UID = new ResourceLocation(ModMain.MODID, "drying_rack");

  @Override
  public ResourceLocation getUid() {
    return UID;
  }

  @Override
  public void registerClient(IWailaClientRegistration registration) {
    registration.registerBlockComponent(this, BlockDryingRack.class);
  }

  @Override
  public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
    if (!(accessor.getBlockEntity() instanceof BlockEntityDryingRack rack)) {
      return;
    }
    if (rack.getStack().isEmpty()) {
      tooltip.add(Component.translatable("tooltip." + ModMain.MODID + ".empty"));
      return;
    }
    DryingRecipe recipe = rack.getMatchingRecipe();
    if (recipe == null) {
      tooltip.add(Component.translatable("tooltip." + ModMain.MODID + ".no_recipe"));
      return;
    }
    int percent = (int) (100L * rack.getTimer() / recipe.getDryTime());
    tooltip.add(Component.translatable("tooltip." + ModMain.MODID + ".drying", percent));
  }
}
