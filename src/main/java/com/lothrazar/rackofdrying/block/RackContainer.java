package com.lothrazar.rackofdrying.block;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

/**
 * A plain Container used only for recipe matching (Recipe<C extends Container> requires it).
 * Deliberately kept off BlockEntityDryingRack itself: vanilla hoppers fall back to interacting
 * directly with any BlockEntity that implements Container when no IItemHandler capability is
 * exposed, so a Container-implementing block entity is still hopper-accessible even with the
 * capability removed. Keeping this as a separate object is what actually blocks hoppers.
 */
public class RackContainer implements Container {

  private final ItemStackHandler inventory;

  public RackContainer(ItemStackHandler inventory) {
    this.inventory = inventory;
  }

  @Override
  public int getContainerSize() {
    return inventory.getSlots();
  }

  @Override
  public boolean isEmpty() {
    return inventory.getStackInSlot(0).isEmpty();
  }

  @Override
  public ItemStack getItem(int slot) {
    return inventory.getStackInSlot(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int count) {
    return inventory.extractItem(slot, count, false);
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return inventory.extractItem(slot, inventory.getStackInSlot(slot).getCount(), false);
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    inventory.setStackInSlot(slot, stack);
  }

  @Override
  public boolean stillValid(Player player) {
    return true;
  }

  @Override
  public void clearContent() {
    inventory.setStackInSlot(0, ItemStack.EMPTY);
  }

  @Override
  public void setChanged() {
    //no-op: the real persistence/sync lives on BlockEntityDryingRack, this is just a recipe-matching view
  }
}
