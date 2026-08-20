package com.lothrazar.rackofdrying.compat.top;

import java.util.function.Function;
import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraftforge.fml.InterModComms;

//kept as its own class file, deliberately: any class referencing TheOneProbe's types anywhere in its
//own bytecode (even inside a lambda body that never runs) fails to load if TOP isn't installed, because
//the JVM verifier resolves every method's referenced types at class-load time, not just invoked ones.
//ModMain must never reference this class directly - only call register() from behind a ModList.isLoaded
//check, so this class (and TOP's types) are never touched at all when TOP is absent.
public class TopCompat {

  public static void register() {
    InterModComms.sendTo("theoneprobe", "getTheOneProbe", () -> (Function<ITheOneProbe, Void>) top -> {
      top.registerProvider(new RackProbeProvider());
      return null;
    });
  }
}
