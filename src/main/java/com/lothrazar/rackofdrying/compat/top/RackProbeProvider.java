package com.lothrazar.rackofdrying.compat.top;

import com.lothrazar.rackofdrying.ModMain;
import com.lothrazar.rackofdrying.ModRegistry;
import com.lothrazar.rackofdrying.block.BlockEntityDryingRack;
import com.lothrazar.rackofdrying.block.DryingRecipe;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RackProbeProvider implements IProbeInfoProvider {

  @Override
  public ResourceLocation getID() {
    return new ResourceLocation(ModMain.MODID, "drying_rack_probe");
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level level, BlockState blockState, IProbeHitData data) {
    if (blockState.getBlock() != ModRegistry.DRYING_RACK.get()) {
      return;
    }
    BlockEntity tile = level.getBlockEntity(data.getPos());
    if (!(tile instanceof BlockEntityDryingRack rack)) {
      return;
    }
    if (rack.getStack().isEmpty()) {
      probeInfo.text(Component.translatable("tooltip." + ModMain.MODID + ".empty"));
      return;
    }
    DryingRecipe recipe = rack.getMatchingRecipe();
    if (recipe == null) {
      probeInfo.text(Component.translatable("tooltip." + ModMain.MODID + ".no_recipe"));
      return;
    }
    probeInfo.progress(rack.getTimer(), recipe.getDryTime());
  }
}
