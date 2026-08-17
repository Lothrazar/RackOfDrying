package com.lothrazar.rackofdrying.block;

import com.lothrazar.rackofdrying.ConfigManager;
import com.lothrazar.rackofdrying.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class BlockEntityDryingRack extends BlockEntity {

  private static final String NBTINV = "inv";
  private static final String NBTTIMER = "timer";
  private static final String NBTRECIPE = "recipe";

  public final ItemStackHandler inventory = new ItemStackHandler(1) {

    @Override
    protected void onContentsChanged(int slot) {
      setChanged();
      syncToClient();
    }
  };
  //deliberately NOT implementing Container on this class - vanilla hoppers fall back to plain
  //Container access when no capability is present, bypassing the isProcessing() gate entirely.
  //See RackContainer (recipe matching only) and RackItemHandler (the actual hopper-facing capability,
  //which enforces the gate: no extraction while isProcessing() is true).
  private final RackContainer recipeView = new RackContainer(inventory);
  private final LazyOptional<IItemHandler> itemHandlerCap = LazyOptional.of(() -> new RackItemHandler(this));
  private int timer = 0;
  private ResourceLocation currentRecipeId;

  public BlockEntityDryingRack(BlockPos pos, BlockState state) {
    super(ModRegistry.TE_DRYING_RACK.get(), pos, state);
  }

  public ItemStack getStack() {
    return inventory.getStackInSlot(0);
  }

  public void setStack(ItemStack stack) {
    inventory.setStackInSlot(0, stack);
    //lock (or clear) the processing state synchronously, not just once per tick() - otherwise a
    //hopper extracting on the very same tick an ingredient is inserted could grab it in the one-tick
    //window before tick() next runs and notices it should be processing
    updateProcessingState();
  }

  //NOT kept in sync with the client every tick - the block entity update packet only fires on
  //inventory changes (see syncToClient()), not on every timer increment. TOP re-queries this live
  //server-side on each probe request so it's unaffected; Jade instead reads it via its own
  //IServerDataProvider hook (see RackWailaPlugin) rather than the client's stale cached copy.
  public int getTimer() {
    return timer;
  }

  public DryingRecipe getMatchingRecipe() {
    return findMatchingRecipe();
  }

  //true only while a recipe is actively timing down (tick() has locked onto a matching recipe id)
  public boolean isProcessing() {
    return currentRecipeId != null;
  }

  private void syncToClient() {
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

  //recomputes currentRecipeId (and resets timer on change) from the current stack. Safe to call
  //redundantly - a no-op if the stack still matches whatever it already locked onto.
  private void updateProcessingState() {
    if (level == null) {
      return; //not attached to a level yet; tick() will catch it once it is
    }
    ItemStack current = getStack();
    if (current.isEmpty()) {
      timer = 0;
      currentRecipeId = null;
      return;
    }
    DryingRecipe recipe = findMatchingRecipe();
    if (recipe == null) {
      timer = 0;
      currentRecipeId = null;
      return;
    }
    if (!recipe.getId().equals(currentRecipeId)) {
      currentRecipeId = recipe.getId();
      timer = 0;
    }
  }

  private void tick() {
    updateProcessingState();
    if (currentRecipeId == null) {
      return;
    }
    DryingRecipe recipe = findMatchingRecipe();
    timer++;
    if (timer >= recipe.getDryTime()) {
      setStack(recipe.assemble(recipeView, level.registryAccess()));
      //plays when an item finishes drying
      level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 1.0F);
    }
  }

  private DryingRecipe findMatchingRecipe() {
    for (DryingRecipe rec : level.getRecipeManager().getAllRecipesFor(ModRegistry.DRYING_RECIPE_TYPE.get())) {
      if (rec.matches(recipeView, level)) {
        return rec;
      }
    }
    return null;
  }

  public static void clientTick(Level level, BlockPos pos, BlockState state, BlockEntityDryingRack tile) {}

  public static void serverTick(Level level, BlockPos pos, BlockState state, BlockEntityDryingRack tile) {
    tile.tick();
  }

  @Override
  public void load(CompoundTag tag) {
    inventory.deserializeNBT(tag.getCompound(NBTINV));
    timer = tag.getInt(NBTTIMER);
    currentRecipeId = tag.contains(NBTRECIPE) ? ResourceLocation.tryParse(tag.getString(NBTRECIPE)) : null;
    super.load(tag);
  }

  @Override
  public void saveAdditional(CompoundTag tag) {
    tag.put(NBTINV, inventory.serializeNBT());
    tag.putInt(NBTTIMER, timer);
    if (currentRecipeId != null) {
      tag.putString(NBTRECIPE, currentRecipeId.toString());
    }
    super.saveAdditional(tag);
  }

  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag syncData = super.getUpdateTag();
    this.saveAdditional(syncData);
    return syncData;
  }

  @Override
  public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
    this.load(pkt.getTag());
    super.onDataPacket(net, pkt);
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public void invalidateCaps() {
    itemHandlerCap.invalidate();
    super.invalidateCaps();
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (cap == ForgeCapabilities.ITEM_HANDLER && ConfigManager.ALLOW_AUTOMATION.get()) {
      return itemHandlerCap.cast();
    }
    return super.getCapability(cap, side);
  }
}
