package com.lothrazar.rackofdrying;

import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.lothrazar.rackofdrying.compat.top.RackProbeProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModMain.MODID)
public class ModMain {

  public static final String MODID = "rackofdrying";
  public static final Logger LOGGER = LogManager.getLogger();

  public ModMain() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    ModRegistry.BLOCKS.register(eventBus);
    ModRegistry.ITEMS.register(eventBus);
    ModRegistry.TILE_ENTITIES.register(eventBus);
    ModRegistry.RECIPE_TYPES.register(eventBus);
    ModRegistry.RECIPE_SERIALIZERS.register(eventBus);
    new ConfigManager();
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::interModEnqueue);
  }

  private void interModEnqueue(final InterModEnqueueEvent event) {
    //TheOneProbe integration: soft dependency via IMC handshake. Jade doesn't need this -
    //it auto-discovers @WailaPlugin annotated classes on the classpath instead.
    InterModComms.sendTo("theoneprobe", "getTheOneProbe", () -> (Function<ITheOneProbe, Void>) top -> {
      top.registerProvider(new RackProbeProvider());
      return null;
    });
  }

  private void setup(final FMLCommonSetupEvent event) {
    //flammability is set via getFlammability/getFireSpreadSpeed overrides on the thatch block classes
    //(FireBlock#setFlammable is private, so it can't be registered from here)
    event.enqueueWork(() -> {
      //same compost chance as wheat, since thatch is dried plant material
      ComposterBlock.COMPOSTABLES.put(ModRegistry.ITHATCH.get(), 0.65F);
    });
  }

  private void setupClient(final FMLClientSetupEvent event) {
    //for client side only setup
  }
}
