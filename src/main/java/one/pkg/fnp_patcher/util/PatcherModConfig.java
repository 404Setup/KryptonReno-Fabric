package one.pkg.fnp_patcher.util;

import net.xstopho.resourceconfigapi.annotations.Config;
import net.xstopho.resourceconfigapi.annotations.ConfigEntry;
import net.xstopho.resourceconfigapi.api.ConfigType;

@Config(fileName = "patchermod_config", type = ConfigType.COMMON)
public class PatcherModConfig {

    @ConfigEntry(category = "General")
    public static boolean bestVarLong = true;

    @ConfigEntry(category = "General")
    public static boolean utilVT = true;

    @ConfigEntry(category = "General")
    public static boolean loginVT = true;

    @ConfigEntry(category = "General")
    public static boolean textFilterVT = true;
}
