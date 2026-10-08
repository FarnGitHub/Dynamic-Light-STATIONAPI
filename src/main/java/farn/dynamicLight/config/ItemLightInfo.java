package farn.dynamicLight.config;

import com.google.gson.annotations.Expose;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.util.Identifier;

public class ItemLightInfo {
    public String identifier;
    public int brightness;
    public int range;
    public boolean underwater;

    @Expose(deserialize = false, serialize = false)
    public boolean enabled = true;

    @Expose(deserialize = false, serialize = false)
    public String itemNames;

    public Item getItem() {
        return ItemRegistry.INSTANCE.get(Identifier.of(identifier));
    }

    public static ItemLightInfo of(int id) {
        return ItemLightInfoLoader.id2info.get(id);
    }
}
