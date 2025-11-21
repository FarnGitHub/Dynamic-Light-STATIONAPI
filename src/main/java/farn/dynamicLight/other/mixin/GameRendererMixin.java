package farn.dynamicLight.other.mixin;

import farn.dynamicLight.other.config.Config;
import farn.dynamicLight.other.world.WorldTick;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Minecraft;

@Mixin(value = GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	private Minecraft client;
	
	@Inject(method = "onFrameUpdate", at = @At("TAIL"))
	public void onRender(float partialTicks, CallbackInfo ci) {
		if(client.world != null && !Config.resetLight) {
			WorldTick.OnTickInGame(client);
		}
	}

}
