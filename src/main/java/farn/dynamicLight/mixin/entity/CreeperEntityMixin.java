package farn.dynamicLight.mixin.entity;

import farn.dynamicLight.world.light_source.LightSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin extends EntityMixin {

    @Shadow
    int fuseTime;

    @Override
    public boolean dynamiclight_shouldAdd() {
        return fuseTime > 0 || super.dynamiclight_shouldAdd();
    }

    @Override
    public LightSource dynamiclight_createLightSource() {
        return new LightSource((Entity)(Object)this, true);
    }
}
