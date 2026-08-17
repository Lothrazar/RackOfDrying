package com.lothrazar.rackofdrying.compat.jade;

import com.lothrazar.rackofdrying.ModMain;
import com.lothrazar.rackofdrying.block.BlockDryingRack;
import com.lothrazar.rackofdrying.block.BlockEntityDryingRack;
import com.lothrazar.rackofdrying.block.DryingRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin(ModMain.MODID)
public class RackWailaPlugin implements IWailaPlugin, IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

  private static final ResourceLocation UID = new ResourceLocation(ModMain.MODID, "drying_rack");
  private static final String NBT_EMPTY = "empty";
  private static final String NBT_TIMER = "timer";
  private static final String NBT_DRY_TIME = "dryTime";

  @Override
  public ResourceLocation getUid() {
    return UID;
  }

  @Override
  public void registerClient(IWailaClientRegistration registration) {
    registration.registerBlockComponent(this, BlockDryingRack.class);
  }

  //appendTooltip only ever sees the client's locally cached block entity, which BlockEntityDryingRack
  //only re-syncs on inventory changes (item inserted/finished) - not every tick while timer counts up.
  //So the live progress has to come from this server-side hook instead, which Jade re-polls and pushes
  //to the client on its own schedule while a player is actually looking at the block.
  @Override
  public void register(IWailaCommonRegistration registration) {
    registration.registerBlockDataProvider(this, BlockEntityDryingRack.class);
  }

  @Override
  public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
    if (!(accessor.getBlockEntity() instanceof BlockEntityDryingRack rack)) {
      return;
    }
    tag.putBoolean(NBT_EMPTY, rack.getStack().isEmpty());
    tag.putInt(NBT_TIMER, rack.getTimer());
    DryingRecipe recipe = rack.getMatchingRecipe();
    tag.putInt(NBT_DRY_TIME, recipe == null ? 0 : recipe.getDryTime());
  }

  @Override
  public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
    CompoundTag data = accessor.getServerData();
    if (!data.contains(NBT_TIMER)) {
      return; //server data hasn't arrived yet (e.g. the very first frame this block is targeted)
    }
    if (data.getBoolean(NBT_EMPTY)) {
      tooltip.add(Component.translatable("tooltip." + ModMain.MODID + ".empty"));
      return;
    }
    int dryTime = data.getInt(NBT_DRY_TIME);
    if (dryTime <= 0) {
      tooltip.add(Component.translatable("tooltip." + ModMain.MODID + ".no_recipe"));
      return;
    }
    int percent = (int) (100L * data.getInt(NBT_TIMER) / dryTime);
    tooltip.add(Component.translatable("tooltip." + ModMain.MODID + ".drying", percent));
  }
}
