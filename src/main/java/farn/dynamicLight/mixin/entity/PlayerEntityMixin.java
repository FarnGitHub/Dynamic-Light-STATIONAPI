package farn.dynamicLight.mixin.entity;

import farn.dynamicLight.world.light_source.LightSource;
import farn.dynamicLight.world.light_source.PlayerLightSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends EntityMixin {

    @Override
    public boolean dynamiclight_shouldAdd() {
        return true;
    }

    @Override
    public LightSource dynamiclight_createLightSource() {
        return new PlayerLightSource((PlayerEntity) (Object)this);
    }
}
