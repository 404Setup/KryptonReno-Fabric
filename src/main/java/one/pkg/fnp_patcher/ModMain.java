package one.pkg.fnp_patcher;

import net.fabricmc.api.ModInitializer;
import net.xstopho.resourceconfigapi.api.ConfigRegistry;
import one.pkg.fnp_patcher.util.PatcherModConfig;

public class ModMain implements ModInitializer {
    private static final String MOD_ID = "fnp_patcher";

    @Override
    public void onInitialize() {
        ConfigRegistry.register(PatcherModConfig.class, MOD_ID);
    }
}
