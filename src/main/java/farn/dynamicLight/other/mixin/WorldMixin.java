package farn.dynamicLight.other.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import farn.dynamicLight.other.cache.LightCache;
import farn.dynamicLight.other.world.Dispatcher;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = World.class)
public class WorldMixin {

    public World world = (World)(Object)this;

    /**
     * @author AtomicStryker
     * @reason farnfarn02
     */
    @WrapMethod(method="getNaturalBrightness")
    public float getNaturalBrightnessWrap(int i, int j, int k, int l, Operation<Float> original)
    {
        float lc = LightCache.getLightValue(i, j, k);
        if(lc > l)
        {
            return lc;
        }

        int lightValue = world.getLightLevel(i, j, k);
        float torchLight = Dispatcher.getLightBrightness(world, i, j, k);
        if(lightValue < torchLight)
        {
            int floorValue = (int)java.lang.Math.floor(torchLight);
            if(floorValue==15)
            {
                return world.dimension.lightLevelToLuminance[15];
            }
            else
            {
                int ceilValue = (int)java.lang.Math.ceil(torchLight);
                float lerpValue = torchLight-floorValue;
                return (1.0f-lerpValue)*world.dimension.lightLevelToLuminance[floorValue]+lerpValue*world.dimension.lightLevelToLuminance[ceilValue];
            }
        }

        lc = world.dimension.lightLevelToLuminance[lightValue];
        LightCache.setLightValue(i, j, k, lc);
        return lc;
    }

    /**
     * @author AtomicStryker
     * @reason farnfarn02
     */
    @WrapMethod(method="method_1782")
    public float method_1782Wrap(int i, int j, int k, Operation<Float> original)
    {
        float lc = LightCache.getLightValue(i, j, k);
        if(lc >= 0)
        {
            return lc;
        }

        int lightValue = world.getLightLevel(i, j, k);
        float torchLight = Dispatcher.getLightBrightness(world, i, j, k);
        if(lightValue < torchLight)
        {
            int floorValue = (int)java.lang.Math.floor(torchLight);
            if(floorValue==15)
            {
                return world.dimension.lightLevelToLuminance[15];
            }
            else
            {
                int ceilValue = (int)java.lang.Math.ceil(torchLight);
                float lerpValue = torchLight-floorValue;
                return (1.0f-lerpValue)*world.dimension.lightLevelToLuminance[floorValue]+lerpValue*world.dimension.lightLevelToLuminance[ceilValue];
            }
        }

        lc = world.dimension.lightLevelToLuminance[lightValue];
        LightCache.setLightValue(i, j, k, lc);
        return lc;
    }

}
