package farn.dynamicLight.mixin;

import farn.dynamicLight.world.tick.WorldTick;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method="setWorld(Lnet/minecraft/world/World;Ljava/lang/String;Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;world:Lnet/minecraft/world/World;", opcode = Opcodes.PUTFIELD))
    public void dynamicLight_setWorld(World world, String name, PlayerEntity player, CallbackInfo ci) {
        WorldTick.INST.setWorld(world);
    }

    @Inject(method="run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;onFrameUpdate(F)V"))
    public void dynamicLight_tick(CallbackInfo ci) {
        WorldTick.INST.tick();
    }
}
