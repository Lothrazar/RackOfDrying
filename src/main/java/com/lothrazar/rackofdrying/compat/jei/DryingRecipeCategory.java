package com.lothrazar.rackofdrying.compat.jei;

import com.lothrazar.rackofdrying.ModRegistry;
import com.lothrazar.rackofdrying.block.DryingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class DryingRecipeCategory implements IRecipeCategory<DryingRecipe> {

  public static final ResourceLocation ID = new ResourceLocation(ModRegistry.DRYING_RECIPE_TYPE.getId().toString());
  public static final RecipeType<DryingRecipe> TYPE = new RecipeType<>(ID, DryingRecipe.class);
  private final IDrawable background;
  private final IDrawable icon;

  public DryingRecipeCategory(IGuiHelper helper) {
    //no custom art needed: a blank background plus the block's own item icon
    this.background = helper.createBlankDrawable(90, 20);
    this.icon = helper.createDrawableItemStack(new ItemStack(ModRegistry.IDRYING_RACK.get()));
  }

  @Override
  public IDrawable getIcon() {
    return icon;
  }

  @Override
  public IDrawable getBackground() {
    return background;
  }

  @Override
  public Component getTitle() {
    return Component.translatable(ModRegistry.DRYING_RACK.get().getDescriptionId());
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, DryingRecipe recipe, IFocusGroup focuses) {
    builder.addSlot(RecipeIngredientRole.INPUT, 4, 2).addIngredients(recipe.getInput());
    builder.addSlot(RecipeIngredientRole.OUTPUT, 70, 2).addItemStack(recipe.getResultForDisplay());
  }

  @Override
  public void draw(DryingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics ms, double mouseX, double mouseY) {
    //blank background, nothing extra to draw
  }

  @Override
  public RecipeType<DryingRecipe> getRecipeType() {
    return TYPE;
  }
}
