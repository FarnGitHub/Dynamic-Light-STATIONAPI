package farn.dynamicLight.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import farn.dynamicLight.world.light_source.DynamicLightEngine;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(World.class)
public abstract class WorldMixin {

    @WrapOperation(method={"getNaturalBrightness", "method_1782"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getLightLevel(III)I"))
    public int dynamiclight_getLightLevel(World world, int x, int y, int z, Operation<Integer> original) {
        int lightValue  = original.call(world, x,y,z);
        int torchLight = DynamicLightEngine.getBrightness(x, y, z);
        return Math.max(lightValue, torchLight);
    }

}
