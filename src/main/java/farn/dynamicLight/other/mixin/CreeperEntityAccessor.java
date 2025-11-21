package farn.dynamicLight.other.mixin;

import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CreeperEntity.class)
public interface CreeperEntityAccessor {

	@Accessor("lastFuseTime")
	int ignitedTime();

}
