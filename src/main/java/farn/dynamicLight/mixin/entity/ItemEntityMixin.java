package farn.dynamicLight.mixin.entity;

import farn.dynamicLight.config.data.LightDatas;
import farn.dynamicLight.world.light_source.ItemEntityLightSource;
import farn.dynamicLight.world.light_source.LightSource;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemEntity.class)
public class ItemEntityMixin extends EntityMixin {

    @Shadow
    public ItemStack stack;

    @Override
    public boolean dynamiclight_shouldAddToLightSource() {
        return stack != null && LightDatas.INSTANCE.isEnabled(stack.itemId)
                || super.dynamiclight_shouldAddToLightSource();
    }

    @Override
    public LightSource dynamiclight_createLightSource() {
        return new ItemEntityLightSource((ItemEntity) (Object)this);
    }
}
