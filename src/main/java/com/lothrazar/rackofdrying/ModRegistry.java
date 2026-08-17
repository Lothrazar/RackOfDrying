package com.lothrazar.rackofdrying;

import com.lothrazar.rackofdrying.block.BlockDryingRack;
import com.lothrazar.rackofdrying.block.BlockEntityDryingRack;
import com.lothrazar.rackofdrying.block.BlockThatch;
import com.lothrazar.rackofdrying.block.BlockThatchBed;
import com.lothrazar.rackofdrying.block.BlockThatchSlab;
import com.lothrazar.rackofdrying.block.BlockThatchStairs;
import com.lothrazar.rackofdrying.block.BlockThatchWall;
import com.lothrazar.rackofdrying.block.DryingRecipe;
import com.lothrazar.rackofdrying.block.DryingRecipe.SerializeDryingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRegistry {

  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MODID);
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModMain.MODID);
  public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ModMain.MODID);
  public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ModMain.MODID);
  public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ModMain.MODID);
  private static final ResourceKey<CreativeModeTab> TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(ModMain.MODID, "tab"));

  @SubscribeEvent
  public static void onCreativeModeTabRegister(RegisterEvent event) {
    event.register(Registries.CREATIVE_MODE_TAB, helper -> {
      helper.register(TAB, CreativeModeTab.builder().icon(() -> new ItemStack(IDRYING_RACK.get()))
          .title(Component.translatable("itemGroup." + ModMain.MODID))
          .displayItems((enabledFlags, populator) -> {
            for (RegistryObject<Item> entry : ITEMS.getEntries()) {
              populator.accept(entry.get());
            }
          }).build());
    });
  }

  //hardness between a sign (1.0) and a chest (2.5)
  public static final RegistryObject<Block> DRYING_RACK = BLOCKS.register("drying_rack", () -> new BlockDryingRack(Block.Properties.of().strength(1.5F).noOcclusion()));
  public static final RegistryObject<Item> IDRYING_RACK = ITEMS.register("drying_rack", () -> new BlockItem(DRYING_RACK.get(), new Item.Properties()));
  public static final RegistryObject<BlockEntityType<BlockEntityDryingRack>> TE_DRYING_RACK =
      TILE_ENTITIES.register("drying_rack", () -> BlockEntityType.Builder.of(BlockEntityDryingRack::new, DRYING_RACK.get()).build(null));
  public static final RegistryObject<RecipeType<DryingRecipe>> DRYING_RECIPE_TYPE = RECIPE_TYPES.register("drying", () -> new RecipeType<DryingRecipe>() {
    //empty, marker type
  });
  public static final RegistryObject<SerializeDryingRecipe> DRYING_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("drying", SerializeDryingRecipe::new);
  //2 food bars (4 nutrition, each drumstick icon = 2 nutrition)
  private static final FoodProperties JERKY_FOOD = new FoodProperties.Builder().nutrition(4).saturationMod(0.3F).build();
  public static final RegistryObject<Item> JERKY = ITEMS.register("jerky", () -> new Item(new Item.Properties().food(JERKY_FOOD)));
  //instabreak like a flower, but solid so it can catch falls like a hay bale
  public static final RegistryObject<Block> THATCH = BLOCKS.register("thatch", () -> new BlockThatch(Block.Properties.of().instabreak().sound(SoundType.GRASS)));
  public static final RegistryObject<Item> ITHATCH = ITEMS.register("thatch", () -> new BlockItem(THATCH.get(), new Item.Properties()));
  //stairs/slab/wall inherit thatch's instabreak + sound via Properties.copy
  public static final RegistryObject<Block> THATCH_STAIRS =
      BLOCKS.register("thatch_stairs", () -> new BlockThatchStairs(THATCH.get().defaultBlockState(), Block.Properties.copy(THATCH.get())));
  public static final RegistryObject<Item> ITHATCH_STAIRS = ITEMS.register("thatch_stairs", () -> new BlockItem(THATCH_STAIRS.get(), new Item.Properties()));
  public static final RegistryObject<Block> THATCH_SLAB = BLOCKS.register("thatch_slab", () -> new BlockThatchSlab(Block.Properties.copy(THATCH.get())));
  public static final RegistryObject<Item> ITHATCH_SLAB = ITEMS.register("thatch_slab", () -> new BlockItem(THATCH_SLAB.get(), new Item.Properties()));
  public static final RegistryObject<Block> THATCH_WALL = BLOCKS.register("thatch_wall", () -> new BlockThatchWall(Block.Properties.copy(THATCH.get())));
  public static final RegistryObject<Item> ITHATCH_WALL = ITEMS.register("thatch_wall", () -> new BlockItem(THATCH_WALL.get(), new Item.Properties()));
  //not instabreak like the rest of the thatch family - matches vanilla bed hardness/hitbox/sleep behavior
  public static final RegistryObject<Block> THATCH_BED =
      BLOCKS.register("thatch_bed", () -> new BlockThatchBed(Block.Properties.of().sound(SoundType.GRASS).strength(0.2F).noOcclusion()));
  public static final RegistryObject<Item> ITHATCH_BED = ITEMS.register("thatch_bed", () -> new BlockItem(THATCH_BED.get(), new Item.Properties()));
}
