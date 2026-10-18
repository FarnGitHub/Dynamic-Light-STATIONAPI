package farn.dynamicLight.world.light_source;

import farn.dynamicLight.config.data.LightData;
import farn.dynamicLight.config.data.LightDatas;
import net.minecraft.entity.ItemEntity;

import java.util.Optional;

public class ItemEntityLightSource extends LightSource{
    public ItemEntityLightSource(ItemEntity entity) {
        super(entity);
        LightData data = LightDatas.INSTANCE.of(entity.stack.itemId);
        if (LightDatas.INSTANCE.isEnabled(data)) {
            setBrightness(data.brightness);
            setWorkUnderWater(data.underwater);
            setLit(true);
        }
    }
}
