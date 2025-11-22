package farn.dynamicLight.other.mod;

import farn.dynamicLight.other.cache.ItemLightData;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.Map;

public class DynamicLightDefaultRegister implements DynamicLightEntryPoint{
    @Override
    public void onDefaultConfig(Map<Integer, ItemLightData> data) {
        data.put(Block.TORCH.id, new ItemLightData(Block.TORCH.id, 15, 31, -1, false));
        data.put(Item.GLOWSTONE_DUST.id, new ItemLightData(Item.GLOWSTONE_DUST.id, 10, 21, -1, true));
        data.put(Block.GLOWSTONE.id, new ItemLightData(Block.GLOWSTONE.id, 12, 25, -1, true));
        data.put(Block.LIT_REDSTONE_TORCH.id, new ItemLightData(Block.LIT_REDSTONE_TORCH.id, 10, 21, -1, true));
        data.put(Block.LIT_REDSTONE_ORE.id, new ItemLightData(Block.LIT_REDSTONE_ORE.id, 10, 21, -1, true));
    }
}
