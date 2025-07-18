package one.pkg.fnp_patcher;

import net.fabricmc.api.ModInitializer;

public class ModMain implements ModInitializer {
    private static final String MOD_ID = "fnp_patcher";

    @Override
    public void onInitialize() {
        safetyCheck();
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
