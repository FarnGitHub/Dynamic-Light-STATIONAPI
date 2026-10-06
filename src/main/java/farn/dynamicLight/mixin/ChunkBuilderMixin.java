package farn.dynamicLight.mixin;

import farn.dynamicLight.world.Dispatcher;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkBuilder.class)
public class ChunkBuilderMixin {

    @Inject(method = "rebuild", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/chunk/ChunkBuilder;built:Z", opcode = Opcodes.PUTFIELD))
    private void afterUpdateRenderer(CallbackInfo ci) {
        Dispatcher.clearCache();
    }

}
