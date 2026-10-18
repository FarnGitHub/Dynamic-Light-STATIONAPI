package farn.dynamicLight.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import farn.dynamicLight.world.tick.DynamicLightEngine;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(World.class)
public class WorldMixin {

    @WrapOperation(
            method={"getNaturalBrightness", "method_1782"},
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;getLightLevel(III)I")
    )
    public int dynamicLight_getLightLevel(
            World world, int x, int y, int z, Operation<Integer> original
    ) {
        int lightLevel  = original.call(world, x,y,z);
        int torchLight = DynamicLightEngine.INST.getBrightness(
                world, x, y, z, lightLevel
        );
        return Math.max(lightLevel, torchLight);
    }

}
