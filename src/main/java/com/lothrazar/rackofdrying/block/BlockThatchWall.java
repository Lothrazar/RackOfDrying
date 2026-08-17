package com.lothrazar.rackofdrying.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BlockThatchWall extends WallBlock {

  public BlockThatchWall(Properties properties) {
    super(properties);
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
