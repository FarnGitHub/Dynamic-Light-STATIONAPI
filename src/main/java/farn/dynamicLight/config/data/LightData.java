package farn.dynamicLight.config.data;

import com.google.gson.annotations.Expose;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.util.Identifier;

public class LightData {

    public String identifier;
    public int brightness;
    public boolean underwater;

    @Expose(serialize = false, deserialize = false)
    public boolean enabled;

    public Item getItem() {
        return ItemRegistry.INSTANCE.get(Identifier.of(identifier));
    }

    public String getTranslatedNames() {
        Item item = getItem();
        return item != null ?
                item.getTranslationKey()
                : identifier;
    }

}
