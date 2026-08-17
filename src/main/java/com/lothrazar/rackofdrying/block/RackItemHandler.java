package com.lothrazar.rackofdrying.block;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * Hopper/pipe-facing item handler for the drying rack. Insertion works normally (a hopper feeding
 * in a fresh ingredient to start a dry is fine), but extraction is blocked while isProcessing() is
 * true - only a finished/idle item (or nothing) can be pulled out. This is the automation-safe
 * counterpart to the identical rule already applied to the player's full-hand interaction in
 * BlockDryingRack.use().
 */
public class RackItemHandler implements IItemHandler {

  private final BlockEntityDryingRack tile;

  public RackItemHandler(BlockEntityDryingRack tile) {
    this.tile = tile;
  }

  @Override
  public int getSlots() {
    return 1;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return tile.getStack();
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if (stack.isEmpty() || !tile.getStack().isEmpty()) {
      return stack;
    }
    if (!simulate) {
      ItemStack toPlace = stack.copy();
      toPlace.setCount(1);
      tile.setStack(toPlace);
    }
    ItemStack remainder = stack.copy();
    remainder.shrink(1);
    return remainder;
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (tile.isProcessing() || tile.getStack().isEmpty()) {
      return ItemStack.EMPTY;
    }
    ItemStack current = tile.getStack();
    ItemStack extracted = current.copy();
    if (!simulate) {
      tile.setStack(ItemStack.EMPTY);
    }
    return extracted;
  }

  @Override
  public int getSlotLimit(int slot) {
    return 1;
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return true;
  }
}
