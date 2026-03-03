package farn.dynamicLight.cache;

import com.google.gson.annotations.Expose;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.Optional;

public class ItemLightData {
    public String identifier;
    public int brightness;
    public int range;
    public int timer;
    public boolean underwater;

    @Expose(deserialize = false, serialize = false)
    public boolean enabled = true;

    @Expose(deserialize = false, serialize = false)
    public String itemNames;

    public ItemLightData(String id, int brightness, int range, int deathAge, boolean underwater) {
        this.identifier = id;
        this.brightness = brightness;
        this.range = range;
        this.timer = deathAge;
        this.underwater = underwater;
    }

    public int getItemId() {
        Optional<Item> item = ItemRegistry.INSTANCE.getOrEmpty(Identifier.of(identifier));
        return item.map((itemBase) -> itemBase.id).orElse(-1);
    }
}
