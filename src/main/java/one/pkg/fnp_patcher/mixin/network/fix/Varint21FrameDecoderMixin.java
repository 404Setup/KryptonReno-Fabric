package one.pkg.fnp_patcher.mixin.network.fix;

import com.llamalad7.mixinextras.sugar.Local;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.BandwidthDebugMonitor;
import net.minecraft.network.VarInt;
import net.minecraft.network.Varint21FrameDecoder;
import one.pkg.fnp_patcher.ModConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(value = Varint21FrameDecoder.class, priority = 1500)
public class Varint21FrameDecoderMixin {
    @Unique
    private final ExecutorService fnp_patcher$executor = Executors.newSingleThreadExecutor(Thread.ofVirtual().factory());
    @Shadow
    @Final
    private BandwidthDebugMonitor monitor;

    @Inject(method = "decode", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private void fnp_patcher$decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list, CallbackInfo ci, @Local(name = {"length", "i"}, ordinal = 0) int length) {
        if (this.monitor != null) fnp_patcher$execute(length);
    }

    /*@TargetHandler(
            mixin = "me.steinborn.krypton.mixin.shared.network.pipeline.SplitterHandlerMixin",
            name = "decode",
            prefix = "handler"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private void doSomething(CallbackInfo originalCi, CallbackInfo ci, @Local int length) {
        if (this.monitor != null) krypton_FNP$execute(length);
    }*/

    @Unique
    private void fnp_patcher$execute(int l) {
        if (ModConfig.Fix.Issues128.isSync()) this.monitor.onReceive(l + VarInt.getByteSize(l));
        else this.fnp_patcher$executor.execute(() -> this.monitor.onReceive(l + VarInt.getByteSize(l)));
    }
}
