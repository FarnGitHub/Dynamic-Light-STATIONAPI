package farn.dynamicLight.world.light_source;

import farn.dynamicLight.config.data.LightData;
import farn.dynamicLight.config.data.LightDatas;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class PlayerLightSource extends LightSource {
    private final PlayerEntity player;
    public int currentItemID = 0;

    public PlayerLightSource(PlayerEntity entity) {
        super(entity);
        this.player = entity;
    }

    public void tick() {
        ItemStack stack = player.getHand();
        int itemId = stack != null ? stack.itemId : 0;
        if (itemId != this.currentItemID){
            this.currentItemID = itemId;
            LightData data = LightDatas.INSTANCE.of(itemId);
            if (data != null && data.enabled) {
                this.setBrightness(data.brightness);
                this.setWorkUnderWater(data.underwater);
                this.setLit(true);
            } else {
                this.setLit(false);
            }
        }

        super.tick();
    }
}
