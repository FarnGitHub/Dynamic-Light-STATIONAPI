package farn.dynamicLight.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import farn.dynamicLight.world.Dispatcher;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public abstract class WorldMixin {

    @Shadow
    public abstract int getLightLevel(int x, int y, int z);

    @Shadow
    @Final
    public Dimension dimension;

    /**
     * @author AtomicStryker
     * @reason farnfarn02
     */
    @WrapMethod(method="getNaturalBrightness")
    public float getNaturalBrightnessWrap(int i, int j, int k, int l, Operation<Float> original)
    {
        int lightValue = getLightLevel(i, j, k);
        float torchLight = Dispatcher.getBrightness(i, j, k);
        if(lightValue < torchLight) {
            int floorValue = (int)java.lang.Math.floor(torchLight);
            return dimension.lightLevelToLuminance[floorValue];
        }
        return dimension.lightLevelToLuminance[lightValue];
    }

    /**
     * @author AtomicStryker
     * @reason farnfarn02
     */
    @WrapMethod(method="method_1782")
    public float method_1782Wrap(int i, int j, int k, Operation<Float> original)
    {
        int lightValue = getLightLevel(i, j, k);
        float torchLight = Dispatcher.getBrightness(i, j, k);
        if(lightValue < torchLight) {
            int floorValue = (int)java.lang.Math.floor(torchLight);
            return dimension.lightLevelToLuminance[floorValue];
        }
        return dimension.lightLevelToLuminance[lightValue];
    }

}
