package com.lothrazar.rackofdrying.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockThatchBed extends BedBlock {

  //matches the bottom-slab model exactly (0-8 height, full footprint, no vanilla-style raised legs)
  private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 8, 16);

  public BlockThatchBed(Properties properties) {
    //color only matters to vanilla's per-color BlockEntityRenderer, which this block opts out of below
    super(DyeColor.BROWN, properties);
  }

  //vanilla's bed shape is a raised platform with small corner legs dropping to y=0 - fits the vanilla
  //mattress-on-legs model but not our flat slab-like one, so replace it with a plain matching box
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  //vanilla beds have no real geometry of their own - they're drawn entirely by a BlockEntityRenderer
  //that swaps in one of 16 wool-color textures read off this block entity. We only need one texture
  //(thatch), so skip the block entity/renderer machinery entirely and use a plain baked model instead.
  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return null;
  }

  //BedBlock normally forces ENTITYBLOCK_ANIMATED rendering (paired with the block entity above);
  //without switching back to MODEL here the bed would render as invisible geometry.
  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  //break the companion half silently (UPDATE_SUPPRESS_DROPS) before this half's own loot table runs,
  //so a two-block bed always yields exactly one item no matter which half was targeted - taking direct
  //control here instead of relying on BedBlock's own neighbor-update chain to suppress the second drop
  @Override
  public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    if (!level.isClientSide) {
      Direction toOther = state.getValue(PART) == BedPart.FOOT ? state.getValue(FACING) : state.getValue(FACING).getOpposite();
      BlockPos otherPos = pos.relative(toOther);
      BlockState otherState = level.getBlockState(otherPos);
      if (otherState.is(this)) {
        level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
        level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
      }
    }
    super.playerWillDestroy(level, pos, state, player);
  }

  @Override
  public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
    return ((FireBlock) Blocks.FIRE).getFlammability(Blocks.HAY_BLOCK.defaultBlockState(), level, pos, direction);
  }

  @Override
  public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
    return ((FireBlock) Blocks.FIRE).getFireSpreadSpeed(Blocks.HAY_BLOCK.defaultBlockState(), level, pos, direction);
  }
}
