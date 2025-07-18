package one.pkg.fnp_patcher;

import de.bsommerfeld.jshepherd.annotation.Comment;
import de.bsommerfeld.jshepherd.annotation.Key;
import de.bsommerfeld.jshepherd.core.ConfigurablePojo;
import de.bsommerfeld.jshepherd.core.ConfigurationLoader;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class ModConfig extends ConfigurablePojo<ModConfig> {
    public static final ModConfig INSTANCE = init();

    @Key("fix-issues128-enabled")
    @Comment("Fix Traffic Statistics")
    private boolean issues128 = false;
    @Key("fix-issues128-sync")
    @Comment("Run bandwidth statistics on sync thread, which is closer to Vanilla behavior.")
    private boolean issues128Sync = true;
    @Key("mixin-loginVT")
    @Comment("Replace player login validation thread with virtual thread")
    private boolean loginVT = true;
    @Key("mixin-textFilterVT")
    @Comment("Replace text filter thread with virtual thread")
    private boolean textFilterVT = true;
    @Key("mixin-utilVT")
    @Comment("Replace download thread with virtual thread")
    private boolean utilVT = true;
    @Key("mixin-bestVarLong")
    @Comment("Optimized VarLong implementation")
    private boolean bestVarLong = true;

    public ModConfig() {
    }

    private static ModConfig init() {
        Path configFile = FabricLoader.getInstance().getConfigDir().resolve("krypton_fnp.yaml");

        ModConfig config = ConfigurationLoader.load(configFile, ModConfig::new);
        config.save();
        config.reload();

        return config;
    }

    public boolean isIssues128() {
        return issues128;
    }

    public boolean isIssues128Sync() {
        return issues128Sync;
    }

    public boolean isLoginVT() {
        return loginVT;
    }

    public boolean isTextFilterVT() {
        return textFilterVT;
    }

    public boolean isUtilVT() {
        return utilVT;
    }

    public boolean isBestVarLong() {
        return bestVarLong;
    }
}

