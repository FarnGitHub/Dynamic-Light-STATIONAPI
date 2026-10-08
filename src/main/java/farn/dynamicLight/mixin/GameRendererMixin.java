package farn.dynamicLight.mixin;

import farn.dynamicLight.world.tick.WorldTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    private Minecraft client;

    @Inject(method="renderFrame", at = @At("RETURN"))
    public void dynamiclight_onUpdate(float tickDelta, long time, CallbackInfo ci) {
        WorldTick.tick(client.world);
    }
}
