package farn.dynamicLight.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import farn.dynamicLight.world.Dispatcher;
import net.minecraft.world.World;
import net.minecraft.world.WorldRegion;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldRegion.class)
public abstract class WorldRegionMixin {

    @Shadow
    private World world;

    @Shadow
    public abstract int getRawBrightness(int x, int y, int z);

    /**
     * @author AtomicStryker
     * @reason farnfarn02
     */
    @WrapMethod(method="getNaturalBrightness")
    public float getNaturalBrightnessWrap(int i, int j, int k, int l, Operation<Float> original)
    {

        int lightValue = getRawBrightness(i, j, k);
        float torchLight = Dispatcher.getBrightness(i, j, k);
        if(lightValue < torchLight)
        {
            int floorValue = (int)java.lang.Math.floor(torchLight);
            return world.dimension.lightLevelToLuminance[floorValue];
        }
        return world.dimension.lightLevelToLuminance[lightValue];
    }

    /**
     * @author AtomicStryker
     * @reason farnfarn02
     */
    @WrapMethod(method="method_1782")
    public float method_1782Wrap(int i, int j, int k, Operation<Float> original)
    {
        int lightValue = getRawBrightness(i, j, k);
        float torchLight = Dispatcher.getBrightness(i, j, k);
        if(lightValue < torchLight) {
            int floorValue = (int)java.lang.Math.floor(torchLight);
            return world.dimension.lightLevelToLuminance[floorValue];
        }
        return world.dimension.lightLevelToLuminance[lightValue];
    }

}
