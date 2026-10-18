package farn.dynamicLight.mixin.entity;

import farn.dynamicLight.world.light_source.LightSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TntEntity.class)
public class TntEntityMixin extends EntityMixin {

    @Override
    public boolean dynamiclight_shouldAddToLightSource() {
        return true;
    }

    @Override
    public LightSource dynamiclight_createLightSource() {
        return new LightSource((Entity)(Object)this, true);
    }
}
