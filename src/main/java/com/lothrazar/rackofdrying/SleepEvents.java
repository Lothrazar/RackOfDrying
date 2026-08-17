package com.lothrazar.rackofdrying;

import com.lothrazar.rackofdrying.block.BlockThatchBed;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModMain.MODID)
public class SleepEvents {

  //fires from ServerPlayer#setRespawnPosition before the position is actually applied (and before the
  //"Respawn point set" message shows), so cancelling it here fully no-ops the spawn change for this one
  //bed while leaving vanilla beds/respawn anchors/every other spawn-setting source untouched
  @SubscribeEvent
  public static void onSetSpawn(PlayerSetSpawnEvent event) {
    BlockPos pos = event.getNewSpawn();
    if (!ConfigManager.THATCH_BED_SET_SPAWN.get() && pos != null && event.getEntity().level().getBlockState(pos).getBlock() instanceof BlockThatchBed) {
      event.setCanceled(true);
    }
  }

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
      //UPDATE_SUPPRESS_DROPS alone isn't enough: Level.setBlock strips that bit before it reaches the
      //neighbor-shape cascade (confirmed by decompiling Level#markAndNotifyBlock - it masks flags with
      //"& -34" before calling updateNeighbourShapes), so removing one half would still let BedBlock's
      //own updateShape cascade auto-clear the other half WITH a drop. UPDATE_KNOWN_SHAPE skips that
      //whole cascade block outright, which is what we actually want since we handle both halves
      //ourselves right here.
      level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
    }
  }
}
