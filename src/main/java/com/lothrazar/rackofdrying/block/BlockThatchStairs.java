package com.lothrazar.rackofdrying.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BlockThatchStairs extends StairBlock {

  public BlockThatchStairs(BlockState baseState, Properties properties) {
    super(baseState, properties);
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
