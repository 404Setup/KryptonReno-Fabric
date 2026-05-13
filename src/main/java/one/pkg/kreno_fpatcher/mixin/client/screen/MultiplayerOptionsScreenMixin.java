package one.pkg.kreno_fpatcher.mixin.client.screen;

import net.minecraft.client.gui.screens.MultiplayerOptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

// TODO
@Mixin(MultiplayerOptionsScreen.class)
public abstract class MultiplayerOptionsScreenMixin extends Screen {
    protected MultiplayerOptionsScreenMixin(Component title) {
        super(title);
    }
}
