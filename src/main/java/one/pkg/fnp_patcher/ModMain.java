package one.pkg.fnp_patcher;

import net.fabricmc.api.ModInitializer;
import net.xstopho.resourceconfigapi.api.ConfigRegistry;
import one.pkg.fnp_patcher.util.PatcherModConfig;

public class ModMain implements ModInitializer {
    private static final String MOD_ID = "fnp_patcher";

    @Override
    public void onInitialize() {
        safetyCheck();

        ConfigRegistry.register(PatcherModConfig.class, MOD_ID);
    }

    // This is a deliberate check.
    protected void safetyCheck() {
        try {
            Class.forName("org.bukkit.advancement.Advancement");
            throw new SecurityException("Unsupported mod detected: bukkit");
        } catch (ClassNotFoundException ignored) {
        }
    }
}
