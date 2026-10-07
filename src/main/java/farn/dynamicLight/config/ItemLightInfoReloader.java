package farn.dynamicLight.config;

import net.modificationstation.stationapi.api.resource.Resource;
import net.modificationstation.stationapi.api.resource.ResourceManager;
import net.modificationstation.stationapi.api.resource.SinglePreparationResourceReloader;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.profiler.Profiler;

import java.util.Map;

public class ItemLightInfoReloader extends SinglePreparationResourceReloader<Map<Identifier, Resource>> {

    public static final ItemLightInfoReloader INSTANCE = new ItemLightInfoReloader();

    @Override
    public Map<Identifier, Resource> prepare(ResourceManager manager, Profiler profiler) {
        return manager.findResources("dynamic_light/item", (name) -> name.path.endsWith(".json"));
    }

    @Override
    public void apply(Map<Identifier, Resource> prepared, ResourceManager manager, Profiler profiler) {
        ItemLightInfoLoader.reload(prepared);
    }
}
