package farn.dynamicLight.mixin.entity;

import farn.dynamicLight.impl.DynamicLightEntity;
import farn.dynamicLight.world.light_source.LightSource;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class EntityMixin implements DynamicLightEntity {
    @Shadow
    public boolean isOnFire() {
        throw new AssertionError();
    }

    @Override
    public boolean dynamiclight_shouldAddToLightSource() {
        return this.isOnFire();
    }

    @Override
    public LightSource dynamiclight_createLightSource() {
        return new LightSource((Entity)(Object)this);
    }
}
