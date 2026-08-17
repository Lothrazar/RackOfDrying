package com.lothrazar.rackofdrying.block;

import com.lothrazar.library.block.EntityBlockFlib;
import com.lothrazar.library.util.BlockstatesUtil;
import com.lothrazar.library.util.ItemStackUtil;
import com.lothrazar.rackofdrying.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockDryingRack extends EntityBlockFlib {

  //thin bar, 2px thick, 2px tall, spanning the full 16px width, mounted flush against the wall it's attached to, near the top of the block
  private static final VoxelShape SHAPE_NORTH = Block.box(0, 14, 14, 16, 16, 16);
  private static final VoxelShape SHAPE_SOUTH = Block.box(0, 14, 0, 16, 16, 2);
  private static final VoxelShape SHAPE_EAST = Block.box(0, 14, 0, 2, 16, 16);
  private static final VoxelShape SHAPE_WEST = Block.box(14, 14, 0, 16, 16, 16);

  public BlockDryingRack(Properties properties) {
    super(properties);
    registerDefaultState(defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(BlockStateProperties.HORIZONTAL_FACING);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
      case SOUTH:
        return SHAPE_SOUTH;
      case EAST:
        return SHAPE_EAST;
      case WEST:
        return SHAPE_WEST;
      case NORTH:
      default:
        return SHAPE_NORTH;
    }
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new BlockEntityDryingRack(pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, ModRegistry.TE_DRYING_RACK.get(), world.isClientSide ? BlockEntityDryingRack::clientTick : BlockEntityDryingRack::serverTick);
  }

  @Override
  public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
    if (entity != null) {
      // TODO: dont place if offset is air
      var facing = BlockstatesUtil.getFacingFromEntityHorizontal(pos, entity);
      if(world.getBlockState(pos.relative(facing)).isAir()) {
        world.setBlock(pos, state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing), 2);

      }
    }
  }

  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (hand != InteractionHand.MAIN_HAND) {
      return InteractionResult.PASS;
    }
    BlockEntityDryingRack tile = getTileEntity(world, pos);
    ItemStack heldItem = player.getItemInHand(hand);
    ItemStack rackStack = tile.getStack();
    if (rackStack.isEmpty() && !heldItem.isEmpty()) {
      //place one item onto the rack
      ItemStack toPlace = heldItem.copy();
      toPlace.setCount(1);
      tile.setStack(toPlace);
      ItemStackUtil.shrink(player, heldItem);
      player.setItemInHand(hand, heldItem);
      return InteractionResult.sidedSuccess(world.isClientSide);
    }
    if (!rackStack.isEmpty() && heldItem.isEmpty()) {
      //harvest whatever is currently on the rack
      tile.setStack(ItemStack.EMPTY);
      player.setItemInHand(hand, rackStack);
      return InteractionResult.sidedSuccess(world.isClientSide);
    }
    if (!rackStack.isEmpty() && !heldItem.isEmpty() && !tile.isProcessing()) {
      //hand is full but nothing is actively drying (e.g. a finished result just sitting there, or an
      //unmatched item that will never process) - pull it into the inventory instead of the hand
      tile.setStack(ItemStack.EMPTY);
      if (!player.getInventory().add(rackStack)) {
        player.drop(rackStack, false);
      }
      return InteractionResult.sidedSuccess(world.isClientSide);
    }
    return InteractionResult.PASS;
  }

  public BlockEntityDryingRack getTileEntity(Level world, BlockPos pos) {
    return (BlockEntityDryingRack) world.getBlockEntity(pos);
  }

  @Override
  public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
    //drop the contained item as its own entity, no matter how the block is broken (matches vanilla
    //chests spilling contents). The block itself is handled separately by its loot table.
    if (!state.is(newState.getBlock()) && world.getBlockEntity(pos) instanceof BlockEntityDryingRack tile && !tile.getStack().isEmpty()) {
      Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), tile.getStack());
    }
    super.onRemove(state, world, pos, newState, isMoving);
  }
}
