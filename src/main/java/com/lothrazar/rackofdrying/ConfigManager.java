package com.lothrazar.rackofdrying;

import com.lothrazar.library.config.ConfigTemplate;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class ConfigManager extends ConfigTemplate {

  private static ForgeConfigSpec CONFIG;
  public static BooleanValue TESTING;
  public static BooleanValue ALLOW_AUTOMATION;
  public static BooleanValue THATCH_BED_SET_SPAWN;
  static {
    final ForgeConfigSpec.Builder BUILDER = builder();
    BUILDER.comment("Mod settings").push(ModMain.MODID);
    ALLOW_AUTOMATION = BUILDER.comment("If true, hoppers/pipes can insert into and extract from the drying rack",
        "(extraction is still blocked while an item is actively drying). If false, the drying rack",
        "has no automation access at all and can only be used by hand.")
        .define("allowAutomation", true);
    THATCH_BED_SET_SPAWN = BUILDER.comment("If true, sleeping in a thatch bed sets your respawn point like a vanilla bed.",
        "If false, you can still sleep in it and skip the night, but it never changes your spawn point.")
        .define("thatchBedSetsSpawn", true);
    BUILDER.pop(); // one pop for every push
    CONFIG = BUILDER.build();
  }

  public ConfigManager() {
    CONFIG.setConfig(setup(ModMain.MODID));
  }
}
