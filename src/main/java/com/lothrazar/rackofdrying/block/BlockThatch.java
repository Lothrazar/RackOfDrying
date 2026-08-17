package com.lothrazar.rackofdrying.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BlockThatch extends Block {

  public BlockThatch(Properties properties) {
    super(properties);
  }

  @Override
  public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
    //same 80% fall damage reduction as a vanilla hay bale
    super.fallOn(level, state, pos, entity, fallDistance * 0.2F);
  }

  //FireBlock#setFlammable is private, so flammability is set by overriding these getters instead,
  //delegating to hay bale's already-registered values
  @Override
  public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
    return ((FireBlock) Blocks.FIRE).getFlammability(Blocks.HAY_BLOCK.defaultBlockState(), level, pos, direction);
  }

  @Override
  public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
    return ((FireBlock) Blocks.FIRE).getFireSpreadSpeed(Blocks.HAY_BLOCK.defaultBlockState(), level, pos, direction);
  }
}
