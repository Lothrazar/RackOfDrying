package com.lothrazar.rackofdrying.compat.jei;

import java.util.List;
import java.util.Objects;
import com.lothrazar.rackofdrying.ModMain;
import com.lothrazar.rackofdrying.ModRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class PluginJEI implements IModPlugin {

  private static final ResourceLocation ID = new ResourceLocation(ModMain.MODID, "jei");

  @Override
  public ResourceLocation getPluginUid() {
    return ID;
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
    registration.addRecipeCatalyst(new ItemStack(ModRegistry.IDRYING_RACK.get()), DryingRecipeCategory.TYPE);
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registry) {
    IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
    registry.addRecipeCategories(new DryingRecipeCategory(guiHelper));
  }

  @Override
  public void registerRecipes(IRecipeRegistration registry) {
    ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
    registry.addRecipes(DryingRecipeCategory.TYPE, List.copyOf(world.getRecipeManager().getAllRecipesFor(ModRegistry.DRYING_RECIPE_TYPE.get())));
  }
}
