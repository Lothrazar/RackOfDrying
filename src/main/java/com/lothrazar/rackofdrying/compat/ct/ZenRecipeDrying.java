package com.lothrazar.rackofdrying.compat.ct;

import org.openzen.zencode.java.ZenCodeType;
import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.lothrazar.rackofdrying.ModMain;
import com.lothrazar.rackofdrying.ModRegistry;
import com.lothrazar.rackofdrying.block.DryingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

@SuppressWarnings("rawtypes")
@ZenRegister
@ZenCodeType.Name("mods.rackofdrying.drying")
public class ZenRecipeDrying implements IRecipeManager {

  @Override
  public RecipeType getRecipeType() {
    return ModRegistry.DRYING_RECIPE_TYPE.get();
  }

  @SuppressWarnings("unchecked")
  @ZenCodeType.Method
  public void addRecipe(String name, IIngredient input, IItemStack output, int time) {
    name = fixRecipeName(name);
    DryingRecipe recipe = new DryingRecipe(new ResourceLocation("crafttweaker", name),
        input.asVanillaIngredient(),
        output.asImmutable().getInternal(), time);
    CraftTweakerAPI.apply(new ActionAddRecipe<DryingRecipe>(this, recipe, ""));
    ModMain.LOGGER.info("crafttweaker: Recipe loaded " + recipe.getId().toString());
  }

  @ZenCodeType.Method
  public void removeRecipe(String... names) {
    removeByName(names);
    ModMain.LOGGER.info("crafttweaker: Recipe removed " + names);
  }
}
