package farn.dynamicLight.other.mixin;

import farn.dynamicLight.other.world.Dispatcher;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkBuilder.class)
public class ChunkBuilderMixin {
    @Inject(method = "rebuild", at = @At("TAIL"))
    private void afterUpdateRenderer(CallbackInfo ci) {
        Dispatcher.clearCacheAndResetPool();
    }

}
