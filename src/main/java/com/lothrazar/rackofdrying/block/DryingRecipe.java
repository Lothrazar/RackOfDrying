package com.lothrazar.rackofdrying.block;

import com.google.gson.JsonObject;
import com.lothrazar.rackofdrying.ModMain;
import com.lothrazar.rackofdrying.ModRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class DryingRecipe implements Recipe<RackContainer> {

  private static final int DEFAULT_DRY_TIME = 200; //10 seconds

  private final ResourceLocation id;
  private final Ingredient input;
  private final ItemStack result;
  private final int dryTime;

  public DryingRecipe(ResourceLocation id, Ingredient input, ItemStack result, int dryTime) {
    this.id = id;
    this.input = input;
    this.result = result;
    this.dryTime = dryTime > 0 ? dryTime : DEFAULT_DRY_TIME;
  }

  public Ingredient getInput() {
    return input;
  }

  public int getDryTime() {
    return dryTime;
  }

  @Override
  public boolean isSpecial() {
    return true;
  }

  @Override
  public boolean matches(RackContainer inv, Level level) {
    return input.test(inv.getItem(0));
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return true;
  }

  @Override
  public ItemStack assemble(RackContainer inv, RegistryAccess ra) {
    return getResultItem(ra);
  }

  @Override
  public ItemStack getResultItem(RegistryAccess ra) {
    return result.copy();
  }

  public ItemStack getResultForDisplay() {
    return result.copy();
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public RecipeType<?> getType() {
    return ModRegistry.DRYING_RECIPE_TYPE.get();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRegistry.DRYING_RECIPE_SERIALIZER.get();
  }

  public static class SerializeDryingRecipe implements RecipeSerializer<DryingRecipe> {

    @Override
    public DryingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
      try {
        //plain JsonElement (not forced to an object) so "input" can be a single {"item"/"tag":...} or an array of options
        Ingredient input = Ingredient.fromJson(json.get("input"));
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        int dryTime = GsonHelper.getAsInt(json, "time", DEFAULT_DRY_TIME);
        return new DryingRecipe(recipeId, input, result, dryTime);
      }
      catch (Exception e) {
        ModMain.LOGGER.error("Error loading drying recipe: " + recipeId, e);
        return null;
      }
    }

    @Override
    public DryingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
      Ingredient input = Ingredient.fromNetwork(buffer);
      ItemStack result = buffer.readItem();
      int dryTime = buffer.readVarInt();
      return new DryingRecipe(recipeId, input, result, dryTime);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, DryingRecipe recipe) {
      recipe.input.toNetwork(buffer);
      buffer.writeItem(recipe.result);
      buffer.writeVarInt(recipe.dryTime);
    }
  }
}
