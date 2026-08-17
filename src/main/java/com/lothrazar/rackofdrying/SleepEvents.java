package com.lothrazar.rackofdrying;

import com.lothrazar.rackofdrying.block.BlockThatchBed;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModMain.MODID)
public class SleepEvents {

  //SleepFinishedTimeEvent only fires once the night has actually been slept through (respecting the
  //multiplayer sleep-percentage gamerule) - unlike PlayerWakeUpEvent, which also fires for players who
  //got out of bed early or were kicked out by a nearby monster. That makes it the right hook for a
  //bed that should only consume itself after a real, successful night's sleep.
  @SubscribeEvent
  public static void onSleepFinished(SleepFinishedTimeEvent event) {
    ServerLevel level = (ServerLevel) event.getLevel();
    for (Player player : level.players()) {
      player.getSleepingPos()
          .filter(pos -> level.getBlockState(pos).getBlock() instanceof BlockThatchBed)
          .ifPresent(pos -> consumeBed(level, player, pos));
    }
  }

  private static void consumeBed(ServerLevel level, Player player, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    BlockPos footPos = state.getValue(BedBlock.PART) == BedPart.FOOT ? pos : pos.relative(state.getValue(BedBlock.FACING).getOpposite());
    BlockPos headPos = footPos.relative(state.getValue(BedBlock.FACING));
    breakSilently(level, footPos);
    breakSilently(level, headPos);
    //hand the item straight to the player instead of relying on the block's own loot table, which
    //would drop it at the (possibly two-tile-away) bed position instead of on the sleeper
    player.drop(new ItemStack(ModRegistry.ITHATCH_BED.get()), false);
  }

  private static void breakSilently(ServerLevel level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    if (state.getBlock() instanceof BlockThatchBed) {
      level.levelEvent(null, 2001, pos, Block.getId(state));
      level.removeBlock(pos, false);
    }
  }
}
